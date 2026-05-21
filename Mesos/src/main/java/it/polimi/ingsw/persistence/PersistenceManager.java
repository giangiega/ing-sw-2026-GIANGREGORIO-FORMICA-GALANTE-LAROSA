package it.polimi.ingsw.persistence;

import com.google.gson.Gson;
import it.polimi.ingsw.network.socket.GsonFactory;

import java.io.*;
import java.nio.file.*;

/**
 * Handles atomic persistence of SavedGameState.
 * Usage:
 *   PersistenceManager.save(state)   — called by GameController after endRound()
 *   PersistenceManager.load()        — called by ServerApp on startup
 *   PersistenceManager.clear()       — called by GameController in endGame()
 */
public class PersistenceManager {

    private static final String SAVE_FILE = "game_save.json";
    private static final String TEMP_FILE = "game_save.tmp";

    private PersistenceManager() {}

    /**
     * Serializes SavedGameState using a .tmp file.
     * Then it does an atomic move to the .json file.
     * In this way I'm sure that the copy of the Game is complete and not corrupted.
     *
     * @param state the current game state to persist
     * @throws IOException if the file cannot be written
     */
    public static void save(SavedGameState state) throws IOException {
        Gson gson = GsonFactory.persistenceGson();
        String json = gson.toJson(state);

        Path tempPath = Paths.get(TEMP_FILE);
        Path savePath = Paths.get(SAVE_FILE);

        // Write to temp first
        Files.writeString(tempPath, json);

        // Atomic rename: if crash happens before this line, old save is still intact
        Files.move(tempPath, savePath, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Loads and deserializes the saved game state from disk.
     * Returns null if no save file exists (normal first-run scenario).
     *
     * @return the deserialized SavedGameState, or null if no save file is found
     * @throws IOException if the file exists but cannot be read or parsed
     */
    public static SavedGameState load() throws IOException {
        Path savePath = Paths.get(SAVE_FILE);

        if (!Files.exists(savePath)) {
            return null;
        }

        Gson gson = GsonFactory.persistenceGson();
        String json = Files.readString(savePath);
        return gson.fromJson(json, SavedGameState.class);
    }

    /**
     * Deletes the save file from disk.
     * Must be called in endGame()
     */
    public static void clear() {
        try {
            Files.deleteIfExists(Paths.get(SAVE_FILE));
            Files.deleteIfExists(Paths.get(TEMP_FILE)); // cleanup orphan temp if any
        } catch (IOException e) {
            // Non-critical: log and continue
            System.err.println("[PersistenceManager] Warning: could not delete save file: " + e.getMessage());
        }
    }

    /**
     * @return true if a save file exists.
     */
    public static boolean hasSave() {
        return Files.exists(Paths.get(SAVE_FILE));
    }
}
