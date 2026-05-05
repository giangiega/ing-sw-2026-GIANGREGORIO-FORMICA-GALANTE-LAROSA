package it.polimi.ingsw.model.eraLogic;

import it.polimi.ingsw.model.boardAndTiles.Board;

public class TransitionEraII extends EraTransition{

    public void applyTransition(Board board){
        board.moveBuildingsToLowerRow();
        board.fillBuildingUpperRow(board.getBuildingDeckEra2());
    }
}
