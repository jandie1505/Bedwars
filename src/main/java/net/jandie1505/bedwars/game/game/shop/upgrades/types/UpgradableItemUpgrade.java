package net.jandie1505.bedwars.game.game.shop.upgrades.types;

import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.shop.UpgradeManager;
import net.jandie1505.bedwars.game.game.shop.upgrades.PlayerUpgrade;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradableItemUpgrade extends PlayerUpgrade {
    @NotNull private final List<ItemStack> items;
    @NotNull private final Component name;
    @NotNull private final Component description;
    @NotNull private final List<ItemStack> icons;

    /**
     * Creates a new player upgrade.
     *
     * @param manager     manager
     * @param id          id
     * @param name        name
     * @param description description
     * @param icons       icons
     */
    public UpgradableItemUpgrade(@NotNull UpgradeManager manager, @NotNull String id, @NotNull Component name, @NotNull Component description, @NotNull List<ItemStack> icons, @NotNull List<ItemStack> items) {
        super(manager, id);
        this.items = items.stream().map(ItemStack::clone).toList();
        this.name = name;
        this.description = description;
        this.icons = icons;

        this.scheduleRepeatingTask(this::checkItem, 1, 20, "check_item");
    }

    // ----- APPLY/REMOVE -----

    @Override
    public void onApply(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeItem(player);
        this.giveItem(player, level);
    }

    @Override
    public void onRemove(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeItem(player);
    }

    @Override
    public void onAffectedPlayerDeath(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeItem(player);
    }

    @Override
    public void onAffectedPlayerRespawn(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeItem(player);
        this.giveItem(player, level);
    }

    private void checkItem(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        boolean itemAvail = false;

        for (int slot = 0; slot < player.getInventory().getSize() + 1; slot++) {

            ItemStack item;
            if (slot < player.getInventory().getSize()) {
                item = player.getInventory().getItem(slot);
            } else {
                item = player.getItemOnCursor();
            }

            if (item == null) continue;
            if (item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").isEmpty()) continue;
            itemAvail = true;

            int upgradeLevel = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_LEVEL, PersistentDataType.INTEGER, -1);

            // Level <= 0 means that the player can't have an item.
            // This is also an invalid id check for the item's upgradeLevel.
            if (upgradeLevel <= 0 || level <= 0) {
                this.removeItem(player);
                itemAvail = false;
                continue;
            }

            // If there is an item with the wrong upgrade level, clear and re-give.
            if (upgradeLevel != level) {
                this.removeItem(player);
                this.giveItem(player, level);
                continue;
            }

        }

        // Give item to player when not already available

        if (level <= 0) return;
        if (itemAvail) return;

        this.removeItem(player);
        this.giveItem(player, level);
    }

    // ----- ITEM -----

    /**
     * Returns the ItemStack of the specified level.
     * @param level level
     * @return ItemStack of that level
     */
    public @NotNull ItemStack getItem(int level) {
        if (this.items.isEmpty()) return new ItemStack(Material.AIR);

        if (level <= 0) return new ItemStack(Material.AIR);
        level -= 1;

        if (level >= this.items.size()) return this.items.getLast();

        return this.items.get(level);
    }

    /**
     * Gives the player the item with the specified level and returns that ItemStack.
     * @param player player
     * @param level level
     * @return ItemStack that has been given to the player
     */
    private @Nullable ItemStack giveItem(@NotNull Player player, int level) {
        ItemStack item = this.getItem(level);
        if (item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, this.getId());
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_LEVEL, PersistentDataType.INTEGER, level);

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_PREVENT_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_KEEP_IN_PLAYER_INVENTORY, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        player.getInventory().addItem(item);
        return item;
    }

    /**
     * Removes all items of this upgrade from the specified player's inventory.
     * @param player player
     */
    private void removeItem(@NotNull Player player) {
        this.removeItem(player.getInventory());
    }

    /**
     * Removes all items of this upgrade from the specified inventory.
     * @param inventory inventory
     */
    private void removeItem(@NotNull Inventory inventory) {

        for (int slot = 0; slot < inventory.getSize(); slot++) {

            ItemStack item = inventory.getItem(slot);
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").equals(this.getId())) {
                continue;
            }

            inventory.clear(slot);
        }

    }

    // ----- APPEARANCE -----

    @Override
    public @NotNull Component getName() {
        return this.name;
    }

    @Override
    public @NotNull Component getDescription() {
        return this.description;
    }

    @Override
    public @NotNull List<ItemStack> getIcons() {
        return this.icons;
    }

}
