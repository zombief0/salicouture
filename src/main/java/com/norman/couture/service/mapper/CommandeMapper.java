package com.norman.couture.service.mapper;

import com.norman.couture.entities.Commande;
import com.norman.couture.service.dto.commande.CommandeResponseDto;

import java.util.stream.Collectors;

public final class CommandeMapper {

    private CommandeMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CommandeResponseDto toResponseDto(Commande commande, boolean includeMesures) {
        if (commande == null) {
            return null;
        }

        CommandeResponseDto dto = new CommandeResponseDto();
        dto.setId(commande.getId());
        dto.setDateCommande(commande.getDateCommande());
        dto.setDateRetrait(commande.getDateRetrait());
        dto.setCoutTotal(commande.getCoutTotal());
        dto.setAvance(commande.getAvance());
        dto.setReste(commande.getReste());
        dto.setNotes(commande.getNotes());
        dto.setEcheance(commande.getEcheance());
        dto.setUseMesureStandardVeste(commande.isUseMesureStandardVeste());
        dto.setUseMesureStandardPantalon(commande.isUseMesureStandardPantalon());
        dto.setUseMesureStandardChemise(commande.isUseMesureStandardChemise());
        dto.setLivrer(commande.isLivrer());
        dto.setClientId(commande.getClient() != null ? commande.getClient().getId() : null);

        if (includeMesures && commande.getMesures() != null) {
            dto.setMesures(commande.getMesures().stream().map(MesureMapper::toResponseDto).collect(Collectors.toList()));
        }

        return dto;
    }
}