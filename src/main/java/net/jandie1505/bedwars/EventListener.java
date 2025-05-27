package net.jandie1505.bedwars;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.game.endlobby.Endlobby;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.entities.BridgeEgg;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.jandie1505.bedwars.game.lobby.LobbyPlayerData;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.lobby.inventory.VotingMenu;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;

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
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;

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
        if (this.plugin.isPlayerBypassing(event.getPlayer())) return;

        if (this.plugin.getGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        if (this.plugin.isPlayerBypassing(event.getPlayer())) return;

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

    // ----- NOT REFACTORED -----

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {

        if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Egg) {

            if (!(this.plugin.getGame() instanceof Game)) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(((Egg) event.getEntity()).getItem());

            if (itemId < 0) {
                return;
            }

            if (((Game) this.plugin.getGame()).getItemShop().getBridgeEgg() == null || itemId != ((Game) this.plugin.getGame()).getItemShop().getBridgeEgg()) {
                return;
            }

            if (event.getEntity().getShooter() == null) {
                return;
            }

            if (!(event.getEntity().getShooter() instanceof Player)) {
                return;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(((Player) event.getEntity().getShooter()).getUniqueId());

            if (playerData == null) {
                return;
            }

            BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

            if (team == null) {
                return;
            }

            Material material = Material.getMaterial(Bedwars.getBlockColorString(team.getData().chatColor()) + "_WOOL");

            if (material == null) {
                return;
            }

            Location location = event.getEntity().getLocation();

            new BridgeEgg((Game) this.plugin.getGame(), location, material);
            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            return;
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

        if (playerData == null) {
            return;
        }

        if (event.getItem().getType() == Material.MILK_BUCKET) {

            event.setCancelled(true);

            Bedwars.removeSpecificAmountOfItems(event.getPlayer().getInventory(), Material.MILK_BUCKET, 1);
            playerData.setMilkTimer(30*20);
            event.getPlayer().sendMessage("§bMilk activated");

            return;
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {

            if (event.getDamage() < 100) {
                event.setDamage(100);
            }

            return;
        }

        if (this.plugin.getGame() != null && this.plugin.isPaused() && (!this.plugin.isPlayerBypassing(event.getEntity().getUniqueId()) || !(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player && !this.plugin.isPlayerBypassing(((EntityDamageByEntityEvent) event).getDamager().getUniqueId())))) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            if (event.getEntity().getWorld() != ((Game) this.plugin.getGame()).getWorld()) {
                return;
            }

            if (this.plugin.isPlayerBypassing(event.getEntity().getUniqueId())) {
                return;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getUniqueId());

            if (playerData == null) {
                event.setCancelled(true);
                return;
            }

            if (event instanceof EntityDamageByEntityEvent) {

                Entity customDamager = null;

                if (((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile) {

                    if (((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter() instanceof Entity) {
                        customDamager = (Entity) ((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter();
                    }

                }

                if (customDamager == null) {
                    customDamager = ((EntityDamageByEntityEvent) event).getDamager();
                }

                if (customDamager instanceof Player) {

                    if (this.plugin.isPlayerBypassing(((EntityDamageByEntityEvent) event).getDamager().getUniqueId())) {
                        return;
                    }

                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayers().get(customDamager.getUniqueId());

                    if (damagerData == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if (playerData.getTeam() == damagerData.getTeam()) {
                        event.setCancelled(true);
                        return;
                    }

                    if(playerData.getTeleportToBaseCooldown() > 0) {
                        Player player = (Player) event.getEntity();
                        player.sendMessage("§cTeleport cenceled");
                        playerData.setTeleportToBaseCooldown(0);
                    }

                }

            }

        } else {

            if (this.plugin.isPlayerBypassing(event.getEntity().getUniqueId())) {
                return;
            }

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();

        for (String criteria : advancement.getCriteria()) {
            event.getPlayer().getAdvancementProgress(advancement).revokeCriteria(criteria);
        }

    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        if (this.plugin.getGame() instanceof Game) {
            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

            if (playerData != null) {
                BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

                if (team == null) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getMessage().startsWith("@everyone ") || event.getMessage().startsWith("@shout ") || event.getMessage().startsWith("@global ") || event.getMessage().startsWith("@all ")) {
                    if (event.getMessage().startsWith("@everyone ")) {
                        event.setMessage(event.getMessage().substring(10));
                    } else if (event.getMessage().startsWith("@shout ")) {
                        event.setMessage(event.getMessage().substring(7));
                    } else if (event.getMessage().startsWith("@global ")) {
                        event.setMessage(event.getMessage().substring(8));
                    } else if (event.getMessage().startsWith("@all ")) {
                        event.setMessage(event.getMessage().substring(5));
                    }

                    event.setFormat("§7[§6GLOBAL§7] " + team.getData().chatColor() + "%1$s§7: §7%2$s");
                } else {
                    event.setFormat("§7[" + team.getData().chatColor() + team.getData().name() + "§7] " + team.getData().chatColor() + "%1$s§7: §7%2$s");

                    event.getRecipients().clear();

                    for (Player recipientPlayer : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {
                        PlayerData recipientPlayerData = ((Game) this.plugin.getGame()).getPlayers().get(recipientPlayer.getUniqueId());

                        if (playerData.getTeam() == recipientPlayerData.getTeam()) {
                            event.getRecipients().add(recipientPlayer);
                        }

                    }
                }

            } else {
                event.setFormat("§r§7[§7§oSPECTATOR§r§7] §7%1$s§7: §7%2$s");

                event.getRecipients().clear();

                for (Player recipientPlayer : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {
                    PlayerData recipientPlayerData = ((Game) this.plugin.getGame()).getPlayers().get(recipientPlayer.getUniqueId());

                    if (recipientPlayerData == null) {
                        event.getRecipients().add(recipientPlayer);
                    }

                }
            }

        } else {
            event.setFormat("§7%1$s§7: §7%2$s");
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {

        if (!(this.plugin.getGame() instanceof Game)) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // teleport

        if (this.plugin.getGame() instanceof Lobby) {

            event.getPlayer().teleport(((Lobby) this.plugin.getGame()).getLobbySpawn());

        }

        // message

        if (this.plugin.getGame() instanceof Game) {
            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

            if (playerData == null) {
                event.setJoinMessage("");
                return;
            }

            BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

            if (team == null) {
                event.setJoinMessage("");
                return;
            }

            event.setJoinMessage(team.getData().chatColor() + event.getPlayer().getDisplayName() + " §7reconnected");

        } else {
            event.setJoinMessage("§e" + event.getPlayer().getDisplayName() + " §7joined the game");
        }

    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {

        if (this.plugin.getGame() instanceof Game) {
            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

            if (playerData == null) {
                event.setQuitMessage("");
                return;
            }

            BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

            if (team == null) {
                event.setQuitMessage("");
                return;
            }

            event.setQuitMessage(team.getData().chatColor() + event.getPlayer().getDisplayName() + " §7disconnected");

        } else {
            event.setQuitMessage("§e" + event.getPlayer().getDisplayName() + " §7left the game");
        }

    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {

        if (event.getEntity() instanceof Snowball) {

            if (event.getHitEntity() instanceof Player) {

                event.setCancelled(true);
                event.getHitEntity().setVelocity(event.getEntity().getVelocity().clone().multiply(1.5));
                event.getEntity().remove();

            } else if (event.getHitEntity() instanceof IronGolem || event.getHitEntity() instanceof TNTPrimed) {

                event.setCancelled(true);
                event.getHitEntity().setVelocity(event.getEntity().getVelocity().clone().multiply(2));
                event.getEntity().remove();

            } else if (event.getHitEntity() instanceof Fireball) {

                event.setCancelled(true);
                ((Fireball) event.getHitEntity()).setDirection(new Vector(0, 0, 0));
                event.getHitEntity().setVelocity(event.getEntity().getVelocity().clone().multiply(4));
                event.getEntity().remove();

            }

            return;

        } else if (event.getEntity() instanceof EnderPearl) {

            if (event.getHitEntity() instanceof LivingEntity && event.getEntity().getShooter() instanceof Player) {

                Location firstLocation = event.getHitEntity().getLocation().clone();
                Location secondLocation = ((Player) event.getEntity().getShooter()).getLocation().clone();

                event.getHitEntity().teleport(secondLocation);
                ((Player) event.getEntity().getShooter()).teleport(firstLocation);

            }

        }

    }

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
