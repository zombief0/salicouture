package com.norman.couture.exception;

public class MesureNotFoundException extends RuntimeException {
    public MesureNotFoundException() {
        super("Mesure introuvable");
    }
}
