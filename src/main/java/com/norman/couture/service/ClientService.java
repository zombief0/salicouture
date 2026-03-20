package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.exception.ClientAlreadyExistsException;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.service.dto.client.SaveClientDto;
import com.norman.couture.service.dto.client.ClientResponseDto;
import com.norman.couture.service.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    private final ClientRepository clientRepository;

    public void enregistrer(SaveClientDto saveClientDto) {
        SaveClientDto validatedSaveClientDto = requireNonNullParam(saveClientDto, "saveClientDto", "enregistrer");
        log.info("Création client demandée: noms={}, prenoms={}", validatedSaveClientDto.getNoms(), validatedSaveClientDto.getPrenoms());
        if (clientRepository.existsByNomsIgnoreCaseAndPrenomsIgnoreCase(validatedSaveClientDto.getNoms(), validatedSaveClientDto.getPrenoms())) {
            log.warn("Création client refusée: doublon détecté pour noms={}, prenoms={}", validatedSaveClientDto.getNoms(), validatedSaveClientDto.getPrenoms());
            throw new ClientAlreadyExistsException();
        }
        Client client = new Client();
        client.setAnniversaire(validatedSaveClientDto.getAnniversaire());
        client.setEmail(validatedSaveClientDto.getEmail());
        client.setSexe(validatedSaveClientDto.getSexe());
        if (validatedSaveClientDto.getNoms() != null) {
            client.setNoms(validatedSaveClientDto.getNoms().toUpperCase(Locale.ROOT));
        }

        if(validatedSaveClientDto.getPrenoms() != null) {
            client.setPrenoms(validatedSaveClientDto.getPrenoms().toUpperCase(Locale.ROOT));
        }

        client.setTelephone(validatedSaveClientDto.getTelephone());
        client.setEmail(validatedSaveClientDto.getEmail());
        Client savedClient = clientRepository.save(client);
        log.info("Client créé avec succès: id={}", savedClient.getId());
    }

    public Client saveClientExcel(Client client) {
        Client validatedClient = requireNonNullParam(client, "client", "saveClientExcel");
        log.info("Import Excel client: noms={}, prenoms={}", validatedClient.getNoms(), validatedClient.getPrenoms());
        Client clientBD = clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase(validatedClient.getNoms(), validatedClient.getPrenoms());
        if (clientBD != null) {
            log.info("Client déjà existant réutilisé pendant import Excel: id={}", clientBD.getId());
            return clientBD;
        }
        Client savedClient = clientRepository.save(validatedClient);
        log.info("Client créé pendant import Excel: id={}", savedClient.getId());
        return savedClient;
    }

    public void update(SaveClientDto saveClientDto, Long idClient) {
        SaveClientDto validatedSaveClientDto = requireNonNullParam(saveClientDto, "saveClientDto", "update");
        Long validatedIdClient = requireNonNullParam(idClient, "idClient", "update");
        log.info("Mise à jour client demandée: id={}", validatedIdClient);
        Client client = clientRepository.findById(validatedIdClient)
                .orElseThrow(ClientNotFoundException::new);

        Client clientBD = clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase(validatedSaveClientDto.getNoms(), validatedSaveClientDto.getPrenoms());
        if (clientBD != null && !Objects.equals(clientBD.getId(), client.getId())) {
            log.warn("Mise à jour client refusée: doublon pour id={}, noms={}, prenoms={}", validatedIdClient, validatedSaveClientDto.getNoms(), validatedSaveClientDto.getPrenoms());
            throw new ClientAlreadyExistsException();
        }
        client.setEmail(validatedSaveClientDto.getEmail());
        if (validatedSaveClientDto.getPrenoms() != null) {
            client.setPrenoms(validatedSaveClientDto.getPrenoms().toUpperCase(Locale.ROOT));
        }

        client.setTelephone(validatedSaveClientDto.getTelephone());
        client.setAnniversaire(validatedSaveClientDto.getAnniversaire());

        if (validatedSaveClientDto.getNoms() != null) {
            client.setNoms(validatedSaveClientDto.getNoms().toUpperCase(Locale.ROOT));
        }

        clientRepository.save(client);
        log.info("Client mis à jour avec succès: id={}", validatedIdClient);
    }

    public List<ClientResponseDto> lister() {
        List<Client> clients = clientRepository.findAll(Sort.by(Sort.Direction.ASC, "noms", "prenoms"));
        log.debug("Liste des clients récupérée: count={}", clients.size());
        return clients.stream().map(ClientMapper::toResponseDto).collect(Collectors.toList());
    }

    private <T> T requireNonNullParam(T value, String paramName, String methodName) {
        if (value == null) {
            log.warn("Paramètre null détecté: service=ClientService, methode={}, parametre={}", methodName, paramName);
            throw new IllegalArgumentException("Le paramètre '" + paramName + "' ne doit pas être null");
        }
        return value;
    }
}
