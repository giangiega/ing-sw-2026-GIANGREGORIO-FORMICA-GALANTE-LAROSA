package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
public class BuildingCardUpperRowTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingCardUpperRow effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingCardUpperRow();
    }
    //Getters and choice methods
    @Test//Checking to see if the default chosenIndex is -1
    void defaultChosenIndex_isMinus1() {
        assertEquals(-1, effect.getChosenIndex(),
                "The chosenIndex should be -1 by default");
    }
    @Test//Checking to see if the default chosenIsBuilding is false
    void defaultChosenIsBuilding_isFalse() {
        assertFalse(effect.getChosenIsBuilding(),
                "The chosenIsBuilding boolean should be false by default");
    }
    @Test//Checking to see if setChoice updates the chosenIndex
    void setChoice_updatesIndex() {
        effect.setChoice(3, false);
        assertEquals(3, effect.getChosenIndex(),
                "The chosenIndex should have been set on 3");
    }
    @Test//Checking to see if setChoice updates the isBuilding boolean
    void setChoice_updatesIsBuilding() {
        effect.setChoice(0, true);
        assertTrue(effect.getChosenIsBuilding(),
                "The chosenIsBuilding boolean should have been updated to true");
    }
    @Test//Checking to see if requiresChoice returns true
    void requiresChoice_returnsTrue() {
        assertTrue(effect.requiresChoice(player, board),
                "requiresChoice should have returned true");
    }
    @Test//Checking to see if the building does anything without the player's choice
    void applyEndTurn_noChoiceSet_doesNothing() throws Exception {
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEndTurn(player, board, -1, false);
        assertEquals(foodBefore, player.getFood(),
                "The player should have not received any food because his choice was negative");
        assertEquals(ppBefore, player.getPP(),
                "The player should have not received any prestige points because his choice was negative");
    }
    @Test//Checking to see if the player pays the right amount of food for the chosen building
    void applyEndTurn_buildingChosen_paysCorrectFood() throws Exception {
        List<BuildingCard> upper = new ArrayList<>(board.getBuildingUpperRow());
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        int cost = target.getCost(player);
        player.gainFood(cost);
        int foodBefore = player.getFood();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(foodBefore - cost, player.getFood(),
                "The player should have payed the right amount of food for the chosen building");
    }
    @Test//Checking to see if the chosen building was added to the player's list
    void applyEndTurn_buildingChosen_addedToPlayerBuildings() throws Exception {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        player.gainFood(target.getCost(player));
        int buildingsBefore = player.getBuildingCards().size();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(buildingsBefore + 1, player.getBuildingCards().size(),
                "The building should have been added to the player's list");
    }
    @Test//Checking to see if the chosen building was removed from upper row
    void applyEndTurn_buildingChosen_removedFromUpperRow() throws Exception {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        player.gainFood(target.getCost(player));
        int rowSizeBefore = board.getBuildingUpperRow().size();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(rowSizeBefore - 1, board.getBuildingUpperRow().size(),
                "The chosen building should have been removed from the upper row");
    }
    @Test//Checking to see if the exception is triggered if the chosenIndex is out of bound
    void applyEndTurn_buildingIndexOutOfBounds_throwsException() {
        effect.setChoice(99, true);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, 99, true),
                "The OutOfBoundException was not thrown");
    }
    @Test//Checking to see if the exception is triggered if the player has not enough food
    void applyEndTurn_notEnoughFood_throwsException() {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        if (target.getCost(player) == 0) return; // skip se gratis

        while (player.getFood() > 0) player.payFood(1);

        effect.setChoice(0, true);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, 0, true),
                "The notEnoughFoodException was not thrown");
    }
    @Test//Checking to see if the chosen character was added to the player's tribe
    void applyEndTurn_characterChosen_addedToTribe() throws Exception {
        List<TribeCard> upper = new ArrayList<>(board.getUpperRow());
        int charIndex = findFirstCharacterIndex(upper);
        if (charIndex == -1) return;

        int tribeSizeBefore = countAllCharacters(player);
        effect.setChoice(charIndex, false);
        effect.applyEndTurn(player, board, charIndex, false);

        assertEquals(tribeSizeBefore + 1, countAllCharacters(player),
                "The character was not added to the player's tribe");
    }
    @Test//Checking to see if the chosen character was removed from the upper row
    void applyEndTurn_characterChosen_removedFromUpperRow() throws Exception {
        List<TribeCard> upper = board.getUpperRow();
        int charIndex = findFirstCharacterIndex(upper);
        if (charIndex == -1) return;

        int rowSizeBefore = board.getUpperRow().size();
        effect.setChoice(charIndex, false);
        effect.applyEndTurn(player, board, charIndex, false);

        assertEquals(rowSizeBefore - 1, board.getUpperRow().size(),
                "The chosen character was not removed from the upper row");
    }
    @Test
    void applyEndTurn_characterChosen_noFoodPaid() throws Exception {
        List<TribeCard> upper = new ArrayList<>(board.getUpperRow());
        int charIndex = findFirstCharacterIndex(upper);
        if (charIndex == -1) return;

        int foodBefore = player.getFood();
        effect.setChoice(charIndex, false);
        effect.applyEndTurn(player, board, charIndex, false);

        assertEquals(foodBefore, player.getFood());
    }

    @Test//Checking to see if an exception is thrown if the player choose an event card
    void applyEndTurn_eventCardChosen_throwsException() {
        List<TribeCard> upper = board.getUpperRow();
        int eventIndex = findFirstEventIndex(upper);
        if (eventIndex == -1) return; // nessun evento in fila: skip

        effect.setChoice(eventIndex, false);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, eventIndex, false),
                "The exception was not thrown after the player chose an event card");
    }

    private int findFirstCharacterIndex(List<TribeCard> row) {
        for (int i = 0; i < row.size(); i++)
            if (row.get(i) instanceof CharacterCard) return i;
        return -1;
    }

    private int findFirstEventIndex(List<TribeCard> row) {
        for (int i = 0; i < row.size(); i++)
            if (row.get(i) instanceof EventCard) return i;
        return -1;
    }

    private int countAllCharacters(Player p) {
        int total = 0;
        for (CharacterEnum type : CharacterEnum.values())
            total += p.getCharacterByType(type).size();
        return total;
    }
}