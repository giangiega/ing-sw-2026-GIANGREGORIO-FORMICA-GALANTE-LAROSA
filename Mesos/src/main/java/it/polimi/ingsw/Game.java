package it.polimi.ingsw;

import java.util.*;

public class Game {
    public List<Player> players;
    public GameConfig config;
    public Board board;
    public int currentRound;

    public Game (List<Player> players, Board board, GameConfig config) {
        this.players = players;
        this.board = board;
        this.config = config;
        this.currentRound = 1;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
    public Board getBoard() {
        return board;
    }
    public GameConfig getConfig() {
        return config;
    }
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * called for each player in TurnOrderTile, from the top to the bottom.
     * The totem moves to a free chosen OfferTile
     * @author Ric
     * @param player that has a totem on OfferTile
     * @param tile from A to G
     * @throws InvalidPlayerActionException
     */
    public void placeTotem(Player player, OfferTile tile) throws InvalidPlayerActionException {
        if (!tile.getFreeOfferTile())
            throw new InvalidPlayerActionException("OfferTile is occupied by a player");
        board.getTurnOrderTile().totemOut(player);
        tile.setOccupant(player);
        }


    /**
     * called for each player in an OfferTile, from left to right.
     * The lists as parameters indicates the cards taken from the rows by the player.
     * playerMove() moves this cards into the player's tribe or buildings.
     * Then I unplace the totem, that returns onto the OferTile
     */
    public void resolveAction( OfferTile tile,
                              List<Integer> upperCards, List<Integer> lowerCards,
                              List<Integer> upperBuildings, List<Integer> lowerBuildings)
            throws InvalidPlayerActionException {
        Player player = tile.getOccupant();
        if (player == null)
            throw new InvalidPlayerActionException("OfferTile is not occupied by any player");

        if (tile.getLetter() == 'A') {
            player.gainFood(3);
        } else {
            if (upperCards.size() > tile.getCountUpperArrow())
                throw new InvalidPlayerActionException("Too many upper cards chosen");
            if (lowerCards.size() > tile.getCountLowerArrow())
                throw new InvalidPlayerActionException("Too many lower cards chosen");
            tile.playerMove(player, board, upperCards, lowerCards, upperBuildings, lowerBuildings);
        }

        tile.setOccupant(null);
        unplaceTotem(player);
    }

    /**
     * called at the end of resolveAction(), moves the totem to the first free TurnOrderTile slot.
     * If the slot has a food bonus, the player gains food immediately, same for malus.
     */
    public void unplaceTotem(Player player) {
        int nextSlot = board.getTurnOrderTile().getOrder().size();
        board.getTurnOrderTile().totemIn(player);

        int bonus = board.getTurnOrderTile().getFoodBonusForSlot(nextSlot);
        if (bonus > 0) {
            player.gainFood(bonus);
            ///  Aggiunta per totem////////////////////////////////////////////////////
            for (BuildingCard c : player.getBuildingCards()) {
                if(c.getEffect() instanceof BuildingBonusTotem){
                    c.getEffect().applyEndTurn(player, board);
                }
            }
        } else if (board.getTurnOrderTile().isLastSlot(nextSlot)) {
            if (player.getFood() > 0)
                player.payFood(1);
            else
                player.losePP(2);
        }
    }

    /**
     * This method use 2 local list to pick every event into the lowerRow. Sustenance are divided by
     * other type of event because sustenance has to be resolved at the end, after all others events.
     * So the first for cycle is done for others List, then the second cycle for Sustenance.
     * At the end it calls rowsEndRound() from Board, with the rows update and the possible eraSwitch.
     * If it's the last round (10th) I have to resolve events also from the upperRow, already sorted
     */
    public void endRound() {
        List<EventCard> others = new ArrayList<>();
        List<EventCard> sustenances = new ArrayList<>();

        for(TribeCard card : board.getLowerRow()) {
            if (card instanceof EventSustenance) {
                sustenances.add((EventCard)card);
            } else if (card instanceof EventCard) {
                others.add((EventCard)card);
            }
        }
        if(currentRound == 10) {
            for (TribeCard card : board.getUpperRow()) {
                if (card instanceof EventSustenance) {
                    sustenances.add((EventCard) card);
                } else if (card instanceof EventCard) {
                    others.add((EventCard) card);
                }
            }
        }

        for(Player p : players){
            for(BuildingCard c : p.getBuildingCards()){
                c.getEffect().applyEndTurn(p, board);
            }
        }

        for(EventCard event : others)
            event.resolve(players, board);
        for(EventCard s : sustenances)
            s.resolve(players, board);

        board.rowsEndRound();
        if(currentRound < 10)
            currentRound++;
    }

    /**
     * has to be called only when currentRound == 10 , and before getWinner()
     * PP from eventual effects of the last round events are not calculated in this method,
     * so they have to be resolved before calling calculateFinalScores() and getWinner()
     * Buildings that modify PP or in general every building which effect has to be shown at the end
     * of the game are resolved here, before than the final score calculation.
     */
    public Map<Player,Integer> calculateFinalScores() {
        Map<Player,Integer> scores = new HashMap<>();
        for(Player p : players){
            for(BuildingCard c : p.getBuildingCards()){
                c.getEffect().applyEndGame(p, board);
            }
        }
        for(Player p : players) {
            int points = p.getPP();
            points += p.getEndGameBuildersPP();
            points += p.getCharacterByType(CharacterEnum.INVENTOR).size() * p.getDistinctInventorsIcon();
            points += (p.getCharacterByType(CharacterEnum.ARTIST).size() / 2) * 10;
            points += p.getTotalBuildingsPP();

            scores.put(p, points);
        }
        return scores;
    }

    /**
     * this method returns a list of players bc it has to manage the case of draw between 2 players.
     * When 2 players have equals PP and food, they both win, so getWinner() has to return them both.
     *
     */
    public List<Player> getWinner() {
        Player winner = players.getFirst();
        Map<Player,Integer> scores = calculateFinalScores();

        for(int i = 1; i < players.size(); i++) {
            Player curr =  players.get(i);
            if(scores.get(curr) > scores.get(winner)) {
                winner = curr;
            }
            else if(scores.get(curr).equals(scores.get(winner))) {
                if(curr.getFood()  > winner.getFood()) {
                    winner = curr;
                }
            }
        }
        List<Player> winners = new ArrayList<>();
        winners.add(winner);
        for(Player player : players) {
            if(scores.get(player).equals(scores.get(winner)) && player.getFood() == winner.getFood()) {
                if(!winners.contains(player)) {
                    winners.add(player);
                }
            }
        }
        return winners;
    }

}
