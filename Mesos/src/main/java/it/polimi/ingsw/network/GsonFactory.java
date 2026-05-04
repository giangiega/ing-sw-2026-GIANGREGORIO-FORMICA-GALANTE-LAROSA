/**
 * @author Giuse
 */
package it.polimi.ingsw.network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.clientInterface.*;
import it.polimi.ingsw.network.serverInterface.*;

public class GsonFactory {

    private static final String TYPE_FIELD = "op";

    /**
     * @return Gson instance ready to send operation out
     * This method sets the Gson: it serializes ClientOperation(s).
     * It gets called by ClientViewSocket to transform an object to Json
     * before writing it on the socket
     */
    public static Gson clientOperationGson(){
        RuntimeTypeAdapterFactory<ClientOperation> factory =
                RuntimeTypeAdapterFactory
                        .of(ClientOperation.class, TYPE_FIELD)
                        .registerSubtype(NumPlayersOperation.class)
                        .registerSubtype(ChooseCardOperation.class)
                        .registerSubtype(LoginOperation.class)
                        .registerSubtype(PlaceTotemOperation.class);

        return new GsonBuilder().registerTypeAdapterFactory(factory).create();
    }

    /**
     * @return :new Gson instance ready to receive events
     */
    public static Gson serverEventGson(){
        RuntimeTypeAdapterFactory<ServerEvent> serverEventFactory =
                RuntimeTypeAdapterFactory
                        .of(ServerEvent.class, TYPE_FIELD)
                        .registerSubtype(AckEvent.class)
                        .registerSubtype(EndGameEvent.class)
                        .registerSubtype(GameStartedEvent.class)
                        .registerSubtype(LoggedEvent.class)
                        .registerSubtype(InvalidChoiceEvent.class)
                        .registerSubtype(IsYourTurnEvent.class)
                        .registerSubtype(MoveTotemEvent.class)
                        .registerSubtype(UpdateBoardEvent.class)
                        .registerSubtype(UpdatePlayerEvent.class)
                        .registerSubtype(ValidCardsEvent.class);

        RuntimeTypeAdapterFactory<TribeCard> tribeFactory =
                RuntimeTypeAdapterFactory
                        .of(TribeCard.class, TYPE_FIELD)
                        .registerSubtype(CharacterCard.class)
                        .registerSubtype(EventCard.class);

        RuntimeTypeAdapterFactory<CharacterCard> characterFactory =
                RuntimeTypeAdapterFactory
                        .of(CharacterCard.class)
                        .registerSubtype(Hunter.class)
                        .registerSubtype(Gatherer.class)
                        .registerSubtype(Shaman.class)
                        .registerSubtype(Builder.class)
                        .registerSubtype(Artist.class)
                        .registerSubtype(Inventor.class);

        RuntimeTypeAdapterFactory<EventCard> eventFactory =
                RuntimeTypeAdapterFactory
                        .of(EventCard.class)
                        .registerSubtype(EventCavePainting.class)
                        .registerSubtype(EventShamanRitual.class)
                        .registerSubtype(EventSustenance.class)
                        .registerSubtype(EventHunt.class);

        RuntimeTypeAdapterFactory<BuildingEffect> buildingFactory =
                RuntimeTypeAdapterFactory
                        .of(BuildingEffect.class)
                        .registerSubtype(BuildingFoodSet.class)
                        .registerSubtype(BuildingDiscountFood.class)
                        .registerSubtype(BuildingSaveShamanPP.class)
                        .registerSubtype(BuildingBonusTotem.class)
                        .registerSubtype(BuildingBonusSameInventors.class)
                        .registerSubtype(BuildingBonusStarShaman.class)
                        .registerSubtype(BuildingBonusDoubleShamanPP.class)
                        .registerSubtype(BuildingBonusHunt.class)
                        .registerSubtype(BuildingDoubleBuilderPP.class)
                        .registerSubtype(BuildingBonusArtist.class)
                        .registerSubtype(BuildingPPForSet.class)
                        .registerSubtype(BuildingBonusForCharacterType.class)
                        .registerSubtype(BuildingCardUpperRow.class)
                        .registerSubtype(BuildingFinal25PP.class);

        return new GsonBuilder().registerTypeAdapterFactory(serverEventFactory)
                .registerTypeAdapterFactory(tribeFactory)
                .registerTypeAdapterFactory(eventFactory)
                .registerTypeAdapterFactory(characterFactory)
                .registerTypeAdapterFactory(buildingFactory).create();
    }
}
