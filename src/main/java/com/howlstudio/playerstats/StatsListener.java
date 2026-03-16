package com.howlstudio.playerstats;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.time.Instant;
import java.util.UUID;

public class StatsListener {

    private final StatsManager manager;

    public StatsListener(StatsManager manager) {
        this.manager = manager;
    }

    public void register() {
        var bus = HytaleServer.get().getEventBus();
        bus.registerGlobal(PlayerReadyEvent.class, this::onJoin);
        bus.registerGlobal(PlayerDisconnectEvent.class, this::onLeave);
    }

    private void onJoin(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        PlayerRef ref = player.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        if (uuid == null) return;
        String name = ref.getUsername() != null ? ref.getUsername() : "Unknown";

        PlayerData data = manager.getOrCreate(uuid, name);
        data.setName(name);
        data.setLastSeen(Instant.now());
        data.startSession();
    }

    private void onLeave(PlayerDisconnectEvent event) {
        PlayerRef ref = event.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        if (uuid == null) return;

        PlayerData data = manager.get(uuid);
        if (data != null) {
            data.endSession();
            data.setLastSeen(Instant.now());
            manager.save();
        }
    }
}
