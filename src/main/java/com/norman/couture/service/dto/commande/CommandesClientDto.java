package com.norman.couture.service.dto.commande;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandesClientDto {
    private List<Commande> commandes = new ArrayList<>();
    private Client client;
}
