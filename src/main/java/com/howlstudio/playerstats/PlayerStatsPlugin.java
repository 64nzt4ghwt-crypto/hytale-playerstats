package com.howlstudio.playerstats;

import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * PlayerStats — Complete player statistics tracking for Hytale servers.
 *
 * Tracks per-player: kills, deaths, KDR, kill streaks, playtime,
 * blocks placed/broken, chat messages, logins, first/last seen.
 *
 * Commands:
 *   /stats [player]          — view your stats or another player's
 *   /leaderboard [category]  — top 10 by: kills|deaths|kdr|streak|playtime|blocks|logins
 *
 * Data persists across restarts (stats.dat). Auto-saves every 5 minutes.
 */
public final class PlayerStatsPlugin extends JavaPlugin {

    private StatsManager manager;
    private ScheduledExecutorService autosave;

    public PlayerStatsPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[PlayerStats] Loading...");

        manager = new StatsManager(getDataDirectory());

        CommandManager cmd = CommandManager.get();
        cmd.register(new StatsCommand(manager));
        cmd.register(new LeaderboardCommand(manager));

        new StatsListener(manager).register();

        // Auto-save every 5 minutes
        autosave = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "stats-autosave");
            t.setDaemon(true);
            return t;
        });
        autosave.scheduleAtFixedRate(manager::save, 5, 5, TimeUnit.MINUTES);

        System.out.println("[PlayerStats] Ready! Tracking stats for " + manager.getLeaderboard("kills", Integer.MAX_VALUE).size() + " players.");
    }

    @Override
    protected void shutdown() {
        if (autosave != null) autosave.shutdownNow();
        if (manager != null) {
            manager.save();
            System.out.println("[PlayerStats] Saved and stopped.");
        }
    }
}
