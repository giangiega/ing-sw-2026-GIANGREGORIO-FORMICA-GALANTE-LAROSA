package it.polimi.ingsw.model.decks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.model.cards.buildings.*;
import it.polimi.ingsw.model.cards.tribe.*;
import it.polimi.ingsw.model.cards.tribe.characters.*;
import it.polimi.ingsw.model.cards.tribe.events.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * this class has all the methods for creating the tribe Deck and BuildingDeck
 * @author Ale
 */
public class CardFactory {

    private JsonObject loadJson(String filename) {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(filename);
            return JsonParser.parseReader(new InputStreamReader(is))
                    .getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("error loading " + filename, e);
        }
    }

    /**
     * builds the completed tribeDeck
     * @param config: GameConfig for the numPlayers
     * @return
     */
    public Deck buildTribeDeck(GameConfig config) {
        JsonObject data = loadJson("TribeCards.json");

        List<TribeCard> eraI = new ArrayList<>();
        List<TribeCard> eraII = new ArrayList<>();
        List<TribeCard> eraIII = new ArrayList<>();
        List<TribeCard> finalEvents = new ArrayList<>();

        JsonArray characters = data.getAsJsonArray("CharacterCards");
        for (JsonElement el : characters) {
            JsonObject card = el.getAsJsonObject();
            if (!isValidForConfig(card, config)) continue;
            CharacterCard c = createCharacterCard(card);
            c.setImage(card.get("image").getAsString());
            addToEra(c, EraEnum.valueOf(card.get("era").getAsString()), eraI, eraII, eraIII);
        }

        JsonArray events = data.getAsJsonArray("EventCards");
        for (JsonElement el : events) {
            JsonObject card = el.getAsJsonObject();
            EventCard e = createEventCard(card);
            e.setImage(card.get("image").getAsString());

            if (e.isFinalEvent()) {
                finalEvents.add(e);
            } else {
                addToEra(e, EraEnum.valueOf(card.get("era").getAsString()), eraI, eraII, eraIII);
            }

            if(finalEvents.size() > 2) throw new RuntimeException("error: final events must be two");
        }

        Collections.shuffle(eraI);
        Collections.shuffle(eraII);
        Collections.shuffle(eraIII);

        List<TribeCard> finalDeck = new ArrayList<>();
        finalDeck.addAll(eraI);
        finalDeck.addAll(eraII);
        finalDeck.addAll(eraIII);
        finalDeck.addAll(finalEvents);

        return new Deck(finalDeck);
    }

    /**
     * returns true if a card is valid for the number of players in the game, otherwise false
     * @param card
     * @param config
     * @return
     */
    private boolean isValidForConfig(JsonObject card, GameConfig config) {
        int numPlayer = card.get("numPlayer").getAsInt();
        return numPlayer <= config.getNumPlayers();
    }

    /**
     * adds the card to the respective era tribe deck
     * @param card
     * @param era
     * @param eraI
     * @param eraII
     * @param eraIII
     */
    private void addToEra(TribeCard card, EraEnum era, List<TribeCard> eraI, List<TribeCard> eraII, List<TribeCard> eraIII) {
        switch (era) {
            case I   -> eraI.add(card);
            case II  -> eraII.add(card);
            case III -> eraIII.add(card);
            default -> throw new IllegalArgumentException("error: unknown era");
        }
    }

    /**
     * creates the specific CharacterCard based on type
     * @param data
     * @return
     */
    private CharacterCard createCharacterCard(JsonObject data) {
        CharacterEnum type = CharacterEnum.valueOf(data.get("type").getAsString());
        EraEnum era = EraEnum.valueOf(data.get("era").getAsString());
        int numPlayer = data.get("numPlayer").getAsInt();

        return switch (type) {
            case HUNTER -> new Hunter(era, numPlayer, data.get("hunt").getAsBoolean());
            case GATHERER -> new Gatherer(era, numPlayer);
            case ARTIST -> new Artist(era, numPlayer);
            case SHAMAN -> new Shaman(era, numPlayer, data.get("starCount").getAsInt());
            case BUILDER -> new Builder(era, numPlayer, data.get("wingCount").getAsInt(), data.get("endGamePP").getAsInt());
            case INVENTOR -> new Inventor(era, numPlayer, IconEnum.valueOf(data.get("iconType").getAsString()));
            default -> throw new IllegalArgumentException("error: Invalid character type");
        };
    }

    /**
     * creates the specific EventCard based on type
     * @param data
     * @return
     */
    private EventCard createEventCard(JsonObject data) {
        String type = data.get("type").getAsString();
        EraEnum era = EraEnum.valueOf(data.get("era").getAsString());
        boolean isFinal = data.get("isFinalEvent").getAsBoolean();

        return switch (type) {
            case "sustenance" -> new EventSustenance(
                    era,
                    isFinal,
                    data.get("ppPerUnfedCharacter").getAsInt()
            );
            case "shamanRitual" -> new EventShamanRitual(
                    era,
                    isFinal,
                    data.get("gainedPP").getAsInt(),
                    data.get("lostPP").getAsInt()
            );
            case "hunt" -> new EventHunt(
                    era,
                    isFinal,
                    data.get("ppPerHunter").getAsInt()
            );
            case "cavePainting" -> new EventCavePainting(
                    era,
                    isFinal,
                    data.get("minArtists").getAsInt(),
                    data.get("gainedPP").getAsInt(),
                    data.get("lostPP").getAsInt()
            );
            default -> throw new IllegalArgumentException("error: unknow event type");
        };
    }

    /**
     * creates a BuildingDeck of a specific era.
     * we should decide where to handle the draw of a specific number of buildingCards
     * for each buildingDecks based on numPlayers of GameConfig
     * @param era
     * @param config
     * @return
     */
    public BuildingDeck buildBuildingDeck(EraEnum era, GameConfig config) {
        JsonObject data = loadJson("BuildingCards.json");
        JsonArray buildings = data.getAsJsonArray("BuildingCards");

        List<BuildingCard> cards = new ArrayList<>();

        for (JsonElement el : buildings) {
            JsonObject card = el.getAsJsonObject();
            if (EraEnum.valueOf(card.get("era").getAsString()) != era) continue;
           // cards.add(createBuildingCard(card));
            BuildingCard b = createBuildingCard(card);
            b.setImage(card.get("image").getAsString());
            cards.add(b);
        }

        Collections.shuffle(cards);

        List<BuildingCard> effectiveCards = new ArrayList<>();
        for(int i = 0; i < config.getBuildingCardsPerEra().get(era); i++){
            effectiveCards.add(cards.getFirst());
            cards.removeFirst();
        }

        return new BuildingDeck(era, effectiveCards);
    }

    /**
     * creates a specific buildingCard using a switch case for
     * creating the correct BuildingEffect (variable of instance)
     * @param data
     * @return
     */
    private BuildingCard createBuildingCard(JsonObject data) {
        String type = data.get("type").getAsString();
        EraEnum era = EraEnum.valueOf(data.get("era").getAsString());
        int foodCost = data.get("baseFoodCost").getAsInt();
        int basePrestigePoints = data.get("basePrestigePoints").getAsInt();

        BuildingEffect be = switch (type) {
            case "buildingFoodSet" ->
                    new BuildingFoodSet();
            case "buildingDiscountFood" ->
                    new BuildingDiscountFood(
                            CharacterEnum.valueOf(data.get("characterType").getAsString())
                    );
            case "buildingSaveShamanPP" ->
                    new BuildingSaveShamanPP();
            case "buildingBonusTotem" ->
                    new BuildingBonusTotem();
            case "buildingBonusSameInventors" ->
                    new BuildingBonusSameInventors();
            case "buildingBonusStarShaman" ->
                    new BuildingBonusStarShaman();
            case "buildingBonusDoubleShamanPP" ->
                    new BuildingBonusDoubleShamanPP();
            case "buildingBonusHunt" ->
                    new BuildingBonusHunt();
            case "buildingDoubleBuilderPP" ->
                    new BuildingDoubleBuilderPP();
            case "buildingBonusArtist" ->
                    new BuildingBonusArtist();
            case "buildingPPForSet" ->
                    new BuildingPPForSet();
            case "buildingBonusForCharacterType" ->
                    new BuildingBonusForCharacterType(
                           data.get("PP").getAsInt(),
                            CharacterEnum.valueOf(data.get("characterType").getAsString())
                    );
            case "buildingCardUpperRow" ->
                    new BuildingCardUpperRow();
            case "buildingFinal25PP" ->
                    new BuildingFinal25PP();
            default -> throw new IllegalArgumentException("error: unknown building type");
        };

        return new BuildingCard(era, foodCost, basePrestigePoints, be);
    }
}
