package it.polimi.ingsw.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is for managing database, it is built using singleton design pattern
 */
public class DatabaseManager {
    private static final String URL  = "jdbc:mysql://localhost:3306/mesos" +
            "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "9mesos";

    private static DatabaseManager database;
    private final String url;
    private final String user;
    private final String password;

    /**
     * constructor, this is a private method because it is called only in this
     * class (getDatabase)
     */
    private DatabaseManager(){
        this.url = URL;
        this.user = USER;
        this.password = PASS;
        createSchema();
    }

    public static synchronized DatabaseManager getDatabase() {
        if (database == null) database = new DatabaseManager();
        return database;
    }

    /**
     * this method use classes from standard java sql library to get Connection
     * from mysql driver
     * @return new connection
     * @throws SQLException standard library exception
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * creates the table "game" if this doesn't exist
     */
    private void createSchema(){
        String urlWithoutDb = "jdbc:mysql://localhost:3306/" +
                "?useSSL=false&allowPublicKeyRetrieval=true";
        String sql = """
             CREATE TABLE IF NOT EXISTS game(
                 id          INT AUTO_INCREMENT PRIMARY KEY,
                 nickname    VARCHAR(50)  NOT NULL,
                 score       INT          NOT NULL,
                 num_players INT          NOT NULL,
                 game_date   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                 winner      BOOLEAN      NOT NULL DEFAULT FALSE
             )
         """;
        /*this "try with resources" closes connections to database, these are limited resources*/
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, user, password);
             Statement  stmt = conn.createStatement()) {
            /*these two lines creates the mesos db if it doesn't exist on local mysql server*/
            stmt.execute("CREATE DATABASE IF NOT EXISTS mesos");
            stmt.execute("USE mesos");
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database error: createSchema failed: " + e.getMessage());
        }
    }

    /**
     * saves result in the table "game"
     * @param nickname player's name
     * @param score achieved score
     * @param numPlayers number of players in the game
     * @param winner the player was a winner
     */
    public void saveResult(String nickname, int score, int numPlayers, boolean winner) {
        String sql = "INSERT INTO game(nickname, score, num_players, winner) VALUES (?, ?, ?, ?)";
        /*PreparedStatement is used to prevent SQLInjection: they are pure data*/
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            /*these lines setups the PreparedStatement to add a rows in the table*/
            ps.setString(1, nickname);
            ps.setInt(2, score);
            ps.setInt(3, numPlayers);
            ps.setBoolean(4, winner);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error: saveResult failed: " + e.getMessage());
        }
    }

    /**
     * builds the ranking (for a specific numPlayers games) from database data
     * @param numPlayers number of players in the game
     * @return ranking
     */
    public List<RankingRow> getRanking(int numPlayers) {
        String sql = """
                SELECT   g1.nickname,
                         g1.num_players,
                    COUNT(*) AS wins,
                    (SELECT SUM(g2.score)
                    FROM   game g2
                    WHERE  g2.nickname = g1.nickname
                    AND    g2.num_players = ?) AS total_score
                FROM     game g1
                WHERE    g1.num_players = ? AND g1.winner = TRUE
                GROUP BY g1.nickname
                ORDER BY wins DESC, total_score DESC
                """;
        List<RankingRow> ranking = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numPlayers);
            ps.setInt(2, numPlayers);
            ResultSet rs = ps.executeQuery();
            /*creating the ranking based of ResultSet*/
            int position = 1;
            int rowCount = 0;
            int lastWins = Integer.MAX_VALUE;
            int lastTotalScore = Integer.MAX_VALUE;
            while (rs.next()) {
                rowCount++;
                int currentWins = rs.getInt("wins");
                int currentTotalScore = rs.getInt("total_score");
                //currentWins > lastWins is impossible because result is ordered desc in sql query
                if(currentWins < lastWins || (currentWins == lastWins && currentTotalScore < lastTotalScore)){
                    position = rowCount;
                    lastWins = currentWins;
                    lastTotalScore = currentTotalScore;
                }
                RankingRow rankingRow = new RankingRow(
                        position,
                        rs.getString("nickname"),
                        currentWins,
                        currentTotalScore,
                        rs.getInt("num_players")
                );
                ranking.add(rankingRow);
            }
        } catch (SQLException e) {
            System.err.println("Database error: getRanking failed: " + e.getMessage());
        }
        return ranking;
    }

    /**
     * return position of a specific player in the ranking of games with number of
     * players equals numPlayers.
     * If two or more player have the same score they are at the same ranking position
     * @param playerName name of the player
     * @param numPlayers type of game (number of players in the game)
     * @return position or -1 an error occurred
     */
    public int getPlayerPosition(String playerName, int numPlayers) {
        String sql = """
                SELECT   g1.nickname,
                         g1.num_players,
                    COUNT(*) AS wins,
                    (SELECT SUM(g2.score)
                    FROM   game g2
                    WHERE  g2.nickname = g1.nickname
                    AND    g2.num_players = ?) AS total_score
                FROM     game g1
                WHERE    g1.num_players = ? AND g1.winner = TRUE
                GROUP BY g1.nickname
                ORDER BY wins DESC, total_score DESC
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numPlayers);
            ps.setInt(2, numPlayers);
            ResultSet rs = ps.executeQuery();
            int position = 1;
            int rowCount = 0;
            int lastWins = Integer.MAX_VALUE;
            int lastTotalScore = Integer.MAX_VALUE;
            while (rs.next()) {
                rowCount++;
                int currentWins = rs.getInt("wins");
                int currentTotalScore = rs.getInt("total_score");
                if(currentWins < lastWins || (currentWins == lastWins && currentTotalScore < lastTotalScore)){
                    position = rowCount;
                    lastWins = currentWins;
                    lastTotalScore = currentTotalScore;
                }
                if (rs.getString("nickname").equals(playerName))
                    return position;
            }
        } catch (SQLException e) {
            System.err.println("Database error: getPosition failed: " + e.getMessage());
        }
        return -1;
    }
}
