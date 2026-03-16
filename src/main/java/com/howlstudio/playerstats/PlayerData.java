package com.howlstudio.playerstats;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID uuid;
    private String name;

    // Combat
    private int kills;
    private int deaths;
    private int currentKillStreak;
    private int bestKillStreak;

    // Time
    private long totalPlaytimeSeconds;
    private transient Instant sessionStart;

    // General
    private int blocksPlaced;
    private int blocksBroken;
    private int chatMessages;
    private int logins;
    private Instant firstSeen;
    private Instant lastSeen;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.firstSeen = Instant.now();
        this.lastSeen = Instant.now();
        this.sessionStart = Instant.now();
    }

    // --- Session ---
    public void startSession() { sessionStart = Instant.now(); logins++; lastSeen = Instant.now(); }
    public void endSession() {
        if (sessionStart != null) {
            totalPlaytimeSeconds += Instant.now().getEpochSecond() - sessionStart.getEpochSecond();
            sessionStart = null;
        }
    }

    // --- Combat ---
    public void addKill() { kills++; currentKillStreak++; if (currentKillStreak > bestKillStreak) bestKillStreak = currentKillStreak; }
    public void addDeath() { deaths++; currentKillStreak = 0; }

    // --- General ---
    public void addBlockPlaced() { blocksPlaced++; }
    public void addBlockBroken() { blocksBroken++; }
    public void addChatMessage() { chatMessages++; }

    // --- Getters ---
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getCurrentKillStreak() { return currentKillStreak; }
    public int getBestKillStreak() { return bestKillStreak; }
    public long getTotalPlaytimeSeconds() { return totalPlaytimeSeconds; }
    public int getBlocksPlaced() { return blocksPlaced; }
    public int getBlocksBroken() { return blocksBroken; }
    public int getChatMessages() { return chatMessages; }
    public int getLogins() { return logins; }
    public Instant getFirstSeen() { return firstSeen; }
    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant t) { this.lastSeen = t; }

    public double getKdr() {
        return deaths == 0 ? kills : Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    public String getFormattedPlaytime() {
        long secs = totalPlaytimeSeconds;
        if (sessionStart != null) secs += Instant.now().getEpochSecond() - sessionStart.getEpochSecond();
        long hours = secs / 3600;
        long mins = (secs % 3600) / 60;
        return hours + "h " + mins + "m";
    }
}
