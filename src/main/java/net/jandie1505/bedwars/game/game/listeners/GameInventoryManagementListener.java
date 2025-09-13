package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.menu.upgrades.UpgradesMenu;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.traps.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public void onInventoryClickForBlockingSlotsInPlayerInventory(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        // This only affects player inventories.
        if (event.getInventory().getHolder() != player) return;

        // Always cancel when player not ingame
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

    }

    @EventHandler
    public void onInventoryDragForPreventingSlotsInPlayerInventories(@NotNull InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        // This only affects player inventories.
        if (event.getInventory().getHolder() != player) return;

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
    public void onInventoryClickForBlockingOtherInventories(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return; // Keep cancelled when already cancelled
        if (!(event.getWhoClicked() instanceof Player player)) return; // Only players
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return; // Bypassing players can do anything

        boolean shouldBeBlocked = this.shouldOtherInventoryBeBlocked(player, event.getInventory(), event.getClickedInventory(), event.getCurrentItem());
        if (shouldBeBlocked) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDragForBlockingInventories(@NotNull InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) return;

        boolean shouldBeBlocked = this.shouldOtherInventoryBeBlocked(player, event.getInventory(), null, event.getOldCursor());
        if (shouldBeBlocked) event.setCancelled(true);

        /*
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

         */

    }

    private boolean shouldOtherInventoryBeBlocked(@NotNull Player player, @NotNull Inventory inventory, @Nullable Inventory clickedInventory, @Nullable ItemStack currentItem) {

        // Always cancel when player not ingame
        if (!this.game.isPlayerIngame(player)) return true;

        // Players can do things in their own inventories. This listener only affects other inventories.
        if (inventory.getHolder() == player) return false;

        // This ensures that players can also move their non-movable items in their own inventory when another inventory is open.
        // For example, a player can move their pickaxe in their inventory when a chest is open, but not into the chest's inventory.
        // Currently broken. TODO: Fix
        //if (clickedInventory != null && clickedInventory.getHolder() == player) return false;

        if (currentItem == null) return false;

        // block not allowed inventories
        if (!ALLOWED_INVENTORY_TYPES.contains(inventory.getType())) return true;

        // The moveable value cannot be got from an item which has no meta.
        ItemMeta meta = currentItem.getItemMeta();
        if (meta == null) return false;

        // Block if not moveable (keep=true)
        return meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_KEEP_IN_PLAYER_INVENTORY, PersistentDataType.BOOLEAN, false);
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;

        // Prevent spectators from dropping items
        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();

        // TODO: Add config option for this
        // Prevent players from dropping items while not on ground (which should prevent players from dropping resources when being knocked into the void)
        Block blockBelow = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().clone().add(0, -1, 0));
        Block blockBelowBlockBelow = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().clone().add(0, -2, 0));
        if (blockBelow.getType() == Material.AIR && blockBelowBlockBelow.getType() == Material.AIR) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>You cannot drop items while you are not on the ground!");
            return;
        }

        // NEW CHECK

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // Prevent drop
        if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_PREVENT_DROP, PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.isPlayerIngame(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (!this.game.getPlugin().getItemStorage().isArmorItem(event.getItem())) return;

        event.setCancelled(true);
    }

    // ----- STUFF THAT HAS TO BE REFACTORED -----
    // TODO: Refactor

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
