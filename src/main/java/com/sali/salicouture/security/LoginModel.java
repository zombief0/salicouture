package com.sali.salicouture.security;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class LoginModel {
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;
}
