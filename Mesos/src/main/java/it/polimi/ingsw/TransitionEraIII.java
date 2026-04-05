package it.polimi.ingsw;

public class TransitionEraIII extends EraTransition {

    public void applyTransition(Board board){
        board.discardLowerRowBuildings();
        board.moveBuildingsToLowerRow();
        board.fillBuildingUpperRow(board.getBuildingDeckEra3());
    }
}
