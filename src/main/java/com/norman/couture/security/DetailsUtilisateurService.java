package com.norman.couture.security;


import com.norman.couture.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailsUtilisateurService implements UserDetailsService {
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return utilisateurRepository
                .findByLogin(s)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur " + s + "invalide"));
    }
}
