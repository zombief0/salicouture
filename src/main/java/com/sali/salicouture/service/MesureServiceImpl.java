package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.repositories.ClientRepository;
import com.sali.salicouture.repositories.CommandeRepository;
import com.sali.salicouture.repositories.MesureRepository;
import com.sali.salicouture.service.dto.enums.Message;
import com.sali.salicouture.service.dto.mesure.SaveMesureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MesureServiceImpl implements MesureService {
    private final MesureRepository mesureRepository;
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    @Override
    public Message ajouter(SaveMesureDto saveMesureDto, Long idCommande) {
        Optional<Commande> optionalCommande = commandeRepository.findById(idCommande);
        if (optionalCommande.isEmpty()) {
            return Message.COMMANDE_NOT_EXIST;
        }
        Commande commande = optionalCommande.get();
        Mesure mesure = new Mesure();
        mesure.setTypeMesure(saveMesureDto.getTypeMesure());
        mesure.setCommande(commande);
        mesure.setTypeVetement(saveMesureDto.getTypeVetement());
        mesure.setValeur(saveMesureDto.getValeur());
        mesureRepository.save(mesure);
        return Message.SUCCES;
    }

    @Override
    public Message update(SaveMesureDto saveMesure, Long idMesure) {
        Optional<Mesure> optionalMesure = mesureRepository.findById(idMesure);
        if (optionalMesure.isEmpty()) {
            return Message.MESURE_NOT_EXIST;
        }
        Mesure mesure = optionalMesure.get();
        mesure.setValeur(saveMesure.getValeur());
        mesure.setTypeMesure(saveMesure.getTypeMesure());
        mesure.setTypeVetement(saveMesure.getTypeVetement());
        mesureRepository.save(mesure);
        return Message.SUCCES;
    }

    @Override
    public Message delete(Long idMesure) {
        Optional<Mesure> optionalMesure = mesureRepository.findById(idMesure);
        if (optionalMesure.isEmpty()) {
            return Message.MESURE_NOT_EXIST;
        }
        mesureRepository.deleteById(idMesure);
        return Message.SUCCES;
    }

    @Override
    public void saveMesures(List<Mesure> mesures, Client client) {
        if (!client.getExistMesureStandard()) {
            mesures.forEach(mesure -> mesure.setClient(client));
        }
        client.setExistMesureStandard(true);
        clientRepository.save(client);
        mesureRepository.saveAll(mesures);
    }
}
