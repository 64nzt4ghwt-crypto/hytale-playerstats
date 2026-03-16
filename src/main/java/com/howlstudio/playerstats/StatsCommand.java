package com.howlstudio.playerstats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StatsCommand extends AbstractPlayerCommand {
    private final StatsManager manager;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        .withZone(ZoneId.systemDefault());

    public StatsCommand(StatsManager manager) {
        super("stats", "View your stats or another player's. Usage: /stats [player]");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        UUID uuid = playerRef.getUuid();
        if (uuid == null) return;

        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+");

        PlayerData data;
        if (parts.length >= 2) {
            data = manager.findByName(parts[1]);
            if (data == null) {
                playerRef.sendMessage(Message.raw("§c[Stats] Player not found: §f" + parts[1]));
                return;
            }
        } else {
            data = manager.getOrCreate(uuid, playerRef.getUsername() != null ? playerRef.getUsername() : "?");
        }

        playerRef.sendMessage(Message.raw("§6§l--- Stats: " + data.getName() + " ---"));
        playerRef.sendMessage(Message.raw("§7Kills: §e" + data.getKills()
            + "  §7Deaths: §e" + data.getDeaths()
            + "  §7KDR: §e" + data.getKdr()));
        playerRef.sendMessage(Message.raw("§7Best Streak: §e" + data.getBestKillStreak()
            + "  §7Current Streak: §e" + data.getCurrentKillStreak()));
        playerRef.sendMessage(Message.raw("§7Playtime: §e" + data.getFormattedPlaytime()
            + "  §7Logins: §e" + data.getLogins()));
        playerRef.sendMessage(Message.raw("§7Blocks Placed: §e" + data.getBlocksPlaced()
            + "  §7Broken: §e" + data.getBlocksBroken()));
        playerRef.sendMessage(Message.raw("§7First Seen: §e" + DATE_FMT.format(data.getFirstSeen())
            + "  §7Last Seen: §e" + DATE_FMT.format(data.getLastSeen())));
    }
}
