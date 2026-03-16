package com.howlstudio.playerstats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;

public class LeaderboardCommand extends AbstractPlayerCommand {
    private final StatsManager manager;

    public LeaderboardCommand(StatsManager manager) {
        super("leaderboard", "View server leaderboards. Usage: /leaderboard [kills|deaths|kdr|streak|playtime|blocks|logins]");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+");
        String category = parts.length >= 2 ? parts[1].toLowerCase() : "kills";

        List<PlayerData> top = manager.getLeaderboard(category, 10);

        String catDisplay = switch (category) {
            case "kdr"      -> "K/D Ratio";
            case "streak"   -> "Best Kill Streak";
            case "playtime" -> "Playtime";
            case "blocks"   -> "Blocks Placed";
            case "logins"   -> "Most Logins";
            case "deaths"   -> "Deaths";
            default         -> "Kills";
        };

        playerRef.sendMessage(Message.raw("§6§l--- Top 10: " + catDisplay + " ---"));
        if (top.isEmpty()) {
            playerRef.sendMessage(Message.raw("§7No data yet. Play to earn a spot!"));
            return;
        }

        for (int i = 0; i < top.size(); i++) {
            PlayerData p = top.get(i);
            String medal = switch (i) {
                case 0 -> "§6#1 ";
                case 1 -> "§7#2 ";
                case 2 -> "§c#3 ";
                default -> "§7#" + (i + 1) + " ";
            };
            String value = switch (category) {
                case "kdr"      -> String.valueOf(p.getKdr());
                case "streak"   -> String.valueOf(p.getBestKillStreak());
                case "playtime" -> p.getFormattedPlaytime();
                case "blocks"   -> String.valueOf(p.getBlocksPlaced());
                case "logins"   -> String.valueOf(p.getLogins());
                case "deaths"   -> String.valueOf(p.getDeaths());
                default         -> String.valueOf(p.getKills());
            };
            playerRef.sendMessage(Message.raw(medal + "§f" + p.getName() + " §7— §e" + value));
        }
        playerRef.sendMessage(Message.raw("§7Categories: kills, deaths, kdr, streak, playtime, blocks, logins"));
    }
}
