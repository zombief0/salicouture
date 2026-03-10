package com.norman.couture.service.dto.commande;

import com.norman.couture.entities.enums.Echeance;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class SaveCommandeDto {
    private LocalDate dateCommande;
    private LocalDate dateRetrait;
    private Long coutTotal;
    private Long avance;
    private Long reste;
    private String notes;
    private boolean useMesureStandardPantalon;
    private boolean useMesureStandardVeste;
    private boolean useMesureStandardChemise;
    @NotNull
    private Echeance echeance;
}
