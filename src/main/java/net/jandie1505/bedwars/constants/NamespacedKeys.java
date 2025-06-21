package net.jandie1505.bedwars.constants;

import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    String NAMESPACE = "bedwars";

    NamespacedKey GAME_ITEM_FIREBALL = new NamespacedKey(NAMESPACE, "item.game.special.fireball");
    NamespacedKey GAME_ITEM_ENHANCED_FIREBALL = new NamespacedKey(NAMESPACE, "item.game.special.fireball_enhanced");
    NamespacedKey GAME_ITEM_SAFETY_PLATFORM = new NamespacedKey(NAMESPACE, "item.game.special.safety_platform");
    NamespacedKey GAME_ITEM_SAFETY_PLATFORM_ENHANCED = new NamespacedKey(NAMESPACE, "item.game.special.safety_platform_enhanced");
    NamespacedKey GAME_ITEM_BRIDGE_EGG = new NamespacedKey(NAMESPACE, "item.game.special.bridge_egg");
    NamespacedKey GAME_ITEM_PLAYER_TRACKER = new NamespacedKey(NAMESPACE, "item.game.special.player_tracker");
    NamespacedKey GAME_ITEM_BASE_DEFENDER = new  NamespacedKey(NAMESPACE, "item.game.special.base_defender");
    NamespacedKey GAME_ITEM_SNOW_DEFENDER = new NamespacedKey(NAMESPACE, "item.game.special.snow_defender");
    NamespacedKey GAME_ITEM_ZAPPER = new NamespacedKey(NAMESPACE, "item.game.special.zapper");
    NamespacedKey GAME_ITEM_SPAWN_DUST = new NamespacedKey(NAMESPACE, "item.game.special.spawn_dust");
    NamespacedKey GAME_ITEM_BLACK_HOLE = new NamespacedKey(NAMESPACE, "item.game.special.black_hole");

    NamespacedKey ENTITY_TARGETING_ENABLED = new NamespacedKey(NAMESPACE, "entity.game.targeting_enabled");
    NamespacedKey ENTITY_PEARL_SWAP_EXCLUDED = new NamespacedKey(NAMESPACE, "entity.game.pearl_swap_excluded");
}
