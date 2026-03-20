package com.norman.couture.service.mapper;

import com.norman.couture.entities.Client;
import com.norman.couture.service.dto.client.ClientResponseDto;

public final class ClientMapper {

    private ClientMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ClientResponseDto toResponseDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(client.getId());
        dto.setNoms(client.getNoms());
        dto.setPrenoms(client.getPrenoms());
        dto.setTelephone(client.getTelephone());
        dto.setEmail(client.getEmail());
        dto.setAnniversaire(client.getAnniversaire());
        dto.setSexe(client.getSexe());
        dto.setExistMesureStandardChemise(client.isExistMesureStandardChemise());
        dto.setExistMesureStandardPantalon(client.isExistMesureStandardPantalon());
        dto.setExistMesureStandardVeste(client.isExistMesureStandardVeste());
        return dto;
    }
}