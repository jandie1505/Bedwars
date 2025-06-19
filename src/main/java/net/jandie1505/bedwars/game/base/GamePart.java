package net.jandie1505.bedwars.game.base;

import net.chaossquad.mclib.executable.CoreExecutable;
import net.jandie1505.bedwars.Bedwars;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class GamePart extends CoreExecutable {
    @NotNull private final Bedwars plugin;

    public GamePart(Bedwars plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
    }

    // ----- PLAYERS -----

    /**
     * Returns a set of all player UUIDs currently registered as ingame.<br/>
     * @return set of all ingame player uuids
     */
    public abstract Set<UUID> getRegisteredPlayers();

    /**
     * Returns a list of all ingame players which are currently online.
     * @return ingame online players
     */
    public final @NotNull List<@NotNull Player> getOnlinePlayers() {
        return this.getRegisteredPlayers().stream().map(uuid -> this.getPlugin().getServer().getPlayer(uuid)).filter(Objects::nonNull).toList();
    }

    /**
     * Returns true if the player with the specified uuid is ingame.
     * @param playerId player uuid
     * @implNote RESULT SHOULD NOT DIFFER FROM A CONTAINS CHECK ON {@link GamePart#getRegisteredPlayers()}!!!
     * @return player ingame
     */
    public abstract boolean isPlayerIngame(@Nullable UUID playerId);

    /**
     * Returns true if the specified player is ingame
     * @param player player
     * @return ingame
     */
    public final boolean isPlayerIngame(@Nullable OfflinePlayer player) {
        if (player == null) return false;
        return this.isPlayerIngame(player.getUniqueId());
    }

    // ----- NEXT STATUS -----

    public abstract @Nullable GamePart getNextStatus();

    // ----- OTHER

    public final @NotNull Bedwars getPlugin() {
        return this.plugin;
    }

}
