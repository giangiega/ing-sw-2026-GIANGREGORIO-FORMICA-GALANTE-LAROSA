package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardUpperRowTest {

    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingCardUpperRow effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingCardUpperRow();
    }
    @Test//Checking if chosenIndex is set at -1 by default
    void defaultChosenIndex_isMinus1() {
        assertEquals(-1, effect.getChosenIndex(),
                "chosenIndex should be -1 by default");
    }
    @Test//Checking if chosenIsBuilding is set on false by default
    void defaultChosenIsBuilding_isFalse() {
        assertFalse(effect.getChosenIsBuilding(),
                "chosenIsBuilding should be false by default");
    }
    @Test//Checking to see if setChoice updates chosenIndex
    void setChoice_updatesIndex() {
        effect.setChoice(3, false);
        assertEquals(3, effect.getChosenIndex(),
                "chosenIndex should be 3");
    }
    @Test//Checking to see if setChoice updates chosenIsBuilding
    void setChoice_updatesIsBuilding() {
        effect.setChoice(0, true);
        assertTrue(effect.getChosenIsBuilding(),
                "now chosenIsBuilding should be true");
    }
    @Test//Checking to see if requiresChoice returns true
    void requiresChoice_returnsTrue() {
        assertTrue(effect.requiresChoice(player, board),
                "requiresChoice should return true");
    }
    @Test//Checking to see if the method works if the player decides to not use his building
    void applyEndTurn_noChoiceSet() throws Exception {
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEndTurn(player, board, -1, false);
        assertEquals(foodBefore, player.getFood(),
                "The player should have the same food because he chose not to use the building");
        assertEquals(ppBefore, player.getPP(),
                "The player should have the same prestige points because he chose not to use the building");
    }
    @Test//Checking to see if the building has been paid correctly
    void applyEndTurn_buildingChosen_paysCorrectFood() throws Exception {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        int cost = target.getCost(player);
        player.gainFood(cost);
        int foodBefore = player.getFood();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(foodBefore - cost, player.getFood(),
                "The player should have 0 food because he had to pay for the building");
    }
    @Test//Checking to see if the chosen building has been added to the player's deck
    void applyEndTurn_buildingChosen_addedToPlayerBuildings() throws Exception {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        player.gainFood(target.getCost(player));
        int buildingsBefore = player.getBuildingCards().size();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(buildingsBefore + 1, player.getBuildingCards().size(),
                "The player's deck should contain the chosen building");
    }
    @Test//Checking to see if the chosen building has been removed from the upper row
    void applyEndTurn_buildingChosen_removedFromUpperRow() throws Exception {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        player.gainFood(target.getCost(player));
        int rowSizeBefore = board.getBuildingUpperRow().size();

        effect.setChoice(0, true);
        effect.applyEndTurn(player, board, 0, true);

        assertEquals(rowSizeBefore - 1, board.getBuildingUpperRow().size(),
                "The upper row shouldn't contain the chosen building");
    }
    @Test//Checking to see if the exception us thrown
    void applyEndTurn_buildingIndexOutOfBounds_throwsException() {
        effect.setChoice(99, true);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, 99, true),
                "InvalidPlayerActionException should be thrown because the player chosen an out of bound index");
    }
    @Test//Checking to see if the exception us thrown
    void applyEndTurn_notEnoughFood_throwsException() {
        List<BuildingCard> upper = board.getBuildingUpperRow();
        if (upper.isEmpty()) return;
        BuildingCard target = upper.get(0);
        if (target.getCost(player) == 0) return; //Skip if free

        while (player.getFood() > 0) player.payFood(1);

        effect.setChoice(0, true);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, 0, true),
                "InvalidPlayerActionException should be thrown because the player doesn't have enough food");
    }
    @Test//Checking to see if the chosen character has been added to the player's tribe
    void applyEndTurn_characterChosen_addedToTribe() throws Exception {
        List<TribeCard> upper = board.getUpperRow();
        int charIndex = findFirstCharacterIndex(upper);
        if (charIndex == -1) return;

        int tribeSizeBefore = countAllCharacters(player);
        effect.setChoice(charIndex, false);
        effect.applyEndTurn(player, board, charIndex, false);

        assertEquals(tribeSizeBefore + 1, countAllCharacters(player),
                "The player's tribe should contain the chosen character");
    }
    @Test//Checking to see if the chosen character has been removed from upper row
    void applyEndTurn_characterChosen_removedFromUpperRow() throws Exception {
        List<TribeCard> upper = board.getUpperRow();
        int charIndex = findFirstCharacterIndex(upper);
        if (charIndex == -1) return;

        int rowSizeBefore = board.getUpperRow().size();
        effect.setChoice(charIndex, false);
        effect.applyEndTurn(player, board, charIndex, false);

        assertEquals(rowSizeBefore - 1, board.getUpperRow().size(),
                "The upper row shouldn't contain the chosen character");
    }
    @Test//Checking to see if the exception is thrown
    void applyEndTurn_eventCardChosen_throwsException() {
        List<TribeCard> upper = board.getUpperRow();
        int eventIndex = findFirstEventIndex(upper);
        if (eventIndex == -1) return; // nessun evento in fila: skip

        effect.setChoice(eventIndex, false);
        assertThrows(InvalidPlayerActionException.class,
                () -> effect.applyEndTurn(player, board, eventIndex, false),
                "InvalidPlayerActionException should be thrown because the player chosen an event card");
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
