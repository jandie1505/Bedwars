package net.jandie1505.bedwars;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.endlobby.Endlobby;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.entities.entities.BridgeEgg;
import net.jandie1505.bedwars.game.entities.entities.SnowDefender;
import net.jandie1505.bedwars.game.menu.shop.old.ShopEntryOld;
import net.jandie1505.bedwars.game.menu.shop.old.ShopMenu;
import net.jandie1505.bedwars.game.menu.shop.old.UpgradeEntryOld;
import net.jandie1505.bedwars.game.menu.upgrades.UpgradesMenu;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.team.traps.*;
import net.jandie1505.bedwars.lobby.Lobby;
import net.jandie1505.bedwars.lobby.LobbyPlayerData;
import net.jandie1505.bedwars.game.MapData;
import net.jandie1505.bedwars.lobby.inventory.VotingMenu;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import java.util.*;

public class EventListener implements ManagedListener {
    private final Bedwars plugin;

    public EventListener(Bedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {

        // Send respawn packet

        if (Boolean.FALSE.equals(event.getEntity().getPlayer().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                event.getEntity().spigot().respawn();
            }, 1);
        }

        // Clear item drops

        event.getDrops().clear();

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

            if (((Game) this.plugin.getGame()).getData().spawnBlockPlaceProtection() > 0 || ((Game) this.plugin.getGame()).getData().villagerBlockPlaceProtection() > 0) {

                for (BedwarsTeam team : ((Game) this.plugin.getGame()).getTeams()) {

                    if (((Game) this.plugin.getGame()).getData().spawnBlockPlaceProtection() > 0) {

                        for (Location location : team.getData().spawnpoints()) {

                            if (Bedwars.getBlockDistance(WorldUtils.locationWithWorld(location, ((Game) this.plugin.getGame()).getWorld()), event.getBlock().getLocation()) <= ((Game) this.plugin.getGame()).getData().spawnBlockPlaceProtection()) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("§cYou cannot place blocks here");
                                return;
                            }

                        }

                    }

                    if (((Game) this.plugin.getGame()).getData().villagerBlockPlaceProtection() > 0) {

                        List<Location> villagerLocations = new ArrayList<>();
                        villagerLocations.addAll(team.getData().shopVillagerLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, ((Game) this.getGame()).getWorld())).toList());
                        villagerLocations.addAll(team.getData().upgradeVillagerLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, ((Game) this.getGame()).getWorld())).toList());

                        for (Location location : villagerLocations) {

                            if (Bedwars.getBlockDistance(location, event.getBlock().getLocation()) <= ((Game) this.plugin.getGame()).getData().villagerBlockPlaceProtection()) {
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

                    TNTPrimed tnt = (TNTPrimed) event.getBlockPlaced().getLocation().getWorld().spawnEntity(location, EntityType.TNT);
                    tnt.setSource(event.getPlayer());
                    tnt.setFuseTicks(80);
                }

                return;
            }

        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) return;

        if (this.plugin.getGame() != null && this.plugin.isPaused()) {
            event.setCancelled(true);
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
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

            // filter for right clicks

            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                return;
            }

            // Get playerdata

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

            if (playerData == null) {
                return;
            }

            // ender chest

            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
                event.getPlayer().openInventory(playerData.getEnderchest());
                return;
            }

            // here were the special items, but they were moved to SpecialItemListeners

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (this.plugin.getGame() != null && this.plugin.isPaused() && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
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

                for (UpgradeEntryOld upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
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

            ShopEntryOld shopEntry = ((Game) this.plugin.getGame()).getItemShop().getShopEntry(itemId);

            if (shopEntry != null) {

                if (!this.purchaseItem(event.getWhoClicked().getInventory(), shopEntry.getPrice(), shopEntry.getCurrency())) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + shopEntry.getCurrency().name() + "s!");
                    return;
                }

                ItemStack item = this.plugin.getGame().getPlugin().getItemStorage().getItem(itemId);

                if (item == null) {
                    return;
                }

                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getWhoClicked().getUniqueId());
                if (playerData != null) {
                    BedwarsTeam team = ((Game) this.plugin.getGame()).getTeam(playerData.getTeam());
                    if (team != null) {
                        Game.replaceBlockWithTeamColor(item, team);
                    }
                }

                event.getWhoClicked().sendMessage("§aItem successfully purchased");
                event.getWhoClicked().getInventory().addItem(item);
                event.getWhoClicked().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getPage(ShopMenu.getMenuPage(event.getInventory())));

                return;
            }

            UpgradeEntryOld upgradeEntry = ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntry(itemId);

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
                playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("playerUpgradePurchased", 0));
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
            } else if (itemId == ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getEndgameBuffUpgrade().getItemId()) {
                teamUpgrade = ((Game) this.plugin.getGame()).getTeamUpgradesConfig().getEndgameBuffUpgrade();
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
                playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("teamUpgradePurchased", 0));
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
                    playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("trapPurchased", 0));
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

                if (map.world().equals(lore.get(1))) {

                    event.getWhoClicked().closeInventory();

                    if (playerData.getVote() == map) {

                        playerData.setVote(null);
                        event.getWhoClicked().sendMessage("§aYou removed your vote");

                    } else {

                        playerData.setVote(map);
                        event.getWhoClicked().sendMessage("§aYou changed your vote to " + map.world());

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

            for (UpgradeEntryOld upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
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

            for (UpgradeEntryOld upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
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

        for (UpgradeEntryOld upgradeEntry : ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntries()) {
            for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                if (this.plugin.getItemStorage().getItemId(event.getItemDrop().getItemStack()) == itemId && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot drop upgradable items");
                    return;
                }

            }
        }

        Block block = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().clone().add(0, -1, 0));

        if (block.getType() == Material.AIR) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop items while you are not on the ground");
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

            if (block.getType() == Material.END_STONE && !(event.getEntity() instanceof TNTPrimed)) {
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

    @Override
    public GamePart getGame() {
        return this.plugin.getGame();
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
