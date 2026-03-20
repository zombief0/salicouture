package com.norman.couture.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.norman.couture.security.component.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (headerToken == null || !headerToken.startsWith("Bearer ")) {
            log.warn("No JWT token found in request headers");
            chain.doFilter(request, response);
            return;
        }


        try {
            log.debug("JWT token found, processing authentication");
            DecodedJWT decodedJWT = jwtService.verifyToken(headerToken.replace("Bearer ", ""));
            String username = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(grantedAuthority));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.debug("JWT authentication successful for user: {}", username);
        } catch (Exception e) {
            log.error("Token invalide ", e);
        }


        chain.doFilter(request, response);

    }
}
