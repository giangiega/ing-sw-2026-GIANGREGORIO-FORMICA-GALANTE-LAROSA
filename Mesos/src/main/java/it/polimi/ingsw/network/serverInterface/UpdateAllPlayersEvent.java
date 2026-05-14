package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class UpdateAllPlayersEvent implements ServerEvent {
    private final List<String> names;
    private final List<Integer> foods;
    private final List<Integer> pps;
    private final List<Map<CharacterEnum, List<String>>> tribeDesc;
    private final List<List<String>> buildingDesc;

    public UpdateAllPlayersEvent(List<Player> players) {
        this.names = players.stream().map(Player::getName).toList();
        this.foods = players.stream().map(Player::getFood).toList();
        this.pps = players.stream().map(Player::getPP).toList();

        this.tribeDesc = new ArrayList<>();
        for (Player p : players) {
            Map<CharacterEnum, List<String>> desc = new EnumMap<>(CharacterEnum.class);
            for (CharacterEnum type : CharacterEnum.values()) {
                List<String> charDesc = new ArrayList<>();
                for (CharacterCard card : p.getCharacterByType(type)) {
                    charDesc.add(card.toString());
                }
                desc.put(type, charDesc);
            }
            this.tribeDesc.add(desc);
        }

        this.buildingDesc = new ArrayList<>();
        for (Player p : players) {
            List<String> buildings = new ArrayList<>();
            for (BuildingCard b : p.getBuildingCards()) {
                buildings.add(b.updateCard());
            }
            this.buildingDesc.add(buildings);
        }
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateAllPlayers(names, foods, pps, tribeDesc, buildingDesc);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onUpdateAllPlayers(names, foods, pps, tribeDesc, buildingDesc);
    }
}
