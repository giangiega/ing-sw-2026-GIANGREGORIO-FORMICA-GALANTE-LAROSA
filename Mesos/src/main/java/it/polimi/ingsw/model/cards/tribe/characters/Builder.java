package it.polimi.ingsw.model.cards.tribe.characters;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Daniele
 */
public class Builder extends CharacterCard {
    private int wingCount;
    private int endGamePP;

    public Builder(EraEnum era, int numPlayers, int wingCount, int endGamePP) {
        super(era, numPlayers);
        this.wingCount = wingCount;
        this.endGamePP = endGamePP;
    }

    /**
     * this method will add the new card to the tribe , update the total food discount of the player and trigger
     * the possible building effect
     * @param player
     * @param board
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        player.getCharacterByType(CharacterEnum.BUILDER).add(this);
        player.updateTotalFoodDiscountBuilder(this.wingCount);


    }

    public int getWingCount() {
        return wingCount;
    }
    public int getEndGamePP() {
        return endGamePP;
    }

    @Override
    public String toString() {
        return "Builder | Food discount: -" + getWingCount() + " | End-game PP: " + getEndGamePP() + " | Era: " + getEra();
    }
}
