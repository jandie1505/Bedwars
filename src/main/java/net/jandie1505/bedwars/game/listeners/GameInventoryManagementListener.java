package net.jandie1505.bedwars.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.shop.old.ShopEntryOld;
import net.jandie1505.bedwars.game.menu.shop.old.ShopMenu;
import net.jandie1505.bedwars.game.menu.shop.old.UpgradeEntryOld;
import net.jandie1505.bedwars.game.menu.upgrades.UpgradesMenu;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.team.traps.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

public class GameInventoryManagementListener implements ManagedListener {
    @NotNull private static final Set<InventoryType> ALLOWED_INVENTORY_TYPES = Set.of( // CRAFTING is btw not the crafting inventory, it's just the player's inventory. WORKBENCH would be the crafting table.
            InventoryType.CRAFTING, InventoryType.PLAYER, InventoryType.CHEST, InventoryType.BARREL, InventoryType.ENDER_CHEST, InventoryType.SHULKER_BOX,
            InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.HOPPER, InventoryType.JUKEBOX
    );
    @NotNull private final Game game;

    public GameInventoryManagementListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- LISTENERS -----

    /**
     * Manages the player's own inventory.
     * @param event event
     */
    @EventHandler
    public void onInventoryClickForPlayerInventory(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() != player) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        if (!this.game.isPlayerIngame(player)) {
            event.setCancelled(true);
            return;
        }

        // Block armor slots
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            player.sendRichMessage("<red>You cannot modify your armor slots!");
            return;
        }

        // Block crafting
        if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
            event.setCancelled(true);
            player.sendRichMessage("<red>You cannot use crafting!");
            return;
        }

        // Prevent putting on armor via shift-right-click
        if (this.game.getPlugin().getItemStorage().isArmorItem(event.getCurrentItem())) {
            event.setCancelled(true);
            player.sendRichMessage("<red>You need to purchase armor in the item shop!");
            return;
        }

        // Block dropping some items
        if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {

            // Default weapon
            if (this.game.getItemShop().getDefaultWeapon() != null && this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem()) == this.game.getItemShop().getDefaultWeapon()) {
                event.setCancelled(true);
                event.getWhoClicked().sendRichMessage("<red>You cannot drop the default weapon!");
                return;
            }

            // Upgradable items
            for (UpgradeEntryOld upgradeEntry : this.game.getItemShop().getUpgradeEntries()) {
                for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                    if (this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem()) == itemId) {
                        event.setCancelled(true);
                        event.getWhoClicked().sendRichMessage("<red>You cannot drop upgradable items!");
                        return;
                    }

                }
            }

        }

    }

    @EventHandler
    public void onInventoryDragForPlayerInventory(@NotNull InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() != player) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        if (!this.game.isPlayerIngame(player)) {
            event.setCancelled(true);
            return;
        }

        // Block armor slots
        if (event.getInventorySlots().contains(36) || event.getInventorySlots().contains(37) || event.getInventorySlots().contains(38) || event.getInventorySlots().contains(39)) {
            event.setCancelled(true);
            event.getWhoClicked().sendRichMessage("<red>You cannot modify your armor slots!");
            return;
        }

        // Block Crafting
        if (event.getRawSlots().contains(0) || event.getRawSlots().contains(1) || event.getRawSlots().contains(2) || event.getRawSlots().contains(3) || event.getRawSlots().contains(4)) {
            event.setCancelled(true);
            return;
        }

    }

    /**
     * Blocks forbidden inventory types.
     * @param event event
     */
    @EventHandler
    public void onInventoryClickForBlockingInventories(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() == player) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        if (!this.game.isPlayerIngame(player)) {
            event.setCancelled(true);
            return;
        }

        // block not allowed inventories
        if (!ALLOWED_INVENTORY_TYPES.contains(event.getInventory().getType())) {
            event.setCancelled(true);
            return;
        }

        if (this.game.getItemShop().getDefaultWeapon() != null && this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem()) == this.game.getItemShop().getDefaultWeapon()) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cYou cannot move the default weapon to other inventories");
            return;
        }

        for (UpgradeEntryOld upgradeEntry : this.game.getItemShop().getUpgradeEntries()) {
            for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                if (this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem()) == itemId) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cYou cannot move upgradable items to other inventories");
                    return;
                }

            }
        }

    }

    @EventHandler
    public void onInventoryDragForBlockingInventories(@NotNull InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() == player) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        if (!this.game.isPlayerIngame(player)) {
            event.setCancelled(true);
            return;
        }

        if (!ALLOWED_INVENTORY_TYPES.contains(event.getInventory().getType())) {
            event.setCancelled(true);
            return;
        }

        if (this.game.getItemShop().getDefaultWeapon() != null && this.game.getPlugin().getItemStorage().getItemId(event.getOldCursor()) == this.game.getItemShop().getDefaultWeapon()) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cYou cannot move the default weapon to other inventories");
            return;
        }

        for (UpgradeEntryOld upgradeEntry : this.game.getItemShop().getUpgradeEntries()) {
            for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                if (this.game.getPlugin().getItemStorage().getItemId(event.getOldCursor()) == itemId) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cYou cannot move upgradable items to other inventories");
                    return;
                }

            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;

        // Prevent spectators from dropping items
        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent dropping default weapon
        if (this.game.getItemShop().getDefaultWeapon() != null && this.game.getPlugin().getItemStorage().getItemId(event.getItemDrop().getItemStack()) == this.game.getItemShop().getDefaultWeapon()) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>You cannot drop the default weapon!");
            return;
        }

        // Prevent dropping upgrade items
        for (UpgradeEntryOld upgradeEntry : this.game.getItemShop().getUpgradeEntries()) {
            for (int itemId : upgradeEntry.getUpgradeItemIds()) {

                if (this.game.getPlugin().getItemStorage().getItemId(event.getItemDrop().getItemStack()) == itemId && !this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    event.getPlayer().sendRichMessage("<red>You cannot drop upgradable items!");
                    return;
                }

            }
        }

        // Prevent players from dropping items while not on ground (which should prevent players from dropping resources when being knocked into the void)
        Block blockBelow = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().clone().add(0, -1, 0));
        Block blockBelowBlockBelow = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().clone().add(0, -2, 0));
        if (blockBelow.getType() == Material.AIR && blockBelowBlockBelow.getType() == Material.AIR) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>You cannot drop items while you are not on the ground!");
        }

    }

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    // ----- STUFF THAT HAS TO BE REFACTORED -----

    /**
     * @deprecated Should be integrated into the gui class itself, will be done when the gui is rewritten (TODO)
     */
    @Deprecated
    @EventHandler
    public void onInventoryClickForShopMenu(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopMenu shopMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) return;
        if (event.getCurrentItem() == null) return;

        int itemId = this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem());
        if (itemId < 0) return;

        Integer[] menuItems = this.game.getItemShop().getMenuItems();
        for (int i = 0; i < menuItems.length; i++) {

            if (menuItems[i] != null && menuItems[i] == itemId) {
                event.getWhoClicked().openInventory(new ShopMenu(this.game, event.getWhoClicked().getUniqueId()).getPage(i));
                return;
            }

        }

        ShopEntryOld shopEntry = this.game.getItemShop().getShopEntry(itemId);
        if (shopEntry != null) {

            if (!this.purchaseItem(event.getWhoClicked().getInventory(), shopEntry.getPrice(), shopEntry.getCurrency())) {
                event.getWhoClicked().sendMessage("§cYou don't have enough " + shopEntry.getCurrency().name() + "s!");
                return;
            }

            ItemStack item = this.game.getPlugin().getItemStorage().getItem(itemId);

            if (item == null) {
                return;
            }

            BedwarsTeam team = this.game.getTeam(playerData.getTeam());
            if (team != null) {
                Game.replaceBlockWithTeamColor(item, team);
            }

            event.getWhoClicked().sendMessage("§aItem successfully purchased");
            event.getWhoClicked().getInventory().addItem(item);
            event.getWhoClicked().openInventory(new ShopMenu(this.game, event.getWhoClicked().getUniqueId()).getPage(ShopMenu.getMenuPage(event.getInventory())));

            return;
        }

        UpgradeEntryOld upgradeEntry = this.game.getItemShop().getUpgradeEntry(itemId);

        if (upgradeEntry != null) {

            int upgradeLevel = upgradeEntry.getUpgradeLevel(playerData) + 1;
            if (upgradeLevel < 0) return;

            int price = upgradeEntry.getUpgradePrice(upgradeLevel);
            if (price < 0) return;

            Material currency = upgradeEntry.getUpgradeCurrency(upgradeLevel);
            if (currency == null) return;

            if (!this.purchaseItem(event.getWhoClicked().getInventory(), price, currency)) {
                event.getWhoClicked().sendMessage("§cYou don't have enough " + currency.name() + "s!");
                return;
            }

            event.getWhoClicked().sendMessage("§aItem successfully purchased");
            upgradeEntry.upgradePlayer(playerData);
            playerData.setRewardPoints(playerData.getRewardPoints() + this.game.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("playerUpgradePurchased", 0));
            event.getWhoClicked().openInventory(new ShopMenu(this.game, event.getWhoClicked().getUniqueId()).getPage(ShopMenu.getMenuPage(event.getInventory())));

            return;
        }

        return;
    }

    @Deprecated
    @EventHandler
    public void onInventoryDragForShopMenu(@NotNull InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopMenu)) return;
        event.setCancelled(true);
    }

    @Deprecated
    @EventHandler
    public void onInventoryClickForUpgradeMenu(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof UpgradesMenu upgradesMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        int itemId = this.game.getPlugin().getItemStorage().getItemId(event.getCurrentItem());
        if (itemId < 0) return;

        TeamUpgrade teamUpgrade;
        if (itemId == this.game.getTeamUpgradesConfig().getSharpnessUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getSharpnessUpgrade();
        } else if (itemId == this.game.getTeamUpgradesConfig().getProtectionUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getProtectionUpgrade();
        } else if (itemId == this.game.getTeamUpgradesConfig().getHasteUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getHasteUpgrade();
        } else if (itemId == this.game.getTeamUpgradesConfig().getForgeUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getForgeUpgrade();
        } else if (itemId == this.game.getTeamUpgradesConfig().getHealPoolUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getHealPoolUpgrade();
        } else if (itemId == this.game.getTeamUpgradesConfig().getEndgameBuffUpgrade().getItemId()) {
            teamUpgrade = this.game.getTeamUpgradesConfig().getEndgameBuffUpgrade();
        } else {
            teamUpgrade = null;
        }

        BedwarsTeam team = this.game.getTeams().get(playerData.getTeam());
        if (team == null) return;

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
            playerData.setRewardPoints(playerData.getRewardPoints() + this.game.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("teamUpgradePurchased", 0));
            event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());

            return;
        }

        if (event.getSlot() < 27) {

            BedwarsTrap bedwarsTrap;
            if (itemId == this.game.getTeamUpgradesConfig().getAlarmTrap()) {
                bedwarsTrap = new AlarmTrap(team);
            } else if (itemId == this.game.getTeamUpgradesConfig().getItsATrap()) {
                bedwarsTrap = new ItsATrap(team);
            } else if (itemId == this.game.getTeamUpgradesConfig().getMiningFatigueTrap()) {
                bedwarsTrap = new MiningFatigueTrap(team);
            } else if (itemId == this.game.getTeamUpgradesConfig().getCountermeasuresTrap()) {
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
                playerData.setRewardPoints(playerData.getRewardPoints() + this.game.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("trapPurchased", 0));
                event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());

            }

        } else {

            if (event.getSlot() == 38) {
                team.getPrimaryTraps()[0] = null;
                event.getWhoClicked().sendMessage("§aTrap successfully removed");
                event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());
            } else if (event.getSlot() == 39) {
                team.getPrimaryTraps()[1] = null;
                event.getWhoClicked().sendMessage("§aTrap successfully removed");
                event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());
            } else if (event.getSlot() == 41) {
                team.getSecondaryTraps()[0] = null;
                event.getWhoClicked().sendMessage("§aTrap successfully removed");
                event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());
            } else if (event.getSlot() == 42) {
                team.getSecondaryTraps()[1] = null;
                event.getWhoClicked().sendMessage("§aTrap successfully removed");
                event.getWhoClicked().openInventory(new UpgradesMenu(this.game, event.getWhoClicked().getUniqueId()).getUpgradesMenu());
            }

        }

        return;
    }

    @Deprecated
    @EventHandler
    public void onInventoryDragForUpgradeMenu(@NotNull InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof UpgradesMenu)) return;
        event.setCancelled(true);
    }

    @Deprecated
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

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
