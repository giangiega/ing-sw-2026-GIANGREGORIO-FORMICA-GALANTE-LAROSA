package it.polimi.ingsw;

public class InvalidStarCountException extends Exception {
    public InvalidStarCountException(int starCount) {
        super("StarCount not valid: " + starCount + ". It needs to be between 1 and 3.");
    }
}