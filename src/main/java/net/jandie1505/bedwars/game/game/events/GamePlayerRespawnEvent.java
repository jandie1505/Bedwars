package net.jandie1505.bedwars.game.game.events;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a player respawns in the bedwars game.
 */
public class GamePlayerRespawnEvent extends Event implements Cancellable {
    @NotNull private static final HandlerList HANDLER_LIST = new HandlerList();
    @NotNull private final Game game;
    @NotNull private final Player player;
    @NotNull private final PlayerData playerData;
    boolean cancelled;

    public GamePlayerRespawnEvent(@NotNull Game game, @NotNull Player player, @NotNull PlayerData playerData) {
        this.game = game;
        this.player = player;
        this.playerData = playerData;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
