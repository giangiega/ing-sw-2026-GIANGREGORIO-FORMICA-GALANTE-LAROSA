package it.polimi.ingsw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class BuildingBonusArtistTest {
    private Board board;
    private GameConfig config;
    private Deck tribeDeck;
    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;

    @BeforeEach
    void setUp(){
        CardFactory cf = new CardFactory();
        config = GameConfig.create(2);
        tribeDeck = cf.buildTribeDeck(config);
        buildingDeckEra1 = cf.buildBuildingDeck(EraEnum.I, config);
        buildingDeckEra2 = cf.buildBuildingDeck(EraEnum.II, config);
        buildingDeckEra3 = cf.buildBuildingDeck(EraEnum.III, config);
    }

    @Test
    void applyEventCavePainting1(){
        Player player = new Player("Testing1", ColorEnum.BLUE);
        board = new Board(config, tribeDeck, buildingDeckEra1, buildingDeckEra2, buildingDeckEra3);
        CharacterCard artist = new Artist(EraEnum.I, 2);
        player.addCharacterCard( artist , board);
        player.addCharacterCard( artist , board);


        int foodBefore = player.getFood();

        BuildingBonusArtist bonus1 = new BuildingBonusArtist();
        bonus1.applyEventCavePainting(player,board);

        int foodAfter = player.getFood();

        assertEquals(foodBefore + 2, foodAfter, "The player's food should increase by 2 ");
    }
    @Test
    void applyEventCavePainting2(){
        Player player = new Player("Testing2", ColorEnum.BLUE);

        int foodBefore = player.getFood();

        BuildingBonusArtist bonus2 = new BuildingBonusArtist();
        bonus2.applyEventCavePainting(player,board);

        assertEquals(foodBefore , player.getFood(), "Player should have the same amount of food as before ");
    }
}
