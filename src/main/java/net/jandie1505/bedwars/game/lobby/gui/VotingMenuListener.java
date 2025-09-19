package net.jandie1505.bedwars.game.lobby.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.lobby.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class VotingMenuListener implements ManagedListener {
    @NotNull private final Lobby lobby;

    public VotingMenuListener(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!this.lobby.isPlayerIngame(event.getPlayer())) return;
        if (!event.getAction().isRightClick()) return;

        int clickedItemId = this.lobby.getPlugin().getItemStorage().getItemId(event.getItem());
        if (clickedItemId < 0) return;
        if (clickedItemId != this.lobby.getMapVoteButtonItemId()) return;

        event.setCancelled(true);

        if (!this.lobby.isMapVoting()) {
            event.getPlayer().sendRichMessage("<red>Map voting is currently disabled");
            event.getPlayer().closeInventory();
            return;
        }

        if (this.lobby.getSelectedMap() != null) {
            event.getPlayer().sendRichMessage("<red>Map voting is already over");
            event.getPlayer().closeInventory();
            return;
        }

        event.getPlayer().openInventory(this.lobby.getMapVoteGUI().getInventory(event.getPlayer()));
    }

    // ----- OTHER -----

    public @NotNull Lobby getLobby() {
        return lobby;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
