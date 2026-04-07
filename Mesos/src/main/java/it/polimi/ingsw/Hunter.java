package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Hunter extends CharacterCard{

    private boolean hunt;

    public Hunter(EraEnum era, boolean hunt) {
        super(era);
        this.hunt = hunt;

    }
    public boolean getHunt(){
        return hunt;
    }
}
