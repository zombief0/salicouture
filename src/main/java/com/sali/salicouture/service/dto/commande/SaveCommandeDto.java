package com.sali.salicouture.service.dto.commande;

import lombok.Getter;
import lombok.Setter;

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
}
