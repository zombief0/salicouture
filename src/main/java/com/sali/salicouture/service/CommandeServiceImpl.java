package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.entities.enums.TypeVetement;
import com.sali.salicouture.repositories.ClientRepository;
import com.sali.salicouture.repositories.CommandeRepository;
import com.sali.salicouture.repositories.MesureRepository;
import com.sali.salicouture.service.dto.commande.CommandesClientDto;
import com.sali.salicouture.service.dto.commande.SaveCommandeDto;
import com.sali.salicouture.service.dto.enums.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    private final MesureRepository mesureRepository;

    @Override
    public Message createCommande(SaveCommandeDto saveCommandeDto, Long idClient) {
        Optional<Client> optionalClient = clientRepository.findById(idClient);
        if (optionalClient.isEmpty()) {
            return Message.CLIENT_NOT_EXIST;
        }

        Client client = optionalClient.get();
        if (saveCommandeDto.isUseMesureStandardVeste() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.VESTE)) {

            return Message.MESURE_STANDARD_VESTE_NOT_EXIST;

        }

        if (saveCommandeDto.isUseMesureStandardPantalon() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.PANTALON)) {

            return Message.MESURE_STANDARD_PANTALON_NOT_EXIST;

        }

        if (saveCommandeDto.isUseMesureStandardChemise() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.CHEMISE)) {

            return Message.MESURE_STANDARD_CHEMISE_NOT_EXIST;

        }
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setDateCommande(saveCommandeDto.getDateCommande());
        commande.setDateRetrait(saveCommandeDto.getDateRetrait());
        commande.setAvance(saveCommandeDto.getAvance());
        commande.setCoutTotal(saveCommandeDto.getCoutTotal());
        commande.setReste(saveCommandeDto.getReste());
        commande.setNotes(saveCommandeDto.getNotes());
        commande.setEcheance(saveCommandeDto.getEcheance());
        commandeRepository.save(commande);

        if (saveCommandeDto.isUseMesureStandardVeste()) {
            commande.setUseMesureStandardVeste(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.VESTE);

            enregistrerNouvellesMesures(mesureList, commande);
        }

        if (saveCommandeDto.isUseMesureStandardPantalon()) {
            commande.setUseMesureStandardPantalon(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.PANTALON);

            enregistrerNouvellesMesures(mesureList, commande);
        }

        if (saveCommandeDto.isUseMesureStandardChemise()) {
            commande.setUseMesureStandardChemise(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.CHEMISE);

            enregistrerNouvellesMesures(mesureList, commande);
        }
        return Message.SUCCES;
    }



    @Override
    @Transactional
    public Message update(SaveCommandeDto saveCommande, Long idCommande) {
        Optional<Commande> optionalCommande = commandeRepository.findById(idCommande);
        if (optionalCommande.isEmpty()) {
            return Message.COMMANDE_NOT_EXIST;
        }
        Commande commande = optionalCommande.get();

        if (saveCommande.isUseMesureStandardVeste() != commande.isUseMesureStandardVeste()){
            if (saveCommande.isUseMesureStandardVeste()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.VESTE);
                if (mesureList.isEmpty()) {
                    return Message.MESURE_STANDARD_VESTE_NOT_EXIST;
                }

                commande.setUseMesureStandardVeste(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardVeste(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.VESTE);
            }
        }

        if (saveCommande.isUseMesureStandardPantalon() != commande.isUseMesureStandardPantalon()){
            if (saveCommande.isUseMesureStandardPantalon()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.PANTALON);
                if (mesureList.isEmpty()) {
                    return Message.MESURE_STANDARD_PANTALON_NOT_EXIST;
                }

                commande.setUseMesureStandardPantalon(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardPantalon(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.PANTALON);
            }
        }

        if (saveCommande.isUseMesureStandardChemise() != commande.isUseMesureStandardChemise()){
            if (saveCommande.isUseMesureStandardChemise()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.CHEMISE);
                if (mesureList.isEmpty()) {
                    return Message.MESURE_STANDARD_CHEMISE_NOT_EXIST;
                }

                commande.setUseMesureStandardChemise(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardChemise(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.CHEMISE);
            }
        }


        commande.setDateCommande(saveCommande.getDateCommande());
        commande.setAvance(saveCommande.getAvance());
        commande.setReste(saveCommande.getReste());
        commande.setDateRetrait(saveCommande.getDateRetrait());
        commande.setNotes(saveCommande.getNotes());
        commande.setCoutTotal(saveCommande.getCoutTotal());
        commande.setEcheance(saveCommande.getEcheance());
        commandeRepository.save(commande);
        return Message.SUCCES;
    }

    @Override
    public List<Commande> listerAll() {
        List<Commande> commandeList = commandeRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCommande"));
        commandeList.forEach(commande -> commande.setMesures(null));
        return commandeList;
    }

    @Override
    public CommandesClientDto listerByClient(Long idClient) {
        List<Commande> commandeList = commandeRepository.findAllByClient_IdOrderByDateRetraitDesc(idClient);
        Optional<Client> optionalClient = clientRepository.findById(idClient);
        if (optionalClient.isEmpty()) {
            return null;
        }
        CommandesClientDto commandesClientDto = new CommandesClientDto();
        Client client = optionalClient.get();
        commandesClientDto.setClient(client);
        if (commandeList.isEmpty()) {
            return commandesClientDto;
        }

        commandeList.forEach(commande -> commande.setClient(null));
        commandesClientDto.setCommandes(commandeList);
        return commandesClientDto;
    }

    @Override
    public Commande saveCommandeExcel(Commande commande, Client client) {
        commande.setClient(client);
        return commandeRepository.save(commande);
    }

    @Override
    public Commande getById(Long idCommande) {
        return commandeRepository.findById(idCommande).orElse(null);
    }

    @Override
    public void livrerNonLivrer(Long idCommande) {
        commandeRepository.findById(idCommande).ifPresent(commande -> {
            commande.setLivrer(!commande.isLivrer());
            commandeRepository.save(commande);
        });
    }

    private void enregistrerNouvellesMesures(List<Mesure> mesureList, Commande commande){
        mesureList.forEach(mesure -> {
            Mesure newMesure = mesure.copy();
            newMesure.setCommande(commande);
            mesureRepository.save(newMesure);
        });
        commandeRepository.save(commande);
    }
}
