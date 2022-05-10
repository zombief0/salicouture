package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.repositories.ClientRepository;
import com.sali.salicouture.service.dto.client.SaveClientDto;
import com.sali.salicouture.service.dto.enums.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public Message enregistrer(SaveClientDto saveClientDto) {
        if (clientRepository.existsByNomsIgnoreCaseAndPrenomsIgnoreCase(saveClientDto.getNoms(), saveClientDto.getPrenoms())) {
            return Message.CLIENT_ALREADY_EXIST;
        }
        Client client = new Client();
        client.setAnniversaire(saveClientDto.getAnniversaire());
        client.setEmail(saveClientDto.getEmail());
        client.setNoms(saveClientDto.getNoms().toUpperCase(Locale.ROOT));
        client.setPrenoms(saveClientDto.getPrenoms().toUpperCase(Locale.ROOT));
        client.setTelephone(saveClientDto.getTelephone());
        client.setEmail(saveClientDto.getEmail());
        clientRepository.save(client);
        return Message.SUCCES;
    }

    @Override
    public Client saveClientExcel(Client client) {
        Client clientBD = clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase(client.getNoms(), client.getPrenoms());
        if (clientBD != null) {
            return clientBD;
        }
        clientRepository.save(client);
        return client;
    }

    @Override
    public Message update(SaveClientDto saveClientDto, Long idClient) {
        Optional<Client> optionalClient = clientRepository.findById(idClient);
        if (optionalClient.isEmpty()) {
            return Message.CLIENT_NOT_EXIST;
        }


        Client client = optionalClient.get();
        Client clientBD = clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase(saveClientDto.getNoms(), saveClientDto.getPrenoms());
        if (clientBD != null && !Objects.equals(clientBD.getId(), client.getId())) {
            return Message.CLIENT_ALREADY_EXIST;
        }
        client.setEmail(saveClientDto.getEmail());
        client.setPrenoms(saveClientDto.getPrenoms());
        client.setTelephone(saveClientDto.getTelephone());
        client.setAnniversaire(saveClientDto.getAnniversaire());
        client.setNoms(saveClientDto.getNoms());
        clientRepository.save(client);
        return Message.SUCCES;
    }

    @Override
    public List<Client> lister() {
        return clientRepository.findAll(Sort.by(Sort.Direction.ASC, "noms", "prenoms"));
    }
}
