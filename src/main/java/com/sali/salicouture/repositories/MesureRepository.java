package com.sali.salicouture.repositories;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.entities.enums.TypeMesure;
import com.sali.salicouture.entities.enums.TypeVetement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {
    List<Mesure> findAllByClientAndTypeVetement(Client client, TypeVetement typeVetement);

    void deleteAllByCommandeAndClientIsNullAndTypeVetement(Commande commande, TypeVetement typeVetement);

    boolean existsByClientAndTypeVetement(Client client, TypeVetement typeVetement);
}
