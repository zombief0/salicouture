package com.norman.couture.repositories;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.entities.enums.TypeVetement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {
    List<Mesure> findAllByClientAndTypeVetement(Client client, TypeVetement typeVetement);

    void deleteAllByCommandeAndClientIsNullAndTypeVetement(Commande commande, TypeVetement typeVetement);

    boolean existsByClientAndTypeVetement(Client client, TypeVetement typeVetement);

    List<Mesure> findAllByClient_Id(Long idClient);
}
