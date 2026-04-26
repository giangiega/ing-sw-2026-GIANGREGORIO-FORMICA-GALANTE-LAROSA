package it.polimi.ingsw.network.clientInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public class GsonFactory {

    private static final String TYPE_FIELD = "type";

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
                        .registerSubtype(BuildingChoiceOperation.class)
                        .registerSubtype(ChooseCardOperation.class)
                        .registerSubtype(LoginOperation.class)
                        .registerSubtype(PlaceTotemOperation.class);

        return new GsonBuilder().registerTypeAdapter(factory).create();
    }

    /**
     * @return :new Gson instance ready to receive events
     */
    public static Gson serverGson(){
        RuntimeTypeAdapterFactory<Server> factory =
                RuntimeTypeAdapterFactory
                        .of(ClientOperation.class, TYPE_FIELD)
                        .registerSubtype(BuildingChoiceOperation.class)
                        .registerSubtype(ChooseCardOperation.class)
                        .registerSubtype(LoginOperation.class)
                        .registerSubtype(PlaceTotemOperation.class);

        return new GsonBuilder().registerTypeAdapter(factory).create();
    }
}
