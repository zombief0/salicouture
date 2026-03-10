package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.service.dto.commande.CommandesClientDto;
import com.norman.couture.service.dto.commande.SaveCommandeDto;
import com.norman.couture.service.dto.enums.Message;

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
