package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.enums.ColorEnum;

public class LoginOperation implements ClientOperation {

    private String namePlayer;
    private ColorEnum totemColor;

    public LoginOperation(String namePlayer, ColorEnum totemColor) {
        this.namePlayer = namePlayer;
        this.totemColor = totemColor;
    }
    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.login(namePlayer,totemColor,cm);
    }
}
