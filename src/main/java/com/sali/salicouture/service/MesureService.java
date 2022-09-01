package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.service.dto.enums.Message;
import com.sali.salicouture.service.dto.mesure.SaveMesureDto;

import java.util.List;

public interface MesureService {
    Message ajouter(SaveMesureDto saveMesureDto, Long idCommande);
    Message update(SaveMesureDto saveMesure, Long idMesure);
    Message delete(Long idMesure);
    void saveMesures(List<Mesure> mesures, Client client);

    List<Mesure> listerMesuresStandards(Long idClient);
}
