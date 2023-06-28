package net.jandie1505.bedwars;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.BaseDefender;
import net.jandie1505.bedwars.game.entities.BridgeEgg;
import net.jandie1505.bedwars.game.generators.Generator;
import net.jandie1505.bedwars.game.menu.shop.ShopEntry;
import net.jandie1505.bedwars.game.menu.shop.ShopMenu;
import net.jandie1505.bedwars.game.menu.shop.UpgradeEntry;
import net.jandie1505.bedwars.game.menu.upgrades.UpgradesMenu;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.team.traps.*;
import net.jandie1505.bedwars.lobby.Lobby;
import net.jandie1505.bedwars.lobby.LobbyPlayerData;
import net.jandie1505.bedwars.lobby.MapData;
import net.jandie1505.bedwars.lobby.inventory.VotingMenu;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class EventListener implements Listener {
    private final Bedwars plugin;

    public EventListener(Bedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (Boolean.FALSE.equals(event.getEntity().getPlayer().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                event.getEntity().spigot().respawn();
            }, 1);
        }

        // Copy and clear item drops

        List<ItemStack> items = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        // Ingame checks

        if (!(this.plugin.getGame() instanceof Game)) {
            return;
        }

        if (!((Game) this.plugin.getGame()).getPlayers().containsKey(event.getEntity().getUniqueId())) {
            return;
        }

        // Death Message

        event.setDeathMessage(this.getDeathMessage(event));

        // Increase deaths count

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getUniqueId());
        playerData.setDeaths(playerData.getDeaths() + 1);

        // Cleanup saved item drops

        for (ItemStack item : List.copyOf(items)) {

            boolean canItemDrop = false;
            for (Generator generator : ((Game) this.plugin.getGame()).getGenerators()) {

                if (generator.getItem().isSimilar(item)) {
                    canItemDrop = true;
                    break;
                }

            }

            if (!canItemDrop) {
                items.remove(item);
            }

        }

        // Give items + increase kill count or drop items

        if (event.getEntity().getKiller() != null && ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getKiller().getUniqueId()) != null) {

            PlayerData killerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getKiller().getUniqueId());
            killerData.setKills(killerData.getKills() + 1);

            for (ItemStack item : items) {
                event.getEntity().getKiller().getInventory().addItem(item);
                event.getEntity().getKiller().sendMessage("§7+ " + item.getAmount() + " " + item.getType());
            }

        } else {

            for (ItemStack item : items) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
            }

        }

        // Remove player if team has no bed

        BedwarsTeam team = ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam());

        if (team == null || team.hasBed() <= 0) {
            ((Game) this.plugin.getGame()).removePlayer(event.getEntity().getUniqueId());
            return;
        }

        // Make player respawn

        playerData.setAlive(false);

        // Decrease upgrades

        if (playerData.getPickaxeUpgrade() > 1) {
            playerData.setPickaxeUpgrade(playerData.getPickaxeUpgrade() - 1);
        }

        if (playerData.getShearsUpgrade() > 1) {
            playerData.setShearsUpgrade(playerData.getShearsUpgrade() - 1);
        }

    }

    private String getDeathMessage(PlayerDeathEvent event) {

        if (!(this.plugin.getGame() instanceof Game)) {
            return "";
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getUniqueId());

        if (playerData == null) {
            return "";
        }

        BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

        if (team == null) {
            return "";
        }

        String deathMessage = team.getChatColor() + event.getEntity().getDisplayName() + "§7 ";

        if (event.getEntity().getLastDamageCause() == null) {
            return deathMessage + "died";
        }

        if (event.getEntity().getKiller() != null) {

            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player) {
                    deathMessage = deathMessage + "lost in close combat against";
                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Projectile) {

                    if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Arrow || ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof SpectralArrow) {
                        deathMessage = deathMessage + "was defeated in bow fight by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Fireball) {
                        deathMessage = deathMessage + "was blown into a thousand pieces by a fireball, thrown by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof ThrownPotion) {
                        deathMessage = deathMessage + "was killed by magic by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Trident) {
                        deathMessage = deathMessage + "was impaled on a trident by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Snowball) {
                        deathMessage = deathMessage + "lost the snowball fight against";
                    } else {
                        deathMessage = deathMessage + "was killed by";
                    }

                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof TNTPrimed) {
                    deathMessage = deathMessage + "was blown away by";
                } else {
                    deathMessage = deathMessage + "died in combat with";
                }

            } else {

                switch (event.getEntity().getLastDamageCause().getCause()) {
                    case DROWNING:
                        deathMessage = deathMessage + "would have been better not to hide in the water from";
                        break;
                    case FALL:
                        deathMessage = deathMessage + "was knocked off a hill by";
                        break;
                    case FREEZE:
                        deathMessage = deathMessage + "wanted a cool down in fight with";
                        break;
                    case LAVA:
                        deathMessage = deathMessage + "took a hot bath, gifted by";
                        break;
                    case VOID:
                        deathMessage = deathMessage + "was knocked into the void by";
                        break;
                    case SUFFOCATION:
                        deathMessage = deathMessage + "was strangled by";
                        break;
                    case FIRE_TICK:
                        deathMessage = deathMessage + "burned to death while in combat with";
                        break;
                    case FIRE:
                        deathMessage = deathMessage + "was burned to death by";
                        break;
                    case ENTITY_EXPLOSION:
                        deathMessage = deathMessage + "exploded in combat with";
                        break;
                    default:
                        deathMessage = deathMessage + "died in combat with";
                        break;
                }

            }

            PlayerData killerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getKiller().getUniqueId());

            if (killerData == null) {
                return deathMessage;
            }

            BedwarsTeam killerTeam = ((Game) this.plugin.getGame()).getTeam(killerData.getTeam());

            if (killerTeam == null) {
                return deathMessage;
            }

            deathMessage = deathMessage + " " + killerTeam.getChatColor() + event.getEntity().getKiller().getDisplayName();

            return deathMessage;
        } else {

            switch (event.getEntity().getLastDamageCause().getCause()) {
                case DROWNING:
                    deathMessage = deathMessage + "could not surface in time before running out of air";
                case ENTITY_EXPLOSION:
                    deathMessage = deathMessage + "became a suicide bomber";
                    break;
                case FIRE_TICK:
                    deathMessage = deathMessage + "has not found any water to extinguish";
                    break;
                case SUFFOCATION:
                    deathMessage = deathMessage + "had to make the experience that you need air to breathe";
                    break;
                case MAGIC:
                    deathMessage = deathMessage + "has felt the painful side of magic";
                    break;
                case LAVA:
                    deathMessage = deathMessage + "wanted to take a hot bath";
                    break;
                case FIRE:
                    deathMessage = deathMessage + "has learned that fire is hot";
                    break;
                case FALL:
                    deathMessage = deathMessage + "should have looked down better before jumping";
                    break;
                case VOID:
                    deathMessage = deathMessage + "has found a hole";
                    break;
                default:
                    deathMessage = deathMessage + "died";
                    break;
            }

            return deathMessage;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {

            Location location = event.getPlayer().getLocation();

            if (location.getY() < -64) {
                location.setY(-64);
            }

            event.setRespawnLocation(location);

        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            return;
        }

        if (this.plugin.getGame() != null && this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (!((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            // BLOCK PLACE PROTECTION

            if (((Game) this.plugin.getGame()).getSpawnBlockPlaceProtection() > 0 || ((Game) this.plugin.getGame()).getVillagerBlockPlaceProtection() > 0) {

                for (BedwarsTeam team : ((Game) this.plugin.getGame()).getTeams()) {

                    if (((Game) this.plugin.getGame()).getSpawnBlockPlaceProtection() > 0) {

                        for (Location location : team.getSpawnpoints()) {

                            if (Bedwars.getBlockDistance(location, event.getBlock().getLocation()) <= ((Game) this.plugin.getGame()).getSpawnBlockPlaceProtection()) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("§cYou cannot place blocks here");
                                return;
                            }

                        }

                    }

                    if (((Game) this.plugin.getGame()).getVillagerBlockPlaceProtection() > 0) {

                        List<Location> villagerLocations = new ArrayList<>();
                        villagerLocations.addAll(team.getShopVillagerLocations());
                        villagerLocations.addAll(team.getUpgradesVillagerLocations());

                        for (Location location : villagerLocations) {

                            if (Bedwars.getBlockDistance(location, event.getBlock().getLocation()) <= ((Game) this.plugin.getGame()).getVillagerBlockPlaceProtection()) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("§cYou cannot place blocks here");
                                return;
                            }

                        }

                    }

                }

            }

            // TNT

            if (event.getBlockPlaced().getType() == Material.TNT) {
                event.setCancelled(true);

                if (event.getItemInHand().getType() == Material.TNT && event.getItemInHand().getAmount() > 0) {
                    event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
                    event.getPlayer().sendMessage("§bTNT activated");

                    Location location = event.getBlockPlaced().getLocation().clone();
                    location.add(0.5, 0, 0.5);

                    TNTPrimed tnt = (TNTPrimed) event.getBlockPlaced().getLocation().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    tnt.setSource(event.getPlayer());
                    tnt.setFuseTicks(80);
                }

                return;
            }

            // IRON GOLEM

            if (((Game) this.plugin.getGame()).getItemShop().getIronGolemSpawnEgg() != null) {

                ItemStack golemItem = this.plugin.getItemStorage().getItem(((Game) this.plugin.getGame()).getItemShop().getIronGolemSpawnEgg());

                if (golemItem != null) {

                    if (event.getBlockPlaced().getType() == golemItem.getType() && event.getItemInHand().isSimilar(golemItem)) {
                        event.setCancelled(true);

                        if (event.getItemInHand().getAmount() > 0) {
                            event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);

                            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

                            if (playerData == null) {
                                return;
                            }

                            BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());

                            if (team == null) {
                                return;
                            }

                            if (event.getBlockPlaced().getWorld() != ((Game) this.plugin.getGame()).getWorld()) {
                                return;
                            }

                            Location location = event.getBlockPlaced().getLocation().clone();
                            location.add(0.5, 0, 0.5);

                            IronGolem ironGolem = (IronGolem) event.getBlockPlaced().getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
                            ((Game) this.plugin.getGame()).addBaseDefender(new BaseDefender((Game) this.plugin.getGame(), ironGolem, team.getId()));

                        }

                        return;
                    }

                }

            }

            ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().add(event.getBlockPlaced().getLocation());

        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (this.plugin.getGame() instanceof Game) {
                ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().remove(event.getBlock().getLocation());
            }

            return;
        }

        if (this.plugin.getGame() != null && this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (!((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            if (((Game) this.plugin.getGame()).getPlayerPlacedBlocks().contains(event.getBlock().getLocation()) || event.getBlock().getBlockData() instanceof Bed) {

                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

                if (event.getBlock().getBlockData() instanceof Bed) {

                    Block otherHalf;

                    if (((Bed) event.getBlock().getBlockData()).getPart() == Bed.Part.HEAD) {
                        otherHalf = event.getBlock().getRelative(((Bed) event.getBlock().getBlockData()).getFacing().getOppositeFace());
                    } else {
                        otherHalf = event.getBlock().getRelative(((Bed) event.getBlock().getBlockData()).getFacing());
                    }

                    for (Location location : ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam()).getBedLocations()) {

                        if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                            event.getPlayer().sendMessage("§cYou cannot break your own bed");
                            event.setCancelled(true);
                            return;
                        }

                    }

                    for (BedwarsTeam team : ((Game) this.plugin.getGame()).getTeams()) {

                        for (Location location : team.getBedLocations()) {

                            if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                                for (Player player : this.plugin.getServer().getOnlinePlayers()) {

                                    PlayerData pData = ((Game) this.plugin.getGame()).getPlayers().get(player.getUniqueId());

                                    BedwarsTeam destroyerTeam = ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam());

                                    if (pData != null && pData.getTeam() == team.getId()) {
                                        player.sendMessage("§7Your Bed was destroyed by " + destroyerTeam.getChatColor() + event.getPlayer().getName() + "§7!");
                                        player.sendTitle("§cBED DESTROYED", "§7You will no longer respawn!", 5, 3*20, 5);
                                        player.playSound(player, Sound.ENTITY_WITHER_DEATH, 1, 1);
                                    } else {
                                        player.sendMessage("§7The Bed of " + team.getChatColor() + "Team " + team.getChatColor().name() + " §7was destroyed by " + destroyerTeam.getChatColor() + event.getPlayer().getName() + "§7!");
                                        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                    }

                                }

                            }

                        }

                    }

                }

                ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().remove(event.getBlock().getLocation());

            } else {

                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou only can break blocks placed by a player");

            }

        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            // Prevent armor modification

            if (!this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

                if (this.plugin.getItemStorage().isArmorItem(event.getItem())) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking() && event.getClickedBlock().getBlockData() instanceof Bed) {
                    event.setCancelled(true);
                    return;
                }

            }

            // Get information

            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getItem());

            if (itemId < 0) {
                return;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

            if (playerData == null) {
                return;
            }

            // Fireball

            if (((Game) this.plugin.getGame()).getItemShop().getFireballItem() != null && itemId == ((Game) this.plugin.getGame()).getItemShop().getFireballItem()) {
                event.setCancelled(true);

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

                return;
            }

            // enhanced fireball

            if (((Game) this.plugin.getGame()).getItemShop().getEnhancedFireballItem() != null && itemId == ((Game) this.plugin.getGame()).getItemShop().getEnhancedFireballItem()) {
                event.setCancelled(true);

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

                return;
            }

            // Safety platform

            if (((Game) this.plugin.getGame()).getItemShop().getSafetyPlatform() != null && itemId == ((Game) this.plugin.getGame()).getItemShop().getSafetyPlatform()) {
                event.setCancelled(true);

                ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());

                if (itemStack != null && itemStack.getAmount() > 0) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }

                BedwarsTeam team = ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam());

                if (team == null || team.getChatColor() == null) {
                    return;
                }

                Material material = Material.getMaterial(Bedwars.getBlockColorString(team.getChatColor()) + "_STAINED_GLASS");

                if (material == null) {
                    return;
                }

                this.spawnSafetyPlatform(this.plugin, event.getPlayer(), material);

                return;
            }

            // Player tracker

            if (((Game) this.plugin.getGame()).getItemShop().getPlayerTracker() != null && itemId == ((Game) this.plugin.getGame()).getItemShop().getPlayerTracker()) {
                event.setCancelled(true);

                List<UUID> randomPlayerList = new ArrayList<>(((Game) this.plugin.getGame()).getPlayers().keySet());
                Collections.shuffle(randomPlayerList);

                for (UUID trackingPlayerId : randomPlayerList) {

                    if (playerData.getTrackingTarget() != null && trackingPlayerId.equals(playerData.getTrackingTarget())) {
                        continue;
                    }

                    Player trackingPlayer = this.plugin.getServer().getPlayer(trackingPlayerId);

                    if (trackingPlayer == null) {
                        continue;
                    }

                    PlayerData trackingPlayerData = ((Game) this.plugin.getGame()).getPlayers().get(trackingPlayerId);

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

                return;
            }
        } else if (this.plugin.getGame() instanceof Lobby) {

            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getItem());

            if (itemId >= 0 && itemId == ((Lobby) this.plugin.getGame()).getMapVoteButtonItemId()) {
                event.setCancelled(true);

                if (!((Lobby) this.plugin.getGame()).isMapVoting()) {
                    event.getPlayer().sendMessage("§cMap voting is currently disabled");
                    event.getPlayer().closeInventory();
                    return;
                }

                if (((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                    event.getPlayer().sendMessage("§cMap voting is already over");
                    event.getPlayer().closeInventory();
                    return;
                }

                event.getPlayer().openInventory(new VotingMenu((Lobby) this.plugin.getGame(), event.getPlayer().getUniqueId()).getVotingMenu());
                return;
            }

            if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);

        }

    }

    private void spawnSafetyPlatform(Bedwars plugin, Player player, Material material) {

        if (!(plugin.getGame() instanceof Game)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 3*20, 0, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3*20, 1, true, true));
        player.sendMessage("§bSafety Platform deployed");

        Location center = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        center.add(0, -2, 0);

        for (int x = center.getBlockX() - 1; x <= center.getBlockX() + 1; x++) {
            for (int z = center.getBlockZ() - 1; z <= center.getBlockZ() + 1; z++) {
                Block block = player.getWorld().getBlockAt(new Location(player.getWorld(), x, center.getBlockY(), z));

                if (block.getType() == Material.AIR) {
                    block.setType(material);
                    ((Game) plugin.getGame()).getPlayerPlacedBlocks().add(block.getLocation());
                }

            }
        }

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);

            for (String tag : List.copyOf(event.getRightClicked().getScoreboardTags())) {

                if (tag.startsWith("shop")) {
                    event.getPlayer().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getPlayer().getUniqueId()).getPage(0));
                    return;
                }

                if (tag.startsWith("upgrades")) {
                    event.getPlayer().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getPlayer().getUniqueId()).getUpgradesMenu());
                    return;
                }

            }

            return;
        }

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (event.getInventory() == null) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.WORKBENCH) {

            if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        if (event.getInventory().getHolder() == event.getWhoClicked()) {

            if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
                return;
            }

            // Block armor slots
            if (event.getSlot() == 36 || event.getSlot() == 37 || event.getSlot() == 38 || event.getSlot() == 39) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou can upgrade your armor in item shop");
                return;
            }

            if (event.isShiftClick() && event.getCurrentItem() != null && this.plugin.getItemStorage().isArmorItem(event.getCurrentItem())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou can upgrade your armor in item shop");
                return;
            }

            // Block crafting

            if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                event.setCancelled(true);
                return;
            }

            // Ingame

            if (!(this.plugin.getGame() instanceof Game)) {
                event.setCancelled(true);
                return;
            }

            if (event.getClick() == ClickType.DROP) {

                if (((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon() != null && this.plugin.getItemStorage().getItemId(event.getCurrentItem()) == ((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon()) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cYou cannot drop the default weapon");
                    return;
                }

                for (UpgradeEntry upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
                    for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                        if (this.plugin.getItemStorage().getItemId(event.getCurrentItem()) == itemId) {
                            event.setCancelled(true);
                            event.getWhoClicked().sendMessage("§cYou cannot drop upgradable items");
                            return;
                        }

                    }
                }

            }

            return;
        }

        if (event.getInventory().getHolder() instanceof ShopMenu) {

            event.setCancelled(true);

            if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
                return;
            }

            if (!(this.plugin.getGame() instanceof Game) || !((Game) this.plugin.getGame()).getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
                return;
            }

            if (event.getCurrentItem() == null) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getCurrentItem());

            if (itemId < 0) {
                return;
            }

            Integer[] menuItems = ((Game) this.plugin.getGame()).getItemShop().getMenuItems();

            for (int i = 0; i < menuItems.length; i++) {

                if (menuItems[i] != null && menuItems[i] == itemId) {
                    event.getWhoClicked().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getPage(i));
                    return;
                }

            }

            ShopEntry shopEntry = ((Game) this.plugin.getGame()).getItemShop().getShopEntry(itemId);

            if (shopEntry != null) {

                if (!this.purchaseItem(event.getWhoClicked().getInventory(), shopEntry.getPrice(), shopEntry.getCurrency())) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + shopEntry.getCurrency().name() + "s!");
                    return;
                }

                event.getWhoClicked().sendMessage("§aItem successfully purchased");
                event.getWhoClicked().getInventory().addItem(((Game) this.plugin.getGame()).getPlugin().getItemStorage().getItem(itemId));
                event.getWhoClicked().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getPage(ShopMenu.getMenuPage(event.getInventory())));

                return;
            }

            UpgradeEntry upgradeEntry = ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntry(itemId);

            if (upgradeEntry != null) {

                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getWhoClicked().getUniqueId());

                if (playerData == null) {
                    return;
                }

                int upgradeLevel = upgradeEntry.getUpgradeLevel(playerData) + 1;

                if (upgradeLevel < 0) {
                    return;
                }

                int price = upgradeEntry.getUpgradePrice(upgradeLevel);

                if (price < 0) {
                    return;
                }

                Material currency = upgradeEntry.getUpgradeCurrency(upgradeLevel);

                if (currency == null) {
                    return;
                }

                if (!this.purchaseItem(event.getWhoClicked().getInventory(), price, currency)) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + currency.name() + "s!");
                    return;
                }

                event.getWhoClicked().sendMessage("§aItem successfully purchased");
                upgradeEntry.upgradePlayer(playerData);
                event.getWhoClicked().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getPage(ShopMenu.getMenuPage(event.getInventory())));

                return;
            }

            return;
        }

        if (event.getInventory().getHolder() instanceof UpgradesMenu) {

            event.setCancelled(true);

            if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
                return;
            }

            if (!(this.plugin.getGame() instanceof Game) || !((Game) this.plugin.getGame()).getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
                return;
            }

            if (event.getCurrentItem() == null) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getCurrentItem());

            if (itemId < 0) {
                return;
            }

            TeamUpgrade teamUpgrade;

            if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getSharpnessUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getSharpnessUpgrade();
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getProtectionUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getProtectionUpgrade();
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getHasteUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getHasteUpgrade();
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getForgeUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getForgeUpgrade();
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getHealPoolUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getHealPoolUpgrade();
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getDragonBuffUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getDragonBuffUpgrade();
            } else {
                teamUpgrade = null;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getWhoClicked().getUniqueId());

            if (playerData == null) {
                return;
            }

            BedwarsTeam team = ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam());

            if (team == null) {
                return;
            }

            if (teamUpgrade != null) {

                if (team.getTeamUpgrade(teamUpgrade) >= teamUpgrade.getUpgradePrices().size()) {
                    return;
                }

                Integer price = teamUpgrade.getUpgradePrices().get(team.getTeamUpgrade(teamUpgrade));

                if (price == null || price < 0) {
                    return;
                }

                if (team.getTeamUpgrade(teamUpgrade) >= teamUpgrade.getUpgradePriceCurrencies().size()) {
                    return;
                }

                Material currency = teamUpgrade.getUpgradePriceCurrencies().get(team.getTeamUpgrade(teamUpgrade));

                if (currency == null) {
                    return;
                }

                if (!this.purchaseItem(event.getWhoClicked().getInventory(), price, currency)) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + currency.name() + "S!");
                    return;
                }

                event.getWhoClicked().sendMessage("§aUpgrade successfully purchased");
                team.setTeamUpgrade(teamUpgrade, team.getTeamUpgrade(teamUpgrade) + 1);
                event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());

                return;
            }

            if (event.getSlot() < 27) {

                BedwarsTrap bedwarsTrap;

                if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getAlarmTrap()) {
                    bedwarsTrap = new AlarmTrap(team);
                } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getItsATrap()) {
                    bedwarsTrap = new ItsATrap(team);
                } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getMiningFatigueTrap()) {
                    bedwarsTrap = new MiningFatigueTrap(team);
                } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getCountermeasuresTrap()) {
                    bedwarsTrap = new CountermeasuresTrap(team);
                } else {
                    bedwarsTrap = null;
                }

                if (bedwarsTrap != null) {

                    boolean secondary = event.getClick() == ClickType.RIGHT;

                    int price = UpgradesMenu.getTrapPrice(secondary, team);

                    if (price < 0) {
                        return;
                    }

                    if (!this.purchaseItem(event.getWhoClicked().getInventory(), price, Material.DIAMOND)) {
                        event.getWhoClicked().sendMessage("§cYou don't have enough " + Material.DIAMOND + "S!");
                        return;
                    }

                    BedwarsTrap[] trapArray;

                    if (secondary) {
                        trapArray = team.getSecondaryTraps();
                    } else {
                        trapArray = team.getPrimaryTraps();
                    }

                    event.getWhoClicked().sendMessage("§aTrap successfully purchased");
                    BedwarsTeam.addTrap(trapArray, bedwarsTrap);
                    event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());

                }

            } else {

                if (event.getSlot() == 38) {
                    team.getPrimaryTraps()[0] = null;
                    event.getWhoClicked().sendMessage("§aTrap successfully removed");
                    event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());
                } else if (event.getSlot() == 39) {
                    team.getPrimaryTraps()[1] = null;
                    event.getWhoClicked().sendMessage("§aTrap successfully removed");
                    event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());
                } else if (event.getSlot() == 41) {
                    team.getSecondaryTraps()[0] = null;
                    event.getWhoClicked().sendMessage("§aTrap successfully removed");
                    event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());
                } else if (event.getSlot() == 42) {
                    team.getSecondaryTraps()[1] = null;
                    event.getWhoClicked().sendMessage("§aTrap successfully removed");
                    event.getWhoClicked().openInventory(new UpgradesMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getUpgradesMenu());
                }

            }

            return;
        }

        if (event.getInventory().getHolder() instanceof VotingMenu) {
            event.setCancelled(true);

            if (!(this.plugin.getGame() instanceof Lobby)) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getCurrentItem());

            if (itemId < 0) {
                return;
            }

            if (itemId != ((Lobby) this.plugin.getGame()).getMapButtonItemId()) {
                return;
            }

            if (!((Lobby) this.plugin.getGame()).isMapVoting()) {
                event.getWhoClicked().sendMessage("§cMap voting is currently disabled");
                event.getWhoClicked().closeInventory();
                return;
            }

            if (((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                event.getWhoClicked().sendMessage("§cMap voting is already over");
                event.getWhoClicked().closeInventory();
                return;
            }

            List<String> lore = event.getCurrentItem().getItemMeta().getLore();

            if (lore.size() < 2) {
                return;
            }

            LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayers().get(event.getWhoClicked().getUniqueId());

            for (MapData map : ((Lobby) this.plugin.getGame()).getMaps()) {

                if (map.getWorld().equals(lore.get(1))) {

                    event.getWhoClicked().closeInventory();

                    if (playerData.getVote() == map) {

                        playerData.setVote(null);
                        event.getWhoClicked().sendMessage("§aYou removed your vote");

                    } else {

                        playerData.setVote(map);
                        event.getWhoClicked().sendMessage("§aYou changed your vote to " + map.getName());

                    }

                    return;
                }

            }

            return;
        }

        if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon() != null && this.plugin.getItemStorage().getItemId(event.getCurrentItem()) == ((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon()) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot move the default weapon to other inventories");
                return;
            }

            for (UpgradeEntry upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
                for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                    if (this.plugin.getItemStorage().getItemId(event.getCurrentItem()) == itemId) {
                        event.setCancelled(true);
                        event.getWhoClicked().sendMessage("§cYou cannot move upgradable items to other inventories");
                        return;
                    }

                }
            }

        }

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

    }

    private boolean purchaseItem(Inventory inventory, int price, Material currency) {

        if (inventory == null) {
            return false;
        }

        int availableCurrency = 0;

        for (ItemStack item : Arrays.copyOf(inventory.getContents(), inventory.getContents().length)) {

            if (item != null && item.getType() == currency) {
                availableCurrency += item.getAmount();
            }

        }

        if (availableCurrency < price) {
            return false;
        }

        Bedwars.removeSpecificAmountOfItems(inventory, currency, price);
        return true;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (event.getInventory().getType() == InventoryType.WORKBENCH) {

            if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        if (event.getInventory().getHolder() == event.getWhoClicked()) {

            if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
                return;
            }

            // Block if not ingame
            if (!(this.plugin.getGame() instanceof Game)) {
                event.setCancelled(true);
                return;
            }

            // Block armor slots
            if (event.getInventorySlots().contains(36) || event.getInventorySlots().contains(37) || event.getInventorySlots().contains(38) || event.getInventorySlots().contains(39)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou can upgrade your armor in item shop");
                return;
            }

            // Block Crafting
            if (event.getRawSlots().contains(0) || event.getRawSlots().contains(1) || event.getRawSlots().contains(2) || event.getRawSlots().contains(3) || event.getRawSlots().contains(4)) {
                event.setCancelled(true);
                return;
            }

            return;
        }

        if (event.getInventory().getHolder() instanceof ShopMenu) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon() != null && this.plugin.getItemStorage().getItemId(event.getOldCursor()) == ((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon()) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot move the default weapon to other inventories");
                return;
            }

            for (UpgradeEntry upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
                for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                    if (this.plugin.getItemStorage().getItemId(event.getOldCursor()) == itemId) {
                        event.setCancelled(true);
                        event.getWhoClicked().sendMessage("§cYou cannot move upgradable items to other inventories");
                        return;
                    }

                }
            }

        }

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {

            if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        if (((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon() != null && this.plugin.getItemStorage().getItemId(event.getItemDrop().getItemStack()) == ((Game) this.plugin.getGame()).getItemShop().getDefaultWeapon()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop the default weapon");
            return;
        }

        for (UpgradeEntry upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
            for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                if (this.plugin.getItemStorage().getItemId(event.getItemDrop().getItemStack()) == itemId && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot drop upgradable items");
                    return;
                }

            }
        }

    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {

            if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

        for (Block block : List.copyOf(event.blockList())) {

            if (!((Game) this.plugin.getGame()).getPlayerPlacedBlocks().contains(block.getLocation())) {
                event.blockList().remove(block);
                continue;
            }

            if (block.getBlockData() instanceof Bed) {
                event.blockList().remove(block);
                continue;
            }

            if (block.getType().toString().endsWith("GLASS")) {
                event.blockList().remove(block);
                continue;
            }

            if (block.getType() == Material.END_STONE) {
                event.blockList().remove(block);
                continue;
            }

        }

    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

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

            Material material = Material.getMaterial(Bedwars.getBlockColorString(team.getChatColor()) + "_WOOL");

            if (material == null) {
                return;
            }

            Vector tpVector = event.getEntity().getVelocity().clone();
            tpVector.divide(new Vector(tpVector.length(), tpVector.length(), tpVector.length()));

            Location location = event.getEntity().getLocation();
            location.add(tpVector);
            location.add(0, -2, 0);

            event.getEntity().teleport(location);

            Vector vector = event.getEntity().getVelocity();

            vector.setX(vector.getX() / 2.0);
            vector.setY(vector.getY() / 2.0);
            vector.setZ(vector.getZ() / 2.0);

            event.getEntity().setVelocity(vector);

            ((Game) this.plugin.getGame()).addBridgeEgg(new BridgeEgg((Game) this.plugin.getGame(), (Egg) event.getEntity(), material));

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
            playerData.setTrapCooldown(30*20);
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

                if (!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)) {
                    return;
                }

                if (this.plugin.isPlayerBypassing(((EntityDamageByEntityEvent) event).getDamager().getUniqueId())) {
                    return;
                }

                PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayers().get(((EntityDamageByEntityEvent) event).getDamager().getUniqueId());

                if (damagerData == null) {
                    event.setCancelled(true);
                    return;
                }

                if (playerData.getTeam() == damagerData.getTeam()) {
                    event.setCancelled(true);
                    return;
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

                    event.setFormat("§7[§6GLOBAL§7] " + team.getChatColor() + "%1$s§7: §7%2$s");
                } else {
                    event.setFormat("§7[" + team.getChatColor() + team.getName() + "§7] " + team.getChatColor() + "%1$s§7: §7%2$s");

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

            event.setJoinMessage(team.getChatColor() + event.getPlayer().getDisplayName() + " §7reconnected");

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

            event.setQuitMessage(team.getChatColor() + event.getPlayer().getDisplayName() + " §7disconnected");

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

}
