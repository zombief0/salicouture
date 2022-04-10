package com.sali.salicouture.restcontroller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sali.salicouture.entities.Utilisateur;
import com.sali.salicouture.security.AuthResponse;
import com.sali.salicouture.security.LoginModel;
import com.sali.salicouture.security.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/utilisateur")
@RequiredArgsConstructor
public class UtilisateurRestController {
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginModel loginModel) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginModel.getLogin(), loginModel.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Date expiredDate = new Date(System.currentTimeMillis() + SecurityProperties.EXPIRES_IN);

        String token = JWT.create()
                .withSubject(utilisateur.getId() + "")
                .withClaim("role", "ROLE_" + utilisateur.getRoleUtilisateur().toString())
                .withExpiresAt(expiredDate).sign(Algorithm.HMAC512(SecurityProperties.SECRET));
        return new AuthResponse(token, SecurityProperties.EXPIRES_IN, utilisateur.getRoleUtilisateur().name(), utilisateur.getLogin());
    }
}
