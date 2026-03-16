package com.howlstudio.playerstats;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatsManager {

    private final Path dataDir;
    private final Map<UUID, PlayerData> players = new ConcurrentHashMap<>();

    public StatsManager(Path dataDir) {
        this.dataDir = dataDir;
        load();
    }

    public PlayerData getOrCreate(UUID uuid, String name) {
        return players.computeIfAbsent(uuid, k -> new PlayerData(k, name));
    }

    public PlayerData get(UUID uuid) { return players.get(uuid); }

    public PlayerData findByName(String name) {
        return players.values().stream()
            .filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst().orElse(null);
    }

    /** Top N players by a stat category. */
    public List<PlayerData> getLeaderboard(String category, int limit) {
        Comparator<PlayerData> comparator = switch (category.toLowerCase()) {
            case "kills"    -> Comparator.comparingInt(PlayerData::getKills).reversed();
            case "deaths"   -> Comparator.comparingInt(PlayerData::getDeaths).reversed();
            case "kdr"      -> Comparator.comparingDouble(PlayerData::getKdr).reversed();
            case "streak"   -> Comparator.comparingInt(PlayerData::getBestKillStreak).reversed();
            case "playtime" -> Comparator.comparingLong(PlayerData::getTotalPlaytimeSeconds).reversed();
            case "blocks"   -> Comparator.comparingInt(PlayerData::getBlocksPlaced).reversed();
            case "logins"   -> Comparator.comparingInt(PlayerData::getLogins).reversed();
            default         -> Comparator.comparingInt(PlayerData::getKills).reversed();
        };
        return players.values().stream()
            .sorted(comparator)
            .limit(limit)
            .collect(Collectors.toList());
    }

    public void save() {
        try {
            Files.createDirectories(dataDir);
            try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(dataDir.resolve("stats.dat")))) {
                oos.writeObject(new HashMap<>(players));
            }
        } catch (IOException ignored) {}
    }

    @SuppressWarnings("unchecked")
    private void load() {
        Path file = dataDir.resolve("stats.dat");
        if (!Files.exists(file)) return;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            players.putAll((Map<UUID, PlayerData>) ois.readObject());
        } catch (Exception ignored) {}
    }
}
