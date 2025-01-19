package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.base.GameBase;
import net.jandie1505.bedwars.base.GameInstance;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class Game extends GameBase {
    private final HashMap<UUID, PlayerData> players;

    public Game(@NotNull GameInstance instance, @NotNull World world) {
        super(instance, world);
        this.players = new HashMap<>();
    }

    @Override
    public boolean isPlayerIngame(@Nullable UUID playerId) {
        return false;
    }

}
