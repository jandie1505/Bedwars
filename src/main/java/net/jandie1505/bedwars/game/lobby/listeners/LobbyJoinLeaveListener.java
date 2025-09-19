package net.jandie1505.bedwars.game.lobby.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class LobbyJoinLeaveListener implements ManagedListener {
    @NotNull private final Lobby lobby;

    public LobbyJoinLeaveListener(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (this.lobby.getPlugin().isPlayerBypassing(event.getPlayer())) {
            event.joinMessage(null);
            return;
        }

        event.joinMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has joined", NamedTextColor.GRAY))
        );

        event.getPlayer().teleport(this.lobby.getLobbySpawn().clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (this.lobby.getPlugin().isPlayerBypassing(event.getPlayer())) {
            event.quitMessage(null);
            return;
        }

        event.quitMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has left", NamedTextColor.GRAY))
        );
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    public @NotNull Lobby getLobby() {
        return lobby;
    }

}
