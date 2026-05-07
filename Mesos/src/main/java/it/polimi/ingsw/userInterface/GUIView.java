package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.socket.ClientViewSocket;

import java.util.List;
import java.util.Map;

public class GUIView implements ViewInterface {

    @Override
    public void init(ClientViewSocket sender) {

    }

    @Override
    public void showLobby(List<String> lobby) {

    }

    @Override
    public void askNumPlayers() {

    }

    @Override
    public void askLogin() {

    }

    @Override
    public void showGameStart() {

    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {

    }

    @Override
    public void updateOfferTrack(List<OfferTile> offerTrack) {

    }

    @Override
    public void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps) {

    }

    @Override
    public void updateRound(int currentRound) {

    }

    @Override
    public void updateTurnOrder(TurnOrderTile turnOrder) {

    }

    @Override
    public void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                           List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {

    }

    @Override
    public void placeTotem(List<Character> freeSlots) {

    }

    @Override
    public void invalidChoice(String message) {

    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {

    }

    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {

    }

}
