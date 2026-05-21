package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TurnControllerSnapshot {

    private final boolean inResolvingPhase;
    private final Set<String> totemPlacedCurrRound;
    private final List<String> resolveOrder;
    private final int idx;
    private final String currentPlacementPlayer;

    public TurnControllerSnapshot(boolean inResolvingPhase, Set<String> totemPlacedCurrRound,
                                  List<String> resolveOrder, int idx,
                                  String currentPlacementPlayer) {
        this.inResolvingPhase = inResolvingPhase;
        this.totemPlacedCurrRound = new HashSet<>(totemPlacedCurrRound);
        this.resolveOrder = new ArrayList<>(resolveOrder);
        this.idx = idx;
        this.currentPlacementPlayer = currentPlacementPlayer;
    }

    public boolean isInResolvingPhase() {
        return inResolvingPhase;
    }
    public Set<String> getTotemPlacedCurrRound() {
        return totemPlacedCurrRound;
    }
    public List<String> getResolveOrder() {
        return resolveOrder;
    }
    public int getIdx() {
        return idx;
    }
    public String getCurrentPlacementPlayer() {
        return currentPlacementPlayer;
    }
}
