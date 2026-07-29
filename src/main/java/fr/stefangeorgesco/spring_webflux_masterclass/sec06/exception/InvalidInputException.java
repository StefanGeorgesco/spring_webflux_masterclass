package fr.stefangeorgesco.spring_webflux_masterclass.sec06.exception;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
