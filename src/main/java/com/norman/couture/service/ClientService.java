package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.service.dto.client.SaveClientDto;
import com.norman.couture.service.dto.enums.Message;

import java.util.List;

public interface ClientService {
    Message enregistrer(SaveClientDto saveClientDto);
    Client saveClientExcel(Client client);
    Message update(SaveClientDto saveClientDto, Long idClient);
    List<Client> lister();
}
