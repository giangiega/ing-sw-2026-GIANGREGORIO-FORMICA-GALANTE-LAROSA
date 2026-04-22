package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingDiscountFoodTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    @BeforeEach
    public void setUp(){
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
    }
    @Test
    void check(){
        assertEquals(CharacterEnum.GATHERER,
                new BuildingDiscountFood(CharacterEnum.GATHERER).getCharacterType());
    }
    @Test//Checking to see if the building gives the right discount during the event sustenance: 1 character
    void applyEventSustenance_oneCharacter() {
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        new BuildingDiscountFood(CharacterEnum.ARTIST).applyEventSustenance(player, board);
        assertEquals(foodBefore + 1, player.getFood(),
                "The building should have given 1 food because the player has 1 right character");
    }
    @Test//Checking to see if the building gives the right discount during the event sustenance: 3 characters
    void applyEventSustenance_threeCharacters() {
        for (int i = 0; i < 3; i++)
            new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        new BuildingDiscountFood(CharacterEnum.ARTIST).applyEventSustenance(player, board);
        assertEquals(foodBefore + 3, player.getFood(),
                "The building should have given 3 food because the player has 3 right characters");
    }
    @Test//Checking to see if the building gives any food for wrong types
    void applyEventSustenance_onlyCountsSpecifiedType() {
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        new BuildingDiscountFood(CharacterEnum.ARTIST).applyEventSustenance(player, board);
        assertEquals(foodBefore + 1, player.getFood(),
                "The building should have given only 1 food because the player has 1 right character");
    }
}
