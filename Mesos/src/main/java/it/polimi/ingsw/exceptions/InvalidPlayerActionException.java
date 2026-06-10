package it.polimi.ingsw.exceptions;

/**
 * this is a checked exception because, for example, in the resolveAction method (Game) if a tile
 * has not any occupant we should move forward on the offerTrack
 */
public class InvalidPlayerActionException extends Exception {
    public InvalidPlayerActionException(String message) {
        super(message);
    }
}
