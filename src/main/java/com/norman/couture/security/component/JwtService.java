package com.norman.couture.security.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.norman.couture.entities.Utilisateur;
import com.norman.couture.security.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.norman.couture.security.component.AuthUtils.*;

@Service
public class JwtService {
    private final String secretKey;
    private final int jwtExpiration;

    public JwtService(@Value("${security.jwt.secret-key}") String secretKey,
                      @Value("${security.jwt.expiration-time}") int jwtExpiration) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
    }

    public AuthResponse generateToken(Authentication authentication){
        var user = (Utilisateur) authentication.getPrincipal();
        var expiredDate = Instant.now().plus(jwtExpiration, ChronoUnit.HOURS);
        String token = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withClaim(ROLE_CLAIM, ROLE_PREFIX + user.getRoleUtilisateur().name())
                .withClaim(LOGIN_CLAIM, user.getLogin())
                .withExpiresAt(expiredDate).sign(Algorithm.HMAC512(secretKey));
        return new AuthResponse(token, jwtExpiration * 60 * 60 * 1000 * 10, user.getRoleUtilisateur().name(), user.getLogin());
    }
}
