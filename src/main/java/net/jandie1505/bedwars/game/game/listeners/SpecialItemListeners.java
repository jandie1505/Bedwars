package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.game.entities.entities.BridgeEgg;
import net.jandie1505.bedwars.game.game.entities.entities.SnowDefender;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpecialItemListeners implements ManagedListener {
    @NotNull private final Game game;

    public SpecialItemListeners(@NotNull Game game) {
        this.game = game;
    }

    // ----- BRIDGE EGG -----

    @EventHandler
    public void onProjectileLaunchForBridgeEgg(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!isSpecialItem(egg.getItem(), CustomItemValues.AUTO_BRIDGE)) return;
        if (!(egg.getShooter() instanceof Player shooter)) return;

        PlayerData shooterData = this.game.getPlayerData(shooter);
        if (shooterData == null) return;

        BedwarsTeam team = this.game.getTeam(shooterData.getTeam());
        if (team == null) return;

        ItemStack wool = new ItemStack(Material.WHITE_WOOL);
        Game.replaceBlockWithTeamColor(wool, team);

        new BridgeEgg(this.game, egg, wool.getType(), 15);
    }

    // ----- GOLEMS -----

    @EventHandler
    public void onPlayerInteractForBaseDefender(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isSpecialItem(event.getItem(), CustomItemValues.BASE_DEFENDER_SPAWN_EGG)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (playerData.getIronGolemCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getIronGolemCooldown() / 20.0) + " to place an iron golem again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());
        if (team == null) return;

        Location location = event.getInteractionPoint();
        if (location == null) return;
        location = location.clone();

        new BaseDefender(this.game, location, team.getId());

        playerData.setIronGolemCooldown(15*20);
    }

    @EventHandler
    public void onPlayerInteractForSnowDefender(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isSpecialItem(event.getItem(), CustomItemValues.SNOW_DEFENDER_SPAWN_EGG)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());

        if (playerData == null) {
            return;
        }

        if (playerData.getIronGolemCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getIronGolemCooldown() / 20.0) + " to place an snow golem again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());

        if (team == null) {
            return;
        }

        Location location = event.getInteractionPoint();
        if (location == null) return;
        location = location.clone();

        new SnowDefender(this.game, location, team.getId());

        playerData.setIronGolemCooldown(15*20);
    }

    // ----- ZAPPER -----

    @EventHandler
    public void onPlayerInteractForZapper(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!isSpecialItem(event.getItem(), CustomItemValues.ZAPPER)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());

        if(playerData == null) {
            return;
        }

        if(playerData.getZapperCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getZapperCooldown() / 20.0) + " to use the Zapper again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());
        if(team == null) return;

        playerData.setZapperCooldown(50*20);

        List<BedwarsTeam> teams = new ArrayList<>(this.game.getTeams());
        teams.remove(team);

        Random random = new Random();
        BedwarsTeam teamToZapp = teams.get(random.nextInt(teams.size()));

        List<UUID> playersToZapp = new ArrayList<UUID>(teamToZapp.getPlayers());
        UUID playerToZapp = playersToZapp.get(random.nextInt(playersToZapp.size()));
        Location zappLocation = Bukkit.getPlayer(playerToZapp).getLocation().clone();

        event.getPlayer().getWorld().spawnEntity(zappLocation, EntityType.LIGHTNING_BOLT);
    }

    // ----- SPAWN DUST -----

    @EventHandler
    public void onPlayerInteractForSpawnDust(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!isSpecialItem(event.getItem(), CustomItemValues.SPAWN_DUST)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        if(playerData.getTeleportToBaseCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou can not use that again during teleport");
            return;
        }

        playerData.setTeleportToBaseCooldown(3*20 + 1);
        event.getPlayer().sendMessage(("§bTeleporting..."));

        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());

        if (itemStack != null && itemStack.getAmount() > 0) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityForCancellingSpawnDustTeleport(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        if (playerData.getTeleportToBaseCooldown() <= 0) return;

        playerData.setTeleportToBaseCooldown(0);
        this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "spawn_dust", 60, Component.text("Spawn dust teleport cancelled", NamedTextColor.RED));
        player.playSound(player.getLocation().clone(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    }

    // ----- BLACK HOLE -----

    @EventHandler
    public void onPlayerInteractForBlackHole(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!isSpecialItem(event.getItem(), CustomItemValues.BLACK_HOLE)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        if(playerData.getBlackHoleCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou have to wait " + ((double) playerData.getBlackHoleCooldown() / 20.0) + " to use a Black Hole again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        playerData.setBlackHoleCooldown(15*20);

        Location center = event.getInteractionPoint();
        if (center == null) return;
        center = center.clone();

        for(int x = center.getBlockX() - 7; x <= center.getBlockX() + 7; x++) {
            for(int y = center.getBlockY() - 7; y <= center.getBlockY() + 7; y++) {
                for(int z = center.getBlockZ() - 7; z <= center.getBlockZ() + 7; z++) {
                    Block block = center.getWorld().getBlockAt(new Location(center.getWorld(), x, y, z));

                    if(block.getType() != Material.AIR) {
                        if(this.game.getBlockProtectionSystem().canBreak(block.getLocation())) {
                            String name = block.getType().name();
                            if(name.contains("WOOL") || name.contains("GLASS")) {
                                block.setType(Material.AIR);
                                this.game.getBlockProtectionSystem().getPlayerPlacedBlocks().remove(block.getLocation().toVector());
                            }
                        }
                    }
                }
            }
        }
    }

    // ----- TNT -----

    @EventHandler
    public void onBlockPlaceForTNT(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getBlockPlaced().getType() != Material.TNT) return;
        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        event.setCancelled(true);

        if (event.getItemInHand().getType() != Material.TNT) return;
        if (event.getItemInHand().getAmount() <= 0) return;

        event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);

        Location location = event.getBlockPlaced().getLocation().clone();
        location.add(0.5, 0, 0.5);

        TNTPrimed tnt = event.getBlockPlaced().getLocation().getWorld().spawn(location, TNTPrimed.class);
        tnt.setSource(event.getPlayer());
        tnt.setFuseTicks(80);
    }

    // ----- SNOWBALLS -----

    @EventHandler
    public void onProjectileHitForSnowBall(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;

        if (event.getHitEntity() instanceof Player player) {

            event.setCancelled(true);
            player.setVelocity(snowball.getVelocity().clone().multiply(1.5));
            snowball.remove();

        } else if (event.getHitEntity() instanceof IronGolem || event.getHitEntity() instanceof TNTPrimed) {

            event.setCancelled(true);
            event.getHitEntity().setVelocity(snowball.getVelocity().clone().multiply(2));
            snowball.remove();

        } else if (event.getHitEntity() instanceof Fireball fireball) {

            event.setCancelled(true);
            fireball.setDirection(new Vector(0, 0, 0));
            fireball.setVelocity(snowball.getVelocity().clone().multiply(4));
            snowball.remove();

        }
    }

    // ----- PEARL SWAP -----

    @EventHandler
    public void onProjectileHitForEnderPearlSwap(ProjectileHitEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(event.getHitEntity() instanceof LivingEntity target) || !(pearl.getShooter() instanceof Player shooter)) return;

        event.setCancelled(true);
        pearl.remove();

        if (target.getPersistentDataContainer().getOrDefault(NamespacedKeys.ENTITY_PEARL_SWAP_EXCLUDED, PersistentDataType.BOOLEAN, false)) return;

        Location firstLocation = target.getLocation().clone();
        Location secondLocation = shooter.getLocation().clone();

        target.teleport(secondLocation);
        shooter.teleport(firstLocation);
    }

    // ----- UTILITIES -----

    /**
     * Special items are identified by a boolean value in the PersistentDataContainer of an item.<br/>
     * If the item has this value available and set to true, the item is a special item.<br/>
     * This method is to make it easier to check for such a value to avoid duplicate code.
     * @param item item (can be null)
     * @return is special item
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isSpecialItem(@Nullable ItemStack item, String specialItemValue) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        String value = dataContainer.getOrDefault(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, "");
        if (value.isEmpty()) return false;
        return value.equals(specialItemValue);
    }

    // ----- OTHER -----

    public GamePart getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
