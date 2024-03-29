package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.service.dto.commande.CommandesClientDto;
import com.sali.salicouture.service.dto.commande.SaveCommandeDto;
import com.sali.salicouture.service.dto.enums.Message;

import java.util.List;

public interface CommandeService {
    Message createCommande(SaveCommandeDto saveCommandeDto, Long idClient);
    Message update(SaveCommandeDto saveCommande, Long idCommande);
    List<Commande> listerAll();
    CommandesClientDto listerByClient(Long idClient);
    Commande saveCommandeExcel(Commande commande, Client client);

    Commande getById(Long idCommande);

    void livrerNonLivrer(Long idCommande);
}
