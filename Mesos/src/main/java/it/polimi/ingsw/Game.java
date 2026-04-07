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
     * This method use 2 local list to pick every event into the lowerRow. Sustenance are divided by
     * other type of event because sustenance has to be resolved at the end, after all others events.
     * So the first for cycle is done for others List, then the second cycle for Sustenance.
     * At the end it calls rowsEndRound() from Board, with the rows update and the possible eraSwitch.
     */
    // manca un controllo sull'ultimo round in cui prima della fine devo risolvere anche gli
    //eventi presenti nella fila superiore?
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
        for(EventCard event : others)
            event.resolve(players, board);
        for(EventCard s : sustenances)
            s.resolve(players, board);

        board.rowsEndRound();
        currentRound++;
    }

    /**
     * has to be called only when currentRound == 10 , and before getWinner()
     * PP from eventual effects of the last round events are not calculated in this method,
     * so they have to be resolved before calling calculateFinalScores() and getWinner()
     */
    public Map<Player,Integer> calculateFinalScores() {
        Map<Player,Integer> scores = new HashMap<>();
        for(Player p : players) {
            int points = p.getPP();
            /*points += punti totali dei builder*/
            points += p.getCharacterByType(CharacterEnum.INVENTOR).size() * p.getDistinctInventorsIcon();
            points += (p.getCharacterByType(CharacterEnum.ARTIST).size() / 2) * 10;
            /*points += punti totali dei buildings*/

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
        Player winner = players.get(0);
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
