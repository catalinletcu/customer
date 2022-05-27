package com.catalin.library.config;

import com.catalin.library.entity.CustomerEntity;
import com.catalin.library.entity.LoginEntity;
import com.catalin.library.repository.CustomerRepository;
import com.catalin.library.repository.LoginRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final LoginRepository loginRepository;
    private final CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = request.getHeader(HEADER);
        try {
            setUpSpringAuthentication(isAuthorized(token));
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private String isAuthorized(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("Token expired.");
        }
        if (token.contains(BEARER)) {
            token = token.replace(BEARER, "");
        }
        final Optional<LoginEntity> byToken = loginRepository.findByToken(token);
        final LoginEntity loginEntity = byToken.orElseThrow(() -> new BadCredentialsException("Bad credentials."));
        if (loginEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token expired");
        }
        return loginEntity.getCustomerUserName();
    }

    private void setUpSpringAuthentication(String customerUserName) {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        final CustomerEntity customerEntity = Optional.ofNullable(customerRepository.getByUserName(customerUserName))
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        authorities.add(new SimpleGrantedAuthority(customerEntity.getRole()));
        final UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                customerEntity.getUserName(),
                customerEntity.getPassword(), authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }
}
