package com.catalin.library.controller;

import com.catalin.library.dto.TokenResponse;
import com.catalin.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<TokenResponse> createJWT(@RequestParam String username, @RequestParam String password) {

        final TokenResponse response = authService.generateJWT(username, password);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
