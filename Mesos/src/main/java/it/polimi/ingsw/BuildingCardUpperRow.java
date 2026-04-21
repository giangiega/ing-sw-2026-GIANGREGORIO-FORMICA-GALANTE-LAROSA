package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingCardUpperRow extends BuildingEffect {
    //At the beginning, we assume that the player doesn't buy any card
    private int chosenIndex = -1;
    private boolean chosenIsBuilding = false;
    /**
     * @param index : index of the chosen card
     * @param isBuilding the chosen card is a building
     * this method is called by Game before applyEndTurn. It registers the player's choice
     */
    @Override
    public void setChoice(int index, boolean isBuilding) {
        this.chosenIndex = index;
        this.chosenIsBuilding = isBuilding;
    }
    @Override
    public int getChosenIndex() {
        return chosenIndex;
    }
    @Override
    public boolean getChosenIsBuilding() {
        return chosenIsBuilding;
    }
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method tells that BuildingUpperRow needs the player's interaction
     */
    @Override
    public boolean requiresChoice(Player p, Board b) {
        return true;
    }
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * @param chosenIndex : index of the chosen card
     * @param chosenIsBuilding : true means that the chosen card is a building
     * @throws InvalidPlayerActionException
     * This method uses the choice of the player to give him the chosen building/character card
     * chosenIndex e chosenIsBuilding should be given by the server, after it has asked the player
     * for them
     */
    @Override
    public void applyEndTurn(Player p, Board b, int chosenIndex, boolean chosenIsBuilding) throws InvalidPlayerActionException {
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
            p.payFood(chosen.getCost(p));
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
    }
}