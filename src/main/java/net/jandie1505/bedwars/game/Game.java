package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.base.GameBase;
import net.jandie1505.bedwars.base.GameInstance;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Game extends GameBase {
    @NotNull private final HashMap<UUID, PlayerData> players;

    public Game(@NotNull GameInstance instance, @NotNull World world) {
        super(instance, world);
        this.players = new HashMap<>();
    }

    // ----- PLAYERS -----

    @Override
    public final boolean isPlayerIngame(@Nullable UUID playerId) {
        return this.players.containsKey(playerId);
    }

    @Override
    public final @NotNull Set<UUID> getRegisteredPlayers() {
        return Set.copyOf(this.players.keySet());
    }

    public final @NotNull Map<UUID, PlayerData> getPlayerDataMap() {
        return Map.copyOf(this.players);
    }

}
