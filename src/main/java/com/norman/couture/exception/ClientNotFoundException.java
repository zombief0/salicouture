package com.norman.couture.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException() {
        super("Client introuvable");
    }
}
