package net.jandie1505.bedwars.game.game.player.upgrades.types;

import net.chaossquad.mclib.json.JSONConfigUtils;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgradeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpgradableItemUpgrade extends ItemUpgrade {
    @NotNull private final List<ItemStack> items;
    @NotNull private final Component name;
    @NotNull private final Component description;

    /**
     * Creates a new player upgrade.
     *
     * @param manager     manager
     * @param id          id
     * @param name        name
     * @param description description
     */
    public UpgradableItemUpgrade(@NotNull PlayerUpgradeManager manager, @NotNull String id, @NotNull Component name, @NotNull Component description, @NotNull List<ItemStack> items,  boolean downgradeOnPlayerDeath, boolean keepFirstLevelOnDowngrade) {
        super(manager, id, downgradeOnPlayerDeath, keepFirstLevelOnDowngrade);
        this.items = items.stream().map(ItemStack::clone).toList();
        this.name = name;
        this.description = description;
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

    // ----- APPEARANCE -----

    @Override
    public @NotNull Component getName() {
        return this.name;
    }

    @Override
    public @NotNull Component getDescription() {
        return this.description;
    }

    // ----- JSON -----

    /**
     * Creates a new UpgradableItemUpgrade from json.
     * @param manager manager
     * @param id id
     * @param json json
     * @return UpgradableItemUpgrade
     */
    public static @NotNull UpgradableItemUpgrade fromJSON(@NotNull PlayerUpgradeManager manager, @NotNull String id, @NotNull JSONObject json) {

        Component name = MiniMessage.miniMessage().deserialize(json.getString("name"));
        Component desc = MiniMessage.miniMessage().deserialize(json.getString("description"));

        List<ItemStack> items = new ArrayList<>();
        JSONArray itemsJSON = json.getJSONArray("items");
        for (Object o : itemsJSON) {
            if (!(o instanceof JSONObject itemJSON)) throw new IllegalArgumentException("Invalid item");
            items.add(JSONConfigUtils.deserializeItem(itemJSON));
        }

        boolean downgradeOnPlayerDeath = json.getBoolean("downgrade_on_player_death");
        boolean keepFirstLevelOnDowngrade = json.getBoolean("keep_first_level_on_downgrade");

        return new UpgradableItemUpgrade(manager, id, name, desc, items, downgradeOnPlayerDeath, keepFirstLevelOnDowngrade);
    }

    /**
     * Converts this UpgradableItemUpgrade to json.
     * @return json
     */
    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("name", MiniMessage.miniMessage().serialize(this.name));
        json.put("description", MiniMessage.miniMessage().serialize(this.description));

        JSONArray itemsJSON = new JSONArray();
        for (ItemStack item : this.items) {
            itemsJSON.put(JSONConfigUtils.serializeItem(item));
        }
        json.put("items", itemsJSON);

        json.put("downgrade_on_player_death", this.isDowngradeOnPlayerDeath());
        json.put("keep_first_level_on_downgrade", this.isKeepFirstLevelOnDowngrade());

        return json;
    }

}
