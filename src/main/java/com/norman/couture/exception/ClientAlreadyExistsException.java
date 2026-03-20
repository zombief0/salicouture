package com.norman.couture.exception;

public class ClientAlreadyExistsException extends RuntimeException {
    public ClientAlreadyExistsException() {
        super("Un client avec ce nom et prénom existe déjà");
    }
}
