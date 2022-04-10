package com.sali.salicouture.repositories;

import com.sali.salicouture.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByNomsIgnoreCaseAndPrenomsIgnoreCase(String nom, String prenom);
    Client findByNomsIgnoreCaseAndPrenomsIgnoreCase(String nom, String prenom);
}
