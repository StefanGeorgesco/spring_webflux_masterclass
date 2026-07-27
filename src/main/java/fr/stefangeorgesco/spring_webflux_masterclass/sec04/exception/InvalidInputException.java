package fr.stefangeorgesco.spring_webflux_masterclass.sec04.exception;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
