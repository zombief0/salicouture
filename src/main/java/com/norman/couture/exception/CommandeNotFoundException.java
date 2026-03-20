package com.norman.couture.exception;

public class CommandeNotFoundException extends RuntimeException {
    public CommandeNotFoundException() {
        super("Commande introuvable");
    }
}
