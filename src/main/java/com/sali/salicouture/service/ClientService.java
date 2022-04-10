package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.service.dto.client.SaveClientDto;
import com.sali.salicouture.service.dto.enums.Message;

import java.util.List;

public interface ClientService {
    Message enregistrer(SaveClientDto saveClientDto);
    Client saveClientExcel(Client client);
    Message update(SaveClientDto saveClientDto, Long idClient);
    List<Client> lister();
}
