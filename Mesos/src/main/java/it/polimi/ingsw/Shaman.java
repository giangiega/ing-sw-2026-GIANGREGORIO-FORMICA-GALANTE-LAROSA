package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Shaman extends CharacterCard{
    private int StarCount;

    public Shaman(EraEnum era, int StarCount) {
        super(era);
        this.StarCount = StarCount;
    }

    public int getStarCount()
    {
        return  StarCount ;
    }
}
