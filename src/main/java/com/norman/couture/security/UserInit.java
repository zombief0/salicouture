package com.norman.couture.security;

import com.norman.couture.entities.Utilisateur;
import com.norman.couture.entities.enums.RoleUtilisateur;
import com.norman.couture.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInit implements CommandLineRunner {
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (utilisateurRepository.findAll().isEmpty()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setLogin("admin");
            utilisateur.setRoleUtilisateur(RoleUtilisateur.ADMIN);
            utilisateur.setActif(true);
            utilisateur.setPassword(passwordEncoder.encode("1234"));
            utilisateurRepository.save(utilisateur);

        }
    }
}
