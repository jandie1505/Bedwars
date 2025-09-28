package net.jandie1505.bedwars.game.game.team.traps;

import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class TeamTrap implements Removable {
    @NotNull private final TeamTrapManager manager;
    @NotNull private final String id;

    public TeamTrap(@NotNull TeamTrapManager manager, @NotNull String id) {
        this.manager = manager;
        this.id = id;
    }

    // ----- TRIGGER -----

    @ApiStatus.OverrideOnly
    public void onTrigger(@NotNull Player player, @NotNull PlayerData playerData) {}

    // ----- OTHER -----

    public final @NotNull TeamTrapManager getManager() {
        return this.manager;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public final boolean isRegistered() {
        return this.manager.getTrap(this.id) == this;
    }

    public final void remove() {
        this.manager.removeTrap(this.id);
    }

    @Override
    public final boolean toBeRemoved() {
        return !this.isRegistered();
    }

}
