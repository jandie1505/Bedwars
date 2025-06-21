package net.jandie1505.bedwars.constants;

import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    String NAMESPACE = "bedwars";

    NamespacedKey GAME_ITEM_BRIDGE_EGG = new NamespacedKey(NAMESPACE, "item.game.bridge_egg");
    NamespacedKey ENTITY_TARGETING_ENABLED = new NamespacedKey(NAMESPACE, "entity.game.targeting_enabled");
    NamespacedKey ENTITY_PEARL_SWAP_EXCLUDED = new NamespacedKey(NAMESPACE, "entity.game.pearl_swap_excluded");
}
