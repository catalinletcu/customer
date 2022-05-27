package com.catalin.library.service;

import com.catalin.library.dto.TokenResponse;
import com.catalin.library.entity.CustomerEntity;
import com.catalin.library.entity.LoginEntity;
import com.catalin.library.exception.BadCredentialsException;
import com.catalin.library.repository.CustomerRepository;
import com.catalin.library.repository.LoginRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int VALIDITY_PERIOD = 30;

    private final CustomerRepository customerRepository;
    private final LoginRepository loginRepository;

    public TokenResponse generateJWT(String username, String password) {
        validateRequest(username, password);
        return createTokenResponse(username);
    }

    private void validateRequest(String username, String password) {
        final CustomerEntity customerEntity = getCustomerEntity(username);
        if (customerEntity == null) {
            throw new BadCredentialsException("Bad credentials.");
        }

        if (!customerEntity.getPassword().equals(password)) {
            throw new BadCredentialsException("Bad credentials.");
        }
    }

    private TokenResponse createTokenResponse(String username) {
        final CustomerEntity customerEntity = getCustomerEntity(username);
        final TokenResponse token = createToken();
        final LoginEntity loginEntity = new LoginEntity();
        loginEntity.setCustomerUserName(customerEntity.getUserName());
        loginEntity.setToken(token.getToken());
        loginEntity.setExpireDateTime(token.getExpireDateTime());

        loginRepository.save(loginEntity);

        return token;
    }

    private TokenResponse createToken() {
        final TokenResponse token = new TokenResponse();
        token.setToken(UUID.randomUUID().toString());
        token.setExpireDateTime(LocalDateTime.now().plusMinutes(VALIDITY_PERIOD));

        return token;
    }

    private CustomerEntity getCustomerEntity(String username) {
        return customerRepository.getByUserName(username);
    }
}
