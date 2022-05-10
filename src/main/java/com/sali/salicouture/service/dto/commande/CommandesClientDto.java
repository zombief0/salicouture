package com.sali.salicouture.service.dto.commande;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommandesClientDto {
    private List<Commande> commandes;
    private Client client;
}
