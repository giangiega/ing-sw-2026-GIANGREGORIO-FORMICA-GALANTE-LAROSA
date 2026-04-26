/**
 * @author Daniele
 */

package it.polimi.ingsw.model;

/**
 * this class will help to compact the parameters of the viewInterface in the case of the method eventResult
 */
public class EventOutcome {
    private int PpChange;
    private int foodChange;


    public EventOutcome(int PpChange, int foodChange) {
        this.PpChange = PpChange;
        this.foodChange = foodChange;
    }

    public int getPPchange() { return PpChange; }

    public int getFoodChange() { return foodChange; }
}
