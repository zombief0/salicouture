package com.norman.couture.service.dto.client;

import com.norman.couture.entities.enums.Sexe;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDto {
    private Long id;
    private String noms;
    private String prenoms;
    private String telephone;
    private String email;
    private String anniversaire;
    private boolean existMesureStandardPantalon;
    private boolean existMesureStandardChemise;
    private boolean existMesureStandardVeste;
    private Sexe sexe;
}