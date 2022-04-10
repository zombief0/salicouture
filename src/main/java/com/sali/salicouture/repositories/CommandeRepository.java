package com.sali.salicouture.repositories;

import com.sali.salicouture.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findAllByClient_IdOrderByDateCreationDesc(Long idClient);
}
