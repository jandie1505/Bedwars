package net.jandie1505.bedwars.constants;

import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    String NAMESPACE = "bedwars";

    NamespacedKey GAME_ITEM_PREVENT_DROP = new NamespacedKey(NAMESPACE, "item.game.properties.prohibit_drop");
    NamespacedKey GAME_ITEM_KEEP_IN_PLAYER_INVENTORY = new NamespacedKey(NAMESPACE, "item.game.properties.lock_player_inventory");

    NamespacedKey GAME_SPECIAL_ITEM = new NamespacedKey(NAMESPACE, "item.game.special");

    NamespacedKey GAME_ITEM_UPGRADE_ID = new NamespacedKey(NAMESPACE, "item.game.upgrades.upgrade.id");
    NamespacedKey GAME_ITEM_UPGRADE_LEVEL = new NamespacedKey(NAMESPACE, "item.game.upgrades.upgrade.level");

    NamespacedKey ENTITY_TARGETING_ENABLED = new NamespacedKey(NAMESPACE, "entity.game.targeting_enabled");
    NamespacedKey ENTITY_PEARL_SWAP_EXCLUDED = new NamespacedKey(NAMESPACE, "entity.game.pearl_swap_excluded");
}
