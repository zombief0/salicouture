package com.norman.couture.service.excel;

import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;

public record MesureImportValue(TypeMesure typeMesure,
                                TypeVetement typeVetement,
                                Double valeur) {
}
