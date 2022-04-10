package com.sali.salicouture.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);


        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityProperties.SECRET))
                        .build().verify(headerToken.replace("Bearer ", ""));
                String username = decodedJWT.getSubject();
                String role = decodedJWT.getClaim("role").asString();
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(grantedAuthority));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (Exception e) {
                System.out.println("Token invalide " + e.getMessage());
            }
        }

        chain.doFilter(request, response);

    }
}
