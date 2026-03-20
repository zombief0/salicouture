package com.norman.couture.exception;

import com.norman.couture.entities.enums.TypeVetement;

public class MesureStandardNotFoundException extends RuntimeException {
    public MesureStandardNotFoundException(TypeVetement typeVetement) {
        super("Mesure standard introuvable pour le type : " + typeVetement.name());
    }
}
