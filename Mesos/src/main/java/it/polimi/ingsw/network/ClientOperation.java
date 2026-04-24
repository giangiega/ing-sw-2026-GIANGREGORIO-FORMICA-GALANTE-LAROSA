package it.polimi.ingsw.network;

public interface ClientOperation {
    public void executeOp(Server server, ClientManagerSocket cm);
}
