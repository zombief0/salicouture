package com.sali.salicouture.repositories;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.entities.Mesure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {
    List<Mesure> findAllByClient(Client client);

    void deleteAllByCommande(Commande commande);
}
