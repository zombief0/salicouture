package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Mesure;
import com.norman.couture.service.dto.enums.Message;
import com.norman.couture.service.dto.mesure.SaveMesureDto;

import java.util.List;

public interface MesureService {
    Message ajouter(SaveMesureDto saveMesureDto, Long idCommande);
    Message update(SaveMesureDto saveMesure, Long idMesure);
    Message delete(Long idMesure);
    void saveMesures(List<Mesure> mesures, Client client);

    List<Mesure> listerMesuresStandards(Long idClient);
}
