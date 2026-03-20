package com.norman.couture.service.dto.commande;

import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.service.dto.mesure.MesureResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandeResponseDto {
    private Long id;
    private LocalDate dateCommande;
    private LocalDate dateRetrait;
    private Long coutTotal;
    private Long avance;
    private Long reste;
    private String notes;
    private Echeance echeance;
    private boolean useMesureStandardVeste;
    private boolean useMesureStandardPantalon;
    private boolean useMesureStandardChemise;
    private boolean livrer;
    private Long clientId;
    private List<MesureResponseDto> mesures = new ArrayList<>();
}