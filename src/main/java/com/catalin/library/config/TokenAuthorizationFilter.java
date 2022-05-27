package com.catalin.library.config;

import com.catalin.library.entity.CustomerEntity;
import com.catalin.library.entity.LoginEntity;
import com.catalin.library.repository.CustomerRepository;
import com.catalin.library.repository.LoginRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final LoginRepository loginRepository;
    private final CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String token = extractToken(request.getHeader(AUTHORIZATION_HEADER));
            final LoginEntity loginEntity = getLoginEntity(token);

            validateTokenExpirationDate(loginEntity);
            setUpSpringAuthentication(loginEntity.getCustomerId());
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private LoginEntity getLoginEntity(String token) {
        return loginRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials."));
    }

    private String extractToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("Token expired.");
        }
        if (token.contains(BEARER)) {
            token = token.replace(BEARER, "");
        }

        return token;
    }

    private void validateTokenExpirationDate(LoginEntity loginEntity) {
        if (loginEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token expired");
        }
    }

    private void setUpSpringAuthentication(Integer customerId) {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        final CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        authorities.add(new SimpleGrantedAuthority(customerEntity.getRole()));
        final UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                customerEntity.getUsername(),
                customerEntity.getPassword(), authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }
}
