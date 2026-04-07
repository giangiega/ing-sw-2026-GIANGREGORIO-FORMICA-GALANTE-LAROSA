package it.polimi.ingsw;

public class InvalidPlayerActionException extends RuntimeException {
    public InvalidPlayerActionException(String message) {
        super(message);
    }
}
