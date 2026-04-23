package it.polimi.ingsw.model;

public class TransitionEraII extends EraTransition{

    public void applyTransition(Board board){
        board.moveBuildingsToLowerRow();
        board.fillBuildingUpperRow(board.getBuildingDeckEra2());
    }
}
