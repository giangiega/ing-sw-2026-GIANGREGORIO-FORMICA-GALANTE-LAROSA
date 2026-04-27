/**
 * @author Giuse
 */
package it.polimi.ingsw.network.clientInterface;
import com.google.gson.Gson;
import it.polimi.ingsw.network.clientInterface.ClientOperation;
import java.io.PrintWriter;
public class ClientViewSocket {

    private final PrintWriter out;
    private final Gson gson;

    public ClientViewSocket(PrintWriter out) {
        this.out = out;
        this.gson = GsonFactory.clientOperationGson();
    }

    /**
     * @param operation : type of operation sent
     * This method send the operation to the server
     */
    public void sendOperation(ClientOperation operation){
        String json = gson.toJson(operation, ClientOperation.class);
        out.println(json);
    }
}
