package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.repositories.CommandeRepository;
import com.norman.couture.repositories.MesureRepository;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.MesureNotFoundException;
import com.norman.couture.service.dto.mesure.MesureResponseDto;
import com.norman.couture.service.dto.mesure.SaveMesureDto;
import com.norman.couture.service.mapper.MesureMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MesureService {
    private final MesureRepository mesureRepository;
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    public void ajouter(SaveMesureDto saveMesureDto, Long idCommande) {
        SaveMesureDto validatedSaveMesureDto = requireNonNullParam(saveMesureDto, "saveMesureDto", "ajouter");
        Long validatedIdCommande = requireNonNullParam(idCommande, "idCommande", "ajouter");
        log.info("Ajout mesure demandé pour commandeId={}", idCommande);
        Commande commande = commandeRepository.findById(validatedIdCommande)
                .orElseThrow(CommandeNotFoundException::new);
        Mesure mesure = new Mesure();
        mesure.setTypeMesure(validatedSaveMesureDto.getTypeMesure());
        mesure.setCommande(commande);
        mesure.setTypeVetement(validatedSaveMesureDto.getTypeVetement());
        mesure.setValeur(validatedSaveMesureDto.getValeur());
        Mesure savedMesure = mesureRepository.save(mesure);
        log.info("Mesure ajoutée avec succès: mesureId={}, commandeId={}", savedMesure.getId(), validatedIdCommande);
    }

    public void update(SaveMesureDto saveMesure, Long idMesure) {
        SaveMesureDto validatedSaveMesure = requireNonNullParam(saveMesure, "saveMesure", "update");
        Long validatedIdMesure = requireNonNullParam(idMesure, "idMesure", "update");
        log.info("Mise à jour mesure demandée: mesureId={}", idMesure);
        Mesure mesure = mesureRepository.findById(validatedIdMesure)
                .orElseThrow(MesureNotFoundException::new);
        mesure.setValeur(validatedSaveMesure.getValeur());
        mesure.setTypeMesure(validatedSaveMesure.getTypeMesure());
        mesure.setTypeVetement(validatedSaveMesure.getTypeVetement());
        mesureRepository.save(mesure);
        log.info("Mesure mise à jour avec succès: mesureId={}", validatedIdMesure);
    }

    public void delete(Long idMesure) {
        Long validatedIdMesure = requireNonNullParam(idMesure, "idMesure", "delete");
        log.info("Suppression mesure demandée: mesureId={}", idMesure);
        if (!mesureRepository.existsById(validatedIdMesure)) {
            log.warn("Suppression mesure impossible: mesureId={} introuvable", validatedIdMesure);
            throw new MesureNotFoundException();
        }
        mesureRepository.deleteById(validatedIdMesure);
        log.info("Mesure supprimée: mesureId={}", validatedIdMesure);
    }

    public void saveMesures(List<Mesure> mesures, Client client) {
        List<Mesure> validatedMesures = requireNonNullParam(mesures, "mesures", "saveMesures");
        Client validatedClient = requireNonNullParam(client, "client", "saveMesures");

        if (validatedMesures.isEmpty()) {
            log.debug("Aucune mesure à sauvegarder pour clientId={}", validatedClient.getId());
            return;
        }

        Map<TypeVetement, List<Mesure>> mesuresByType = initMesuresByType();
        for (Mesure mesure : validatedMesures) {
            if (mesure == null) {
                log.warn("Mesure nulle ignorée pendant saveMesures: clientId={}", validatedClient.getId());
                continue;
            }
            if (mesure.getTypeVetement() == null) {
                log.warn("Mesure sans typeVetement ignorée: clientId={}, mesureId={}", validatedClient.getId(), mesure.getId());
                continue;
            }
            mesuresByType.get(mesure.getTypeVetement()).add(mesure);
        }

        List<Mesure> mesuresToSave = new ArrayList<>();
        Commande commandeToUpdate = null;

        commandeToUpdate = processTypeMesures(TypeVetement.PANTALON, mesuresByType.get(TypeVetement.PANTALON), validatedClient, mesuresToSave, commandeToUpdate);
        commandeToUpdate = processTypeMesures(TypeVetement.VESTE, mesuresByType.get(TypeVetement.VESTE), validatedClient, mesuresToSave, commandeToUpdate);
        commandeToUpdate = processTypeMesures(TypeVetement.CHEMISE, mesuresByType.get(TypeVetement.CHEMISE), validatedClient, mesuresToSave, commandeToUpdate);

        if (commandeToUpdate != null) {
            commandeRepository.save(commandeToUpdate);
        }

        if (!mesuresToSave.isEmpty()) {
            mesureRepository.saveAll(mesuresToSave);
        }

        if (!mesuresToSave.isEmpty()) {
            clientRepository.save(validatedClient);
        }

    }

    public List<MesureResponseDto> listerMesuresStandards(Long idClient) {
        Long validatedIdClient = requireNonNullParam(idClient, "idClient", "listerMesuresStandards");
        List<Mesure> mesureList = mesureRepository.findAllByClient_Id(validatedIdClient);
        log.debug("Mesures standards récupérées: clientId={}, count={}", validatedIdClient, mesureList.size());
        return mesureList.stream().map(MesureMapper::toResponseDto).collect(Collectors.toList());
    }

    private Map<TypeVetement, List<Mesure>> initMesuresByType() {
        Map<TypeVetement, List<Mesure>> mesuresByType = new EnumMap<>(TypeVetement.class);
        mesuresByType.put(TypeVetement.PANTALON, new ArrayList<>());
        mesuresByType.put(TypeVetement.VESTE, new ArrayList<>());
        mesuresByType.put(TypeVetement.CHEMISE, new ArrayList<>());
        return mesuresByType;
    }

    private Commande processTypeMesures(TypeVetement typeVetement,
                                        List<Mesure> mesures,
                                        Client client,
                                        List<Mesure> mesuresToSave,
                                        Commande commandeToUpdate) {
        if (mesures.isEmpty() || hasStandardMesure(client, typeVetement)) {
            return commandeToUpdate;
        }

        mesures.forEach(mesure -> mesure.setClient(client));
        Commande resolvedCommande = commandeToUpdate != null ? commandeToUpdate : mesures.getFirst().getCommande();
        applyStandardFlags(client, resolvedCommande, typeVetement);
        mesuresToSave.addAll(mesures);
        return resolvedCommande;
    }

    private boolean hasStandardMesure(Client client, TypeVetement typeVetement) {
        return switch (typeVetement) {
            case PANTALON -> client.isExistMesureStandardPantalon();
            case VESTE -> client.isExistMesureStandardVeste();
            case CHEMISE -> client.isExistMesureStandardChemise();
        };
    }

    private void applyStandardFlags(Client client, Commande commande, TypeVetement typeVetement) {
        switch (typeVetement) {
            case PANTALON:
                commande.setUseMesureStandardPantalon(true);
                client.setExistMesureStandardPantalon(true);
                break;
            case VESTE:
                commande.setUseMesureStandardVeste(true);
                client.setExistMesureStandardVeste(true);
                break;
            case CHEMISE:
                commande.setUseMesureStandardChemise(true);
                client.setExistMesureStandardChemise(true);
                break;
            default:
                break;
        }
    }

    private <T> T requireNonNullParam(T value, String paramName, String methodName) {
        if (value == null) {
            log.warn("Paramètre null détecté: service=MesureService, methode={}, parametre={}", methodName, paramName);
            throw new IllegalArgumentException("Le paramètre '" + paramName + "' ne doit pas être null");
        }
        return value;
    }
}
