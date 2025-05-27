package net.jandie1505.bedwars.game.utils;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.base.GamePart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A listener for {@link net.jandie1505.bedwars.game.lobby.Lobby} and {@link net.jandie1505.bedwars.game.endlobby.Endlobby} which prevents players from doing certain things.
 */
public class LobbyProtectionsListener implements ManagedListener {
    @NotNull private final GamePart game;

    public LobbyProtectionsListener(@NotNull GamePart game) {
        this.game = game;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (!this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
