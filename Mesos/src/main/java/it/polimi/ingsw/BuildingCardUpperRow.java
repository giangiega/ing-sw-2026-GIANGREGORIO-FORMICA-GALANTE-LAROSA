package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingCardUpperRow extends BuildingEffect {
    //At the beginning, we assume that the plater doesn't buy any card
    private int chosenIndex = -1;
    private boolean chosenIsBuilding = false;
    /**
     * @param index : index of the chosen card
     * @param isBuilding the chosen card is a building
     * this method is called by Game before applyEndTurn. It registers the player's choice
     */
    public void setChoice(int index, boolean isBuilding) {
        this.chosenIndex = index;
        this.chosenIsBuilding = isBuilding;
    }
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * @throws InvalidPlayerActionException
     * This method uses the choice of the player to give him the chosen building/character card
     */
    @Override
    public void applyEndTurn(Player p, Board b) throws InvalidPlayerActionException {
        if (chosenIndex == -1){
            return; //the player chose not to use his extra move
        }

        if (chosenIsBuilding) {
            if (chosenIndex < 0 || chosenIndex >= b.getBuildingUpperRow().size()){
                throw new InvalidPlayerActionException("Building index out of bounds");
            }
            BuildingCard chosen = b.getBuildingUpperRow().get(chosenIndex);
            if (p.getFood() < chosen.getCost(p)) {
                throw new InvalidPlayerActionException("Not enough food");
            }
            b.removeFromBuildingUpperRow(chosen);
            p.addBuildingCard(chosen);
        } else {
            if (chosenIndex < 0 || chosenIndex >= b.getBuildingUpperRow().size()){
                throw new InvalidPlayerActionException("Building index out of bounds");
            }
            TribeCard card = b.getUpperRow().get(chosenIndex);
            if (card instanceof EventCard){
                throw new InvalidPlayerActionException("Cannot take an EventCard");
            }
            b.removeFromUpperRow(card);
            p.addCharacterCard((CharacterCard) card, b);
        }
        chosenIndex = -1; //reset after using the method
    }
}