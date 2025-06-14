package com.sali.salicouture.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage());
        if (request.getRequestURI().contains("login")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
