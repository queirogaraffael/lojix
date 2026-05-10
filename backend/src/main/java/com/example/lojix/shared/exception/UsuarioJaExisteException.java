package com.example.lojix.shared.exception;

public class UsuarioJaExisteException extends RuntimeException {
    public UsuarioJaExisteException(String message) {
        super(message);
    }
}
