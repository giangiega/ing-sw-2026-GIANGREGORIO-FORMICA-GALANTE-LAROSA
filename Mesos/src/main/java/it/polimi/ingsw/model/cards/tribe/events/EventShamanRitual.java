/**
 * @author Giuse
 */

package it.polimi.ingsw.model.cards.tribe.events;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;

import java.util.List;
import java.util.ArrayList;

public class EventShamanRitual extends EventCard {
    private final int gainedPP;
    private final int lostPP;

    public EventShamanRitual(EraEnum era, boolean isFinalEvent, int gainedPP, int lostPP) {
        super(era, isFinalEvent);
        this.gainedPP = gainedPP;
        this.lostPP = lostPP;
    }

    /**
     * @param players : list of active players
     * @param board : state of the board
     * This method find the winners and the losers of the event "Shaman Ritual"
     * then it adds/subtracts the right amount of pp and checks for building effects
     */
    @Override
    public void resolve(List<Player> players, Board board) {
        //Finding losers and winners
        for(Player p : players){
            List<BuildingCard> buildings = p.getBuildingCards();
            for (BuildingCard b : buildings){
                b.getEffect().applyEventShamanBonusStars(p, board);
            }
        }

        int minStars = players.get(0).getEffectiveStars();
        int maxStars = players.get(0).getEffectiveStars();
        int playerStars;
        List <Player> winners = new ArrayList<>();
        List <Player> losers = new ArrayList<>();

        for (Player p : players) {
            playerStars = p.getEffectiveStars();
            if (playerStars < minStars) {
                minStars = playerStars;
            }
            if (playerStars > maxStars) {
                maxStars = playerStars;
            }
        }
        for (Player p : players){
            playerStars = p.getEffectiveStars();
            if (playerStars == minStars) {
                losers.add(p);
            }
            if(playerStars == maxStars){
                winners.add(p);
            }
        }
        //removing pp from losers and giving pp to winners
        for(Player p : losers){
            p.losePP(lostPP);
            for(BuildingCard c : p.getBuildingCards()){
                    c.getEffect().applyEventShamanWinner(p, board, lostPP, false);
                }
        }
        for(Player p : winners){
            p.gainPP(gainedPP);
            for(BuildingCard c : p.getBuildingCards()){
                    c.getEffect().applyEventShamanWinner(p, board, gainedPP, true);
                }
            }
        }

    public int getGainedPP() {
        return gainedPP;
    }
    public int getLostPP() {
        return lostPP;
    }

    @Override
    public String toString() {
        return "Event - Shaman Ritual | Most stars: +" + gainedPP +
                " PP | Fewest stars: -" + lostPP + " PP";
    }
}
