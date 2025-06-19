package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class GameProtectionsForNotIngamePlayersListener implements ManagedListener {
    @NotNull private final Game game;

    public GameProtectionsForNotIngamePlayersListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (!this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() != Event.Result.DENY || event.useItemInHand() != Event.Result.DENY) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (this.game.isPlayerIngame(player)) return;
        event.setCancelled(true);
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
