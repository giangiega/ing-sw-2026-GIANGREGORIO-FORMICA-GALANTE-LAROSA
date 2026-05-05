package it.polimi.ingsw.model.eraLogic;

import it.polimi.ingsw.model.boardAndTiles.Board;

public class TransitionEraIII extends EraTransition {

    public void applyTransition(Board board){
        board.discardLowerRowBuildings();
        board.moveBuildingsToLowerRow();
        board.fillBuildingUpperRow(board.getBuildingDeckEra3());
    }
}
