package it.polimi.ingsw.exceptions;

public class EmptyDeckException extends RuntimeException {
    public EmptyDeckException(String message) {
        super(message);
    }
}
