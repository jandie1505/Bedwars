package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.game.entities.entities.BridgeEgg;
import net.jandie1505.bedwars.game.game.entities.entities.SnowDefender;
import net.jandie1505.bedwars.game.game.player.PlayerData;
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

    // ----- FIREBALLS -----

    @EventHandler
    public void onPlayerInteractForFireball(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_FIREBALL)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        if (playerData.getFireballCooldown() <= 0) {

            ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());

            if (itemStack != null && itemStack.getAmount() > 0) {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }

            Fireball fireball = event.getPlayer().launchProjectile(Fireball.class);
            fireball.setShooter(event.getPlayer());
            fireball.setDirection(event.getPlayer().getEyeLocation().getDirection());
            fireball.setYield(2);
            fireball.setIsIncendiary(false);
            fireball.setTicksLived(3*20);

            playerData.setFireballCooldown(2*20);

        } else {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getFireballCooldown() / 20.0) + " to use the fireball again");
        }
    }

    @EventHandler
    public void onPlayerInteractForEnhancedFireball(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_ENHANCED_FIREBALL)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        if (playerData.getFireballCooldown() <= 0) {

            ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());

            if (itemStack != null && itemStack.getAmount() > 0) {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }

            Fireball fireball = event.getPlayer().launchProjectile(Fireball.class);
            fireball.setShooter(event.getPlayer());
            fireball.setDirection(event.getPlayer().getEyeLocation().getDirection().multiply(2));
            fireball.setYield(4);
            fireball.setIsIncendiary(false);
            fireball.setTicksLived(10*20);

            playerData.setFireballCooldown(3*20);

        } else {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getFireballCooldown() / 20.0) + " to use the fireball again");
        }
    }

    // ----- SAFETY PLATFORMS -----

    @EventHandler
    public void onPlayerInteractForSafetyPlatform(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        this.createSafetyPlatform(event.getPlayer(), playerData, false);
    }

    @EventHandler
    public void onPlayerInteractForEnhancedSafetyPlatform(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(),NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM_ENHANCED)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        this.createSafetyPlatform(event.getPlayer(), playerData, true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (hasItemSpecialItemKey(event.getMainHandItem(), NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM)) {
            event.setCancelled(true);
            this.createSafetyPlatform(event.getPlayer(), playerData, false);
            return;
        }

        if(hasItemSpecialItemKey(event.getMainHandItem(), NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM_ENHANCED)) {
            event.setCancelled(true);
            this.createSafetyPlatform(event.getPlayer(), playerData, true);
            return;
        }
    }

    private void createSafetyPlatform(Player player, PlayerData playerData, boolean enhanced) {

        ItemStack itemStack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());

        if ((hasItemSpecialItemKey(itemStack, NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM) || hasItemSpecialItemKey(itemStack, NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM_ENHANCED)) && itemStack.getAmount() > 0) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        } else {
            ItemStack offhandItem = player.getInventory().getItemInOffHand();

            if ((hasItemSpecialItemKey(itemStack, NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM) || hasItemSpecialItemKey(itemStack, NamespacedKeys.GAME_ITEM_SAFETY_PLATFORM_ENHANCED)) && itemStack.getAmount() > 0) {
                offhandItem.setAmount(offhandItem.getAmount() - 1);
            }

        }

        BedwarsTeam team = this.game.getTeams().get(playerData.getTeam());

        if (team == null || team.getData().chatColor() == null) {
            return;
        }

        Material material = Material.getMaterial(Bedwars.getBlockColorString(team.getData().chatColor()) + "_STAINED_GLASS");

        if (material == null) {
            return;
        }

        if (enhanced) {
            this.spawnEnhancedSafetyPlatform(player, material);
        } else {
            this.spawnSafetyPlatform(player, material);
        }

    }

    private void spawnSafetyPlatform(Player player, Material material) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 3*20, 0, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3*20, 1, true, true));
        player.sendMessage("§bSafety Platform deployed");

        Location center = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        center.add(0, -1, 0);

        for (int x = center.getBlockX() - 1; x <= center.getBlockX() + 1; x++) {
            for (int z = center.getBlockZ() - 1; z <= center.getBlockZ() + 1; z++) {
                Block block = player.getWorld().getBlockAt(new Location(player.getWorld(), x, center.getBlockY(), z));

                if (block.getType() == Material.AIR) {
                    block.setType(material);
                    this.game.getBlockProtectionSystem().getPlayerPlacedBlocks().add(block.getLocation().toVector());
                }

            }
        }

    }

    private void spawnEnhancedSafetyPlatform(Player player, Material material) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 3*20, 0, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3*20, 1, true, true));
        player.sendMessage("§bSafety Platform deployed");

        Location center = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        center.add(0, -1, 0);

        for (int x = center.getBlockX() - 15; x <= center.getBlockX() + 15; x++) {
            for (int z = center.getBlockZ() - 15; z <= center.getBlockZ() + 15; z++) {
                Block block = player.getWorld().getBlockAt(new Location(player.getWorld(), x, center.getBlockY(), z));

                if (block.getType() == Material.AIR) {
                    block.setType(material);
                    this.game.getBlockProtectionSystem().getPlayerPlacedBlocks().add(block.getLocation().toVector());
                }

            }
        }
    }

    // ----- BRIDGE EGG -----

    @EventHandler
    public void onProjectileLaunchForBridgeEgg(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!hasItemSpecialItemKey(egg.getItem(), NamespacedKeys.GAME_ITEM_BRIDGE_EGG)) return;
        if (!(egg.getShooter() instanceof Player shooter)) return;

        PlayerData shooterData = this.game.getPlayerData(shooter);
        if (shooterData == null) return;

        BedwarsTeam team = this.game.getTeam(shooterData.getTeam());
        if (team == null) return;

        ItemStack wool = new ItemStack(Material.WHITE_WOOL);
        Game.replaceBlockWithTeamColor(wool, team);

        new BridgeEgg(this.game, egg, wool.getType(), 15);
    }

    // ----- PLAYER TRACKER -----

    @EventHandler
    public void onPlayerInteractForPlayerTracker(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_PLAYER_TRACKER)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        List<UUID> randomPlayerList = new ArrayList<>(this.game.getRegisteredPlayers());
        Collections.shuffle(randomPlayerList);

        for (UUID trackingPlayerId : randomPlayerList) {

            if (playerData.getTrackingTarget() != null && trackingPlayerId.equals(playerData.getTrackingTarget())) {
                continue;
            }

            Player trackingPlayer = this.game.getPlugin().getServer().getPlayer(trackingPlayerId);

            if (trackingPlayer == null) {
                continue;
            }

            PlayerData trackingPlayerData = this.game.getPlayerData(trackingPlayer);

            if (trackingPlayerData == null) {
                continue;
            }

            if (trackingPlayerData.getTeam() == playerData.getTeam()) {
                continue;
            }

            playerData.setTrackingTarget(trackingPlayerId);
            event.getPlayer().sendMessage("§bTracking target changed to " + trackingPlayer.getName());
            break;

        }
    }

    // ----- GOLEMS -----

    @EventHandler
    public void onPlayerInteractForBaseDefender(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_BASE_DEFENDER)) return;

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
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_SNOW_DEFENDER)) return;

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
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_ZAPPER)) return;

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
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_SPAWN_DUST)) return;

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
        player.sendActionBar(Component.text("Spawn dust teleport cancelled", NamedTextColor.RED));
        player.playSound(player.getLocation().clone(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    }

    // ----- BLACK HOLE -----

    @EventHandler
    public void onPlayerInteractForBlackHole(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!hasItemSpecialItemKey(event.getItem(), NamespacedKeys.GAME_ITEM_BLACK_HOLE)) return;

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

    // ----- STEALTH MILK -----

    @EventHandler
    public void onItemConsumeForStealthMilk(PlayerItemConsumeEvent event) {
        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (event.getItem().getType() != Material.MILK_BUCKET) return;
        event.setCancelled(true);

        Bedwars.removeSpecificAmountOfItems(event.getPlayer().getInventory(), Material.MILK_BUCKET, 1);
        playerData.setMilkTimer(30*20);
        event.getPlayer().sendMessage("§bMilk activated");
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
     * @param key (not null)
     * @return is special item
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasItemSpecialItemKey(@Nullable ItemStack item, @NotNull NamespacedKey key) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return dataContainer.getOrDefault(key, PersistentDataType.BOOLEAN, false);
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
