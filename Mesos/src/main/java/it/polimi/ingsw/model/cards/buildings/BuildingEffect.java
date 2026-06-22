package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

/**
 * @author Giuse
 */
public abstract class BuildingEffect implements Serializable {
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called when a player acquires a building card
     */
    public void applyOnCardAdded(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Sustenance"
     */
    public void applyEventSustenance(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Hunt"
     */
    public void applyEventHunt(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Shaman Ritual"
     */
    public void applyEventShamanBonusStars(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * @param PP : amount of prestige points lost or won
     * @param win : true means that the player has won, false means that he has lost the event
     * This method is called during the following event: "Shaman Ritual"
     */
    public void applyEventShamanWinner(Player p, Board b, int PP, boolean win) {}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Cave Painting"
     */
    public void applyEventCavePainting(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * @param chosenIndex : index of the chosen card
     * @param chosenIsBuilding : true means that the chosen card is a building
     * @throws InvalidPlayerActionException
     * This method is called at the end of a turn
     */
    public void applyEndTurn(Player p, Board b, int chosenIndex, boolean chosenIsBuilding ) throws InvalidPlayerActionException {} //turn means round
    /**
     * @param p : player who has this building card
     * @param b  state of the board
     * This method is called only when the player's totem lands on a TurnOrderTile
     * slot that has a food bonus (never on the last slot). It is called once,
     * right when the totem is placed back, not at endRound.
     */
    public void applyTotemFoodBonus(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called at the end of the game
     */
    public void applyEndGame(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method tells if a BuildingEffect requires a player's interaction;
     * It is set on false by default; BuildingUpperRow will override it because it needs the player
     */
    public boolean requiresChoice(Player p, Board b) {
        return false;
    }
    /**
     * @param chosenIndex : index of the chosen card
     * @param chosenIsBuilding the chosen card is a building
     * This method is called by Game before applyEndTurn. It registers the player's choice
     * Used by the server
     */
    public void setChoice(int chosenIndex, boolean chosenIsBuilding) {}
    public int getChosenIndex(){return -1;}
    public boolean getChosenIsBuilding(){return false;}
}
