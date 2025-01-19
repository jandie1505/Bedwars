package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.base.GameBase;
import net.jandie1505.bedwars.base.GameInstance;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Lobby extends GameBase {

    public Lobby(@NotNull GameInstance instance, @NotNull World world) {
        super(instance, world);
    }

    @Override
    public boolean isPlayerIngame(@Nullable UUID playerId) {
        return false;
    }

}
