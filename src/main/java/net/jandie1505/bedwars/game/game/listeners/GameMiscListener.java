package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameMiscListener implements ManagedListener {
    @NotNull private final Game game;

    public GameMiscListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onBlockBreakForBreakingBed(@NotNull BlockBreakEvent event) {

        PlayerData playerData = this.game.getPlayerData(event.getPlayer().getUniqueId());
        if (playerData == null) return;

        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Bed bed)) return;

        Block otherHalf;

        if (bed.getPart() == Bed.Part.HEAD) {
            otherHalf = event.getBlock().getRelative(bed.getFacing().getOppositeFace());
        } else {
            otherHalf = event.getBlock().getRelative(bed.getFacing());
        }

        // Protect own bed(s)
        for (Location location : this.game.getTeams().get(playerData.getTeam()).getData().bedLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList()) {

            if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                event.getPlayer().sendMessage("§cYou cannot break your own bed");
                event.setCancelled(true);
                return;
            }

        }

        // Send bed destroyed message
        for (BedwarsTeam team : this.game.getTeams()) {

            for (Location location : team.getData().bedLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList()) {

                if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                    playerData.setBedsBroken(playerData.getBedsBroken() + 1);
                    playerData.setRewardPoints(playerData.getRewardPoints() + this.game.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("bedDestroyed", 0));

                    for (Player player : this.game.getPlugin().getServer().getOnlinePlayers()) {

                        PlayerData pData = this.game.getPlayerData(player.getUniqueId());

                        BedwarsTeam destroyerTeam = this.game.getTeams().get(playerData.getTeam());

                        if (pData != null && pData.getTeam() == team.getId()) {
                            player.sendMessage("§7Your Bed was destroyed by " + destroyerTeam.getData().chatColor() + event.getPlayer().getName() + "§7!");
                            player.sendTitle("§cBED DESTROYED", "§7You will no longer respawn!", 5, 3*20, 5);
                            player.playSound(player, Sound.ENTITY_WITHER_DEATH, 1, 1);
                        } else {
                            player.sendMessage("§7The Bed of " + team.getData().chatColor() + "Team " + team.getData().chatColor().name() + " §7was destroyed by " + destroyerTeam.getData().chatColor() + event.getPlayer().getName() + "§7!");
                            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                        }

                    }

                }

            }

        }

    }

    @EventHandler
    public void onBlockPlaceForSpawnProtection(@NotNull BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getData().spawnBlockPlaceProtection() <= 0 && this.game.getData().villagerBlockPlaceProtection() <= 0) return;
        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        for (BedwarsTeam team : this.game.getTeams()) {

            if (this.game.getData().spawnBlockPlaceProtection() > 0) {

                for (Location location : team.getData().spawnpoints()) {

                    if (Bedwars.getBlockDistance(WorldUtils.locationWithWorld(location, this.game.getWorld()), event.getBlock().getLocation()) <= this.game.getData().spawnBlockPlaceProtection()) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks here");
                        return;
                    }

                }

            }

            if (this.game.getData().villagerBlockPlaceProtection() > 0) {

                List<Location> villagerLocations = new ArrayList<>();
                villagerLocations.addAll(team.getData().shopVillagerLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList());
                villagerLocations.addAll(team.getData().upgradeVillagerLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList());

                for (Location location : villagerLocations) {

                    if (Bedwars.getBlockDistance(location, event.getBlock().getLocation()) <= this.game.getData().villagerBlockPlaceProtection()) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks here");
                        return;
                    }

                }

            }

        }
    }

    @EventHandler
    public void onPlayerInteractForEnderChest(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENDER_CHEST) return;

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        event.setCancelled(true);
        event.getPlayer().openInventory(playerData.getEnderchest());
    }

    @EventHandler
    public void onPlayerInteractForBlockingSleep(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer().isSneaking()) return;
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getBlockData() instanceof Bed)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        if (!this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        for (Block block : List.copyOf(event.blockList())) {

            // Prevent non-player-placed blocks from getting destroyed
            if (!this.game.getBlockProtectionSystem().canBreak(block.getLocation())) {
                event.blockList().remove(block);
                continue;
            }

            // Prevent beds from being destroyed from explosions
            if (block.getBlockData() instanceof Bed) {
                event.blockList().remove(block);
                continue;
            }

            // Prevent glass from getting destroyed by explosions
            if (block.getType().toString().endsWith("GLASS")) {
                event.blockList().remove(block);
                continue;
            }

            // Prevent endstone from getting destroyed by TNT
            if (block.getType() == Material.END_STONE && !(event.getEntity() instanceof TNTPrimed)) {
                event.blockList().remove(block);
                continue;
            }

        }

    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerData playerData = this.game.getPlayerData(player.getUniqueId());
        if (playerData == null) return;

        if (event instanceof EntityDamageByEntityEvent byEntityEvent) {

            Entity damager = PlayerUtils.getRealDamager(byEntityEvent.getDamager());

            if (damager instanceof Player damagerPlayer) {

                // Prevent not ingame players from damaging ingame players
                PlayerData damagerData = this.game.getPlayerData(damagerPlayer);
                if (damagerData == null) {
                    event.setCancelled(true);
                    return;
                }

                // Prevent players from hitting their teammates
                if (damagerData.getTeam() == playerData.getTeam()) {
                    event.setCancelled(true);
                    return;
                }

            }

        }

    }

    /**
     * Prevents entities from targeting other players naturally
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity().getWorld() != this.game.getWorld()) return;
        if (event.getTarget() instanceof Player player && this.game.getPlugin().isPlayerBypassing(player)) return;
        if (event.getEntity().getPersistentDataContainer().getOrDefault(NamespacedKeys.ENTITY_TARGETING_ENABLED, PersistentDataType.BOOLEAN, false)) return;

        event.setCancelled(true);
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
