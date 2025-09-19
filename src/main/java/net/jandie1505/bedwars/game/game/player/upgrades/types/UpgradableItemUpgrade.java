package net.jandie1505.bedwars.game.game.player.upgrades.types;

import net.chaossquad.mclib.json.JSONConfigUtils;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgradeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpgradableItemUpgrade extends ItemUpgrade {
    @NotNull private final List<ItemStack> items;

    /**
     * Creates a new player upgrade.
     *
     * @param manager     manager
     * @param data        data
     */
    public UpgradableItemUpgrade(@NotNull PlayerUpgradeManager manager, @NotNull Data data) {
        super(manager, data.id(), data.downgradeOnPlayerDeath(), data.keepFirstLevelOnDowngrade());
        this.items = data.items().stream().map(ItemStack::clone).toList();
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
     * @param playerData player data
     * @param level level
     */
    protected void giveItem(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        ItemStack item = this.getItem(level);
        if (item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, this.getId());
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_LEVEL, PersistentDataType.INTEGER, level);

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_PREVENT_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_KEEP_IN_PLAYER_INVENTORY, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        player.getInventory().addItem(item);
    }

    // ----- DATA -----

    /**
     * Stores data about UpgradableItemUpgrade.
     * @param id id
     * @param items items
     * @param downgradeOnPlayerDeath downgrade player on death
     * @param keepFirstLevelOnDowngrade keep first level on downgrade
     */
    public record Data(@NotNull String id, @NotNull List<ItemStack> items,  boolean downgradeOnPlayerDeath, boolean keepFirstLevelOnDowngrade) implements PlayerUpgrade.Data {

        public Data(@NotNull String id, @NotNull List<ItemStack> items,  boolean downgradeOnPlayerDeath, boolean keepFirstLevelOnDowngrade) {
            this.id = id;
            this.items = items.stream().map(ItemStack::clone).toList();
            this.downgradeOnPlayerDeath = downgradeOnPlayerDeath;
            this.keepFirstLevelOnDowngrade = keepFirstLevelOnDowngrade;
        }

        @Override
        public @NotNull String type() {
            return "upgradable_item";
        }

        /**
         * Creates a new UpgradableItemUpgrade Data from json.
         * @param id id
         * @param json json
         * @return UpgradableItemUpgrade
         */
        public static @NotNull Data fromJSON(@NotNull String id, @NotNull JSONObject json) {

            List<ItemStack> items = new ArrayList<>();
            JSONArray itemsJSON = json.getJSONArray("items");
            for (Object o : itemsJSON) {
                if (!(o instanceof JSONObject itemJSON)) throw new IllegalArgumentException("Invalid item");
                items.add(JSONConfigUtils.deserializeItem(itemJSON));
            }

            boolean downgradeOnPlayerDeath = json.getBoolean("downgrade_on_player_death");
            boolean keepFirstLevelOnDowngrade = json.getBoolean("keep_first_level_on_downgrade");

            return new Data(id, items, downgradeOnPlayerDeath, keepFirstLevelOnDowngrade);
        }

        /**
         * Converts this UpgradableItemUpgrade Data to json.
         * @return json
         */
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("type", this.type());

            JSONArray itemsJSON = new JSONArray();
            for (ItemStack item : this.items) {
                itemsJSON.put(JSONConfigUtils.serializeItem(item));
            }
            json.put("items", itemsJSON);

            json.put("downgrade_on_player_death", this.downgradeOnPlayerDeath);
            json.put("keep_first_level_on_downgrade", this.keepFirstLevelOnDowngrade);

            return json;
        }

        @Override
        public @NotNull PlayerUpgrade buildUpgrade(@NotNull PlayerUpgradeManager manager) {
            return new UpgradableItemUpgrade(manager, this);
        }

    }

    public @NotNull Data getData() {
        return new Data(this.getId(), this.items, this.isDowngradeOnPlayerDeath(), this.isKeepFirstLevelOnDowngrade());
    }

}
