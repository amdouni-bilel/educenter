package com.beedigital.educenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserDTO user;
    private Long expiresIn;
}