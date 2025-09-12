package net.jandie1505.bedwars.game.base;

import net.chaossquad.mclib.command.DynamicSubcommandProvider;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.executable.CoreExecutable;
import net.jandie1505.bedwars.Bedwars;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class GamePart extends CoreExecutable implements DynamicSubcommandProvider {
    @NotNull private final Bedwars plugin;
    @NotNull private final Map<String, SubcommandEntry> dynamicSubcommands;

    public GamePart(Bedwars plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
        this.dynamicSubcommands = new HashMap<>();
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

    // ----- SUBCOMMANDS -----

    /**
     * Returns a list of gamemode-specific subcommands.
     * NEVER REGISTER THESE COMMANDS AS PLUGIN COMMANDS.
     * REGISTERING THEM AS PLUGIN COMMANDS WILL PREVENT THE GARBAGE COLLECTOR FROM CLEANING THE GAME UP WHEN IT IS OVER.
     * @return map of subcommands
     */
    public final Map<String, SubcommandEntry> getDynamicSubcommands() {
        return Map.copyOf(this.dynamicSubcommands);
    }

    /**
     * Registers a new dynamic subcommand.
     * @param command command string
     * @param entry command entry
     */
    public final void addDynamicSubcommand(String command, SubcommandEntry entry) {
        this.dynamicSubcommands.put(command, entry);
    }

    /**
     * Unregisters a new dynamic subcommand.
     * @param command command string
     */
    public final void removeDynamicSubcommand(String command) {
        this.dynamicSubcommands.remove(command);
    }

    /**
     * Unregisters all dynamic subcommands.
     */
    public final void clearDynamicSubcommands() {
        this.dynamicSubcommands.clear();
    }

    // ----- NEXT STATUS -----

    public abstract @Nullable GamePart getNextStatus();

    // ----- OTHER

    public final @NotNull Bedwars getPlugin() {
        return this.plugin;
    }

}
