package com.sali.salicouture.service.dto.commande;

import com.sali.salicouture.entities.enums.Echeance;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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
    private boolean useMesuresStandard;
    @NotNull
    private Echeance echeance;
}
