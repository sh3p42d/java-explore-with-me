package ru.practicum.client.config.exceptions;

public class ClientRequestException extends RuntimeException {
    public ClientRequestException(String message) {
        super(message);
    }
}
