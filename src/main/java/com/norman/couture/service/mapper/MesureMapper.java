package com.norman.couture.service.mapper;

import com.norman.couture.entities.Mesure;
import com.norman.couture.service.dto.mesure.MesureResponseDto;

public final class MesureMapper {

    private MesureMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static MesureResponseDto toResponseDto(Mesure mesure) {
        if (mesure == null) {
            return null;
        }

        MesureResponseDto dto = new MesureResponseDto();
        dto.setId(mesure.getId());
        dto.setTypeVetement(mesure.getTypeVetement());
        dto.setTypeMesure(mesure.getTypeMesure());
        dto.setValeur(mesure.getValeur());
        return dto;
    }
}