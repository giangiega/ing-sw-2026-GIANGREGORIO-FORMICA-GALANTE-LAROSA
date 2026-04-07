package it.polimi.ingsw;
/**
 * @author Daniele
 */
import java.util.List;

public class EventHunt extends EventCard {
    private final int ppPerHunter;

    public EventHunt(EraEnum era, int ppPerHunter) {
        super(era);
        this.ppPerHunter = ppPerHunter;
    }

    @Override
    public void resolve(List<Player> players, Board board) {

        players.forEach(p -> {
            p.getBuildingCards().forEach(b -> b.getEffect().applyEventHunt(p, board));

            int hunterCount = p.getCharacterByType(CharacterEnum.HUNTER).size();
            if (hunterCount > 0) {
                p.gainFood(hunterCount);
                p.gainPP(hunterCount * ppPerHunter);
            }
        });
    }

    public int getPpPerHunter() {
        return ppPerHunter;
    }
}

/**
 * Prendete 1 Cibo e guadagnate i Punti Prestigio
 * indicati sulla carta Evento per ogni Cacciatore
 * nella vostra tribù (nell’esempio illustrato qui a
 * sinistra, 2 PP per ogni Cacciatore).
 *
 * Durante l’Evento Caccia, per ogni Cacciatore
 * nella vostra tribù, prendete 1 Cibo e
 * guadagnate 1 Punto Prestigio addizionali
 */