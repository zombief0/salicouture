package com.norman.couture.service.dto.mesure;

import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
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
