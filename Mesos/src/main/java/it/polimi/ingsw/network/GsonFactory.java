/**
 * @author Giuse
 */
package it.polimi.ingsw.network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.network.clientInterface.*;
import it.polimi.ingsw.network.serverInterface.*;

public class GsonFactory {

    private static final String TYPE_FIELD = "op";

    /**
     * @return : Gson instance ready to send operation out
     * This method sets the Gson: it serializes ClientOperation(s).
     * It gets called by ClientViewSocket to transform an object to Json
     * before writing it on the socket
     */
    public static Gson clientOperationGson(){
        RuntimeTypeAdapterFactory<ClientOperation> factory =
                RuntimeTypeAdapterFactory
                        .of(ClientOperation.class, TYPE_FIELD)
                        .registerSubtype(ChooseCardOperation.class)
                        .registerSubtype(LoginOperation.class)
                        .registerSubtype(PlaceTotemOperation.class);

        return new GsonBuilder().registerTypeAdapterFactory(factory).create();
    }

    /**
     * @return :new Gson instance ready to receive events
     */
    public static Gson serverEventGson(){
        RuntimeTypeAdapterFactory<ServerEvent> factory =
                RuntimeTypeAdapterFactory
                        .of(ServerEvent.class, TYPE_FIELD)
                        .registerSubtype(EndGameEvent.class)
                        .registerSubtype(LoggedEvent.class)
                        .registerSubtype(AckEvent.class)
                        .registerSubtype(IsYourTurnEvent.class)
                        .registerSubtype(MoveTotemEvent.class)
                        .registerSubtype(UpdateBoardEvent.class)
                        .registerSubtype(ValidCardsEvent.class);


        return new GsonBuilder().registerTypeAdapterFactory(factory).create();
    }
}
