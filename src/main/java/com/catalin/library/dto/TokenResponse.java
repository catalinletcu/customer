package com.catalin.library.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TokenResponse {

    private String token;
    private LocalDateTime expireDateTime;
}
