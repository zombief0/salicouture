package com.sali.salicouture.service.dto.mesure;

import com.sali.salicouture.entities.enums.TypeMesure;
import com.sali.salicouture.entities.enums.TypeVetement;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SaveMesureDto {
    @NotNull
    private TypeVetement typeVetement;

    @NotNull
    private TypeMesure typeMesure;

    @NotNull
    private Double valeur;
}
