package it.polimi.ingsw;
/**
 * @author Giuse
 */
import java.util.List;
import java.util.ArrayList;

public class EventShamanRitual extends EventCard {
    private int gainedPP;
    private int lostPP;

    public EventShamanRitual(EraEnum era) {
        super(era);
    }

    /**
     * @param players : list of active players
     * @param board :state of the board
     * this method find the winners and the losers of the event "Shaman Ritual"
     * then it adds/subtracts the right amount of pp and checks for building effects
     */
    @Override
    public void resolve(List<Player> players, Board board) {
        //Finding losers and winners
        int minStars = players.get(0).getEffectiveShamanStars();
        int maxStars = players.get(0).getEffectiveShamanStars();
        int playerStars;
        List <Player> winners = new ArrayList<>();
        List <Player> losers = new ArrayList<>();

        for (Player p : players) {
            playerStars = p.getEffectiveShamanStars();
            if (playerStars < minStars) {
                minStars = playerStars;
            }
            if (playerStars > maxStars) {
                maxStars = playerStars;
            }
        }
        for (Player p : players){
            playerStars = p.getEffectiveShamanStars();
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
                c.getEffect().applyEventShaman(p, board);
            }
        }
        for(Player p : winners){
            p.gainPP(gainedPP);
            for(BuildingCard c : p.getBuildingCards()){
                c.getEffect().applyEventShaman(p, board);
            }
        }
    }
    public int getGainedPP() {
        return gainedPP;
    }
    public int getLostPP() {
        return lostPP;
    }
}
