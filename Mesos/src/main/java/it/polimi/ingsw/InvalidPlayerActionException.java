package it.polimi.ingsw;

/**
 * this is a checked exception because in the resolveAction method (Game) if a tile
 * has not any occupant we should move forward on the offerTrack
 */
public class InvalidPlayerActionException extends Exception {
    public InvalidPlayerActionException(String message) {
        super(message);
    }
}
