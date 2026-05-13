/**
 * @author Giuse
 */
package it.polimi.ingsw.network.socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.model.cards.buildings.*;
import it.polimi.ingsw.model.cards.tribe.*;
import it.polimi.ingsw.model.cards.tribe.characters.*;
import it.polimi.ingsw.model.cards.tribe.events.EventCavePainting;
import it.polimi.ingsw.model.cards.tribe.events.EventHunt;
import it.polimi.ingsw.model.cards.tribe.events.EventShamanRitual;
import it.polimi.ingsw.model.cards.tribe.events.EventSustenance;
import it.polimi.ingsw.network.clientInterface.*;
import it.polimi.ingsw.network.serverInterface.*;

public class GsonFactory {

    /**
     * @return Gson instance ready to send operation out
     * This method sets the Gson: it serializes ClientOperation(s).
     * It gets called by ClientViewSocket to transform an object to Json
     * before writing it on the socket
     */
    public static Gson clientOperationGson(){
        RuntimeTypeAdapterFactory<ClientOperation> factory =
                RuntimeTypeAdapterFactory
                        .of(ClientOperation.class, "typeOp")
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
                        .of(ServerEvent.class, "typeEvent")
                        .registerSubtype(AckEvent.class)
                        .registerSubtype(EndGameEvent.class)
                        .registerSubtype(GameStartedEvent.class)
                        .registerSubtype(LoggedEvent.class)
                        .registerSubtype(InvalidChoiceEvent.class)
                        .registerSubtype(IsYourTurnEvent.class)
                        .registerSubtype(MoveTotemEvent.class)
                        .registerSubtype(UpdateAllPlayersEvent.class)
                        .registerSubtype(UpdateAllTribesEvent.class)
                        .registerSubtype(UpdateBoardEvent.class)
                        .registerSubtype(UpdateOfferTrackEvent.class)
                        .registerSubtype(UpdateRoundEvent.class)
                        .registerSubtype(UpdateRowsEvent.class)
                        .registerSubtype(ValidCardsEvent.class);

        RuntimeTypeAdapterFactory<TribeCard> tribeFactory =
                RuntimeTypeAdapterFactory
                        .of(TribeCard.class, "typeTribeCard")
                        .registerSubtype(Hunter.class)
                        .registerSubtype(Gatherer.class)
                        .registerSubtype(Shaman.class)
                        .registerSubtype(Builder.class)
                        .registerSubtype(Artist.class)
                        .registerSubtype(Inventor.class)
                        .registerSubtype(EventCavePainting.class)
                        .registerSubtype(EventShamanRitual.class)
                        .registerSubtype(EventSustenance.class)
                        .registerSubtype(EventHunt.class);

        RuntimeTypeAdapterFactory<CharacterCard> characterCardFactory =
                RuntimeTypeAdapterFactory
                        .of(CharacterCard.class, "typeTribeCard")
                        .registerSubtype(Hunter.class)
                        .registerSubtype(Gatherer.class)
                        .registerSubtype(Shaman.class)
                        .registerSubtype(Builder.class)
                        .registerSubtype(Artist.class)
                        .registerSubtype(Inventor.class);

        RuntimeTypeAdapterFactory<BuildingEffect> buildingFactory =
                RuntimeTypeAdapterFactory
                        .of(BuildingEffect.class, "typeBuilding")
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
                .registerTypeAdapterFactory(characterCardFactory)
                .registerTypeAdapterFactory(buildingFactory).create();
    }
}
