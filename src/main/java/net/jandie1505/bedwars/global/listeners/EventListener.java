package net.jandie1505.bedwars.global.listeners;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.endlobby.Endlobby;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.lobby.Lobby;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EventListener implements Listener {
    @NotNull private final Bedwars plugin;

    public EventListener(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    // ----- BYPASSING PLAYERS -----

    /**
     * Un-cancel BlockPlaceEvents for bypassing players, allowing them to always build.
     * @param event BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceForBypassingPlayers(BlockPlaceEvent event) {
        if (!this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        event.setCancelled(false);
    }

    /**
     * Un-cancel BlockBreakEvent for bypassing players, allowing them to always break blocks.
     * @param event BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakForBypassingPlayers(BlockBreakEvent event) {
        if (!this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        event.setCancelled(false);
    }

    /**
     * Un-cancel InventoryClickEvents for the own inventory, allowing them to always use their inventory.
     * @param event InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClickForBypassingPlayers(InventoryClickEvent event) {
        if (!this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        if (event.getInventory().getHolder() != event.getWhoClicked()) return;
        event.setCancelled(false);
    }

    /**
     * Un-cancel InventoryDragEvents for the own inventory, allowing them to always use their inventory.
     * @param event InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDragForBypassingPlayers(InventoryDragEvent event) {
        if (!this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        if (event.getInventory().getHolder() != event.getWhoClicked()) return;
        event.setCancelled(false);
    }

    /**
     * Un-cancel EntityDamageEvents if the player is bypassing or the damager is bypassing
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageForBypassingPlayers(EntityDamageEvent event) {

        // Un-cancel the event if the player is bypassing
        if (event.getEntity() instanceof Player player && this.plugin.isPlayerBypassing(player)) {
            event.setCancelled(false);
            return;
        }

        // Un-cancel the event if the damager is bypassing
        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player player && this.plugin.isPlayerBypassing(player)) {
            event.setCancelled(false);
            return;
        }

    }

    // ----- PROTECTIONS -----
    // This section contains event listeners preventing players from doing certain stuff.
    // They only should do things when the player is bypassing, the game is not running or the game is paused.
    // If those three things are not the case, these listeners should not do anything because the gamemode has to handle this then.

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (this.plugin.isPlayerBypassing(player.getUniqueId())) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (this.plugin.isPlayerBypassing(player.getUniqueId())) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        if (this.allowPlayerEvent(event)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;
        if (this.allowPlayerEvent(event)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;

        // Protections when no game is running
        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        // Protections when game is paused
        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;

        // Protections when no game is running
        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        // Protections when game is paused
        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.allowPlayerEvent(event)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        if (this.allowPlayerEvent(event)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        if (this.allowPlayerEvent(event)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamageForNotIngameProtection(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerMoveForPausedGamesEvent(PlayerMoveEvent event) {
        if (this.plugin.getGame() == null) return;
        if (!this.plugin.isPaused()) return;
        if (this.plugin.isPlayerBypassing(event.getPlayer())) return;

        event.setCancelled(true);
    }

    // ----- SPECIAL -----

    /**
     * Un-cancels void damage, because void damage MUST NOT be cancelled.
     * @param event event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageForUnCancellingVoidDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;
        event.setCancelled(false); // Void damage MUST NOT be cancelled
    }

    // ----- CHECKS -----

    public boolean allowPlayerEvent(PlayerEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer())) {
            return true;
        }

        return this.plugin.getGame() != null && !this.plugin.isPaused();
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();

        for (String criteria : advancement.getCriteria()) {
            event.getPlayer().getAdvancementProgress(advancement).revokeCriteria(criteria);
        }

    }

    // ----- NOT REFACTORED -----

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        // slot of the slot system (+ bypassed join for admins)
        // bypassed join: admins can join without being added to the game (bypassed players will not be added to the game)

        boolean isFullThroughSlotSystem = false;

        if (this.plugin.getGame() instanceof Lobby && ((Lobby) this.plugin.getGame()).getMaxPlayers() > 0 && ((Lobby) this.plugin.getGame()).getPlayers().size() >= ((Lobby) this.plugin.getGame()).getMaxPlayers() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (event.getPlayer().hasPermission("bedwars.admin")) {

                if (((Lobby) this.plugin.getGame()).getLoginBypassList().contains(event.getPlayer().getUniqueId())) {

                    ((Lobby) this.plugin.getGame()).removeLoginBypassPlayer(event.getPlayer().getUniqueId());
                    this.plugin.addBypassingPlayer(event.getPlayer().getUniqueId());
                    event.getPlayer().sendMessage("§aAuto-bypass enabled");

                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_FULL, "§cServer is currently full.\nReconnect to join auto-bypassed.");
                    isFullThroughSlotSystem = true;
                }

            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "§cServer is currently full");
                isFullThroughSlotSystem = true;
            }

        }

        // check if player is getting kicked because server is full

        if (event.getResult() != PlayerLoginEvent.Result.KICK_FULL) {
            return;
        }

        // check if the player can bypass a full server

        if (!event.getPlayer().hasPermission("bedwars.admin") && !event.getPlayer().hasPermission("bedwars.fulljoin")) {
            return;
        }

        // find a target to kick so that the prioritized player can join

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

            // player is not a target if the slots were set by the slot system and the player is not ingame
            // this prevents having more ingame players on the server than there are slots (of the slot system) available
            if (this.plugin.getGame() instanceof Lobby && isFullThroughSlotSystem && !((Lobby) this.plugin.getGame()).getPlayers().containsKey(player.getUniqueId())) {
                continue;
            }

            // player is not a target if the player is ingame (it would be illogical to kick an ingame player that a spectator can join)
            if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(player.getUniqueId())) {
                continue;
            }

            // admins cannot be kicked
            if (player.hasPermission("bedwars.admin")) {
                continue;
            }

            // fulljoin players cannot kick other fulljoin players, but admins can kick other fulljoin players
            if (player.hasPermission("bedwars.fulljoin") && !event.getPlayer().hasPermission("bedwars.admin")) {
                continue;
            }

            // kick the target if one was found and allow the player to log in
            player.kickPlayer("§cYou were kicked to make room for a player with higher priority");
            event.allow();

            break;
        }

    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {

        if (this.plugin.getGame() instanceof Lobby) {

            int maxPlayers = ((Lobby) this.plugin.getGame()).getMaxPlayers();

            if (maxPlayers < 0) {
                event.setMaxPlayers(maxPlayers);
            }

            if (((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                event.setMotd(((Lobby) this.plugin.getGame()).getSelectedMap().world());
            } else {
                event.setMotd("MAP VOTING");
            }

            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            event.setMotd("INGAME");

            return;
        }

        if (this.plugin.getGame() instanceof Endlobby) {

            event.setMotd("ENDLOBBY");

            return;
        }

    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(this.plugin.getGame() instanceof Game game)) return;
        if (event.getEntity().getWorld() != game.getWorld()) return;
        event.getEntity().setPersistent(false);
    }

    /**
     * Returns the game from the plugin.
     * @deprecated Use {@link Bedwars#getGame()}.
     * @return game
     */
    @Deprecated(forRemoval = true)
    public @Nullable GamePart getGame() {
        return this.plugin.getGame();
    }

}
