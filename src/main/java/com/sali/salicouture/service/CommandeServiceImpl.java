package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.repositories.ClientRepository;
import com.sali.salicouture.repositories.CommandeRepository;
import com.sali.salicouture.service.dto.commande.SaveCommandeDto;
import com.sali.salicouture.service.dto.enums.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    @Override
    public Message createCommande(SaveCommandeDto saveCommandeDto, Long idClient) {
        Optional<Client> optionalClient = clientRepository.findById(idClient);
        if (optionalClient.isEmpty()) {
            return Message.CLIENT_NOT_EXIST;
        }

        Commande commande = new Commande();
        commande.setClient(optionalClient.get());
        commande.setDateCommande(saveCommandeDto.getDateCommande());
        commande.setDateRetrait(saveCommandeDto.getDateRetrait());
        commande.setAvance(saveCommandeDto.getAvance());
        commande.setCoutTotal(saveCommandeDto.getCoutTotal());
        commande.setReste(saveCommandeDto.getReste());
        commande.setNotes(saveCommandeDto.getNotes());
        commandeRepository.save(commande);
        return Message.SUCCES;
    }

    @Override
    public Message update(SaveCommandeDto saveCommande, Long idCommande) {
        Optional<Commande> optionalCommande = commandeRepository.findById(idCommande);
        if (optionalCommande.isEmpty()) {
            return Message.COMMANDE_NOT_EXIST;
        }
        Commande commande = optionalCommande.get();
        commande.setDateCommande(saveCommande.getDateCommande());
        commande.setAvance(saveCommande.getAvance());
        commande.setReste(saveCommande.getReste());
        commande.setDateRetrait(saveCommande.getDateRetrait());
        commande.setNotes(saveCommande.getNotes());
        commande.setCoutTotal(saveCommande.getCoutTotal());
        commandeRepository.save(commande);
        return Message.SUCCES;
    }

    @Override
    public List<Commande> listerAll() {
        return commandeRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreation", "dateModification"));
    }

    @Override
    public List<Commande> listerByClient(Long idClient) {
        return commandeRepository.findAllByClient_IdOrderByDateCreationDesc(idClient);
    }

    @Override
    public Commande saveCommandeExcel(Commande commande, Client client) {
        commande.setClient(client);
        return commandeRepository.save(commande);
    }
}
