package com.norman.couture.service.dto.mesure;

import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesureResponseDto {
    private Long id;
    private TypeVetement typeVetement;
    private TypeMesure typeMesure;
    private Double valeur;
}