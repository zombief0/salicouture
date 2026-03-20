package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.repositories.CommandeRepository;
import com.norman.couture.repositories.MesureRepository;
import com.norman.couture.service.dto.commande.CommandesClientDto;
import com.norman.couture.service.dto.commande.CommandeResponseDto;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.MesureStandardNotFoundException;
import com.norman.couture.service.dto.commande.SaveCommandeDto;
import com.norman.couture.service.mapper.ClientMapper;
import com.norman.couture.service.mapper.CommandeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    private final MesureRepository mesureRepository;

    public void createCommande(SaveCommandeDto saveCommandeDto, Long idClient) {
        SaveCommandeDto validatedSaveCommandeDto = requireNonNullParam(saveCommandeDto, "saveCommandeDto", "createCommande");
        Long validatedIdClient = requireNonNullParam(idClient, "idClient", "createCommande");
        log.info("Création commande demandée pour clientId={}", validatedIdClient);
        Client client = clientRepository.findById(validatedIdClient)
                .orElseThrow(ClientNotFoundException::new);

        if (validatedSaveCommandeDto.isUseMesureStandardVeste() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.VESTE)) {
            log.warn("Création commande refusée: mesure standard VESTE absente pour clientId={}", validatedIdClient);
            throw new MesureStandardNotFoundException(TypeVetement.VESTE);
        }

        if (validatedSaveCommandeDto.isUseMesureStandardPantalon() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.PANTALON)) {
            log.warn("Création commande refusée: mesure standard PANTALON absente pour clientId={}", validatedIdClient);
            throw new MesureStandardNotFoundException(TypeVetement.PANTALON);
        }

        if (validatedSaveCommandeDto.isUseMesureStandardChemise() && !mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.CHEMISE)) {
            log.warn("Création commande refusée: mesure standard CHEMISE absente pour clientId={}", validatedIdClient);
            throw new MesureStandardNotFoundException(TypeVetement.CHEMISE);
        }
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setDateCommande(validatedSaveCommandeDto.getDateCommande());
        commande.setDateRetrait(validatedSaveCommandeDto.getDateRetrait());
        commande.setAvance(validatedSaveCommandeDto.getAvance());
        commande.setCoutTotal(validatedSaveCommandeDto.getCoutTotal());
        commande.setReste(validatedSaveCommandeDto.getReste());
        commande.setNotes(validatedSaveCommandeDto.getNotes());
        commande.setEcheance(validatedSaveCommandeDto.getEcheance());
        commandeRepository.save(commande);

        if (validatedSaveCommandeDto.isUseMesureStandardVeste()) {
            commande.setUseMesureStandardVeste(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.VESTE);

            enregistrerNouvellesMesures(mesureList, commande);
        }

        if (validatedSaveCommandeDto.isUseMesureStandardPantalon()) {
            commande.setUseMesureStandardPantalon(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.PANTALON);

            enregistrerNouvellesMesures(mesureList, commande);
        }

        if (validatedSaveCommandeDto.isUseMesureStandardChemise()) {
            commande.setUseMesureStandardChemise(true);
            List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.CHEMISE);

            enregistrerNouvellesMesures(mesureList, commande);
        }
        log.info("Commande créée avec succès: commandeId={}, clientId={}", commande.getId(), validatedIdClient);
    }

    @Transactional
    public void update(SaveCommandeDto saveCommande, Long idCommande) {
        SaveCommandeDto validatedSaveCommande = requireNonNullParam(saveCommande, "saveCommande", "update");
        Long validatedIdCommande = requireNonNullParam(idCommande, "idCommande", "update");
        log.info("Mise à jour commande demandée: commandeId={}", validatedIdCommande);
        Commande commande = commandeRepository.findById(validatedIdCommande)
                .orElseThrow(CommandeNotFoundException::new);

        if (validatedSaveCommande.isUseMesureStandardVeste() != commande.isUseMesureStandardVeste()){
            if (validatedSaveCommande.isUseMesureStandardVeste()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.VESTE);
                if (mesureList.isEmpty()) {
                    log.warn("Mise à jour commande refusée: mesure standard VESTE absente pour commandeId={}", validatedIdCommande);
                    throw new MesureStandardNotFoundException(TypeVetement.VESTE);
                }

                commande.setUseMesureStandardVeste(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardVeste(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.VESTE);
            }
        }

        if (validatedSaveCommande.isUseMesureStandardPantalon() != commande.isUseMesureStandardPantalon()){
            if (validatedSaveCommande.isUseMesureStandardPantalon()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.PANTALON);
                if (mesureList.isEmpty()) {
                    log.warn("Mise à jour commande refusée: mesure standard PANTALON absente pour commandeId={}", validatedIdCommande);
                    throw new MesureStandardNotFoundException(TypeVetement.PANTALON);
                }

                commande.setUseMesureStandardPantalon(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardPantalon(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.PANTALON);
            }
        }

        if (validatedSaveCommande.isUseMesureStandardChemise() != commande.isUseMesureStandardChemise()){
            if (validatedSaveCommande.isUseMesureStandardChemise()) {
                List<Mesure> mesureList = mesureRepository.findAllByClientAndTypeVetement(commande.getClient(), TypeVetement.CHEMISE);
                if (mesureList.isEmpty()) {
                    log.warn("Mise à jour commande refusée: mesure standard CHEMISE absente pour commandeId={}", validatedIdCommande);
                    throw new MesureStandardNotFoundException(TypeVetement.CHEMISE);
                }

                commande.setUseMesureStandardChemise(true);
                enregistrerNouvellesMesures(mesureList, commande);
            } else {
                commande.setUseMesureStandardChemise(false);
                mesureRepository.deleteAllByCommandeAndClientIsNullAndTypeVetement(commande, TypeVetement.CHEMISE);
            }
        }


        commande.setDateCommande(validatedSaveCommande.getDateCommande());
        commande.setAvance(validatedSaveCommande.getAvance());
        commande.setReste(validatedSaveCommande.getReste());
        commande.setDateRetrait(validatedSaveCommande.getDateRetrait());
        commande.setNotes(validatedSaveCommande.getNotes());
        commande.setCoutTotal(validatedSaveCommande.getCoutTotal());
        commande.setEcheance(validatedSaveCommande.getEcheance());
        commandeRepository.save(commande);
        log.info("Commande mise à jour avec succès: commandeId={}", validatedIdCommande);
    }

    public List<CommandeResponseDto> listerAll() {
        List<Commande> commandeList = commandeRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCommande"));
        log.debug("Liste des commandes récupérée: count={}", commandeList.size());
        return commandeList.stream().map(commande -> CommandeMapper.toResponseDto(commande, false)).collect(Collectors.toList());
    }

    public CommandesClientDto listerByClient(Long idClient) {
        Long validatedIdClient = requireNonNullParam(idClient, "idClient", "listerByClient");
        List<Commande> commandeList = commandeRepository.findAllByClient_IdOrderByDateRetraitDesc(validatedIdClient);
        Client client = clientRepository.findById(validatedIdClient)
                .orElseThrow(ClientNotFoundException::new);
        CommandesClientDto commandesClientDto = new CommandesClientDto();
        commandesClientDto.setClient(ClientMapper.toResponseDto(client));
        if (commandeList.isEmpty()) {
            return commandesClientDto;
        }

        commandesClientDto.setCommandes(commandeList.stream().map(commande -> CommandeMapper.toResponseDto(commande, false)).collect(Collectors.toList()));
        log.debug("Commandes client récupérées: clientId={}, count={}", validatedIdClient, commandeList.size());
        return commandesClientDto;
    }

    public Commande saveCommandeExcel(Commande commande, Client client) {
        Commande validatedCommande = requireNonNullParam(commande, "commande", "saveCommandeExcel");
        Client validatedClient = requireNonNullParam(client, "client", "saveCommandeExcel");
        validatedCommande.setClient(validatedClient);
        Commande savedCommande = commandeRepository.save(validatedCommande);
        log.debug("Commande importée depuis Excel: commandeId={}, clientId={}", savedCommande.getId(), client.getId());
        return savedCommande;
    }

    public CommandeResponseDto getById(Long idCommande) {
        Long validatedIdCommande = requireNonNullParam(idCommande, "idCommande", "getById");
        Commande commande = commandeRepository.findById(validatedIdCommande)
                .orElseThrow(CommandeNotFoundException::new);
        return CommandeMapper.toResponseDto(commande, true);
    }

    public void livrerNonLivrer(Long idCommande) {
        Long validatedIdCommande = requireNonNullParam(idCommande, "idCommande", "livrerNonLivrer");
        log.info("Toggle livraison demandé: commandeId={}", validatedIdCommande);
        commandeRepository.findById(validatedIdCommande).ifPresent(commande -> {
            commande.setLivrer(!commande.isLivrer());
            commandeRepository.save(commande);
            log.info("Statut livraison mis à jour: commandeId={}, livrer={}", validatedIdCommande, commande.isLivrer());
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

    private <T> T requireNonNullParam(T value, String paramName, String methodName) {
        if (value == null) {
            log.warn("Paramètre null détecté: service=CommandeService, methode={}, parametre={}", methodName, paramName);
            throw new IllegalArgumentException("Le paramètre '" + paramName + "' ne doit pas être null");
        }
        return value;
    }
}
