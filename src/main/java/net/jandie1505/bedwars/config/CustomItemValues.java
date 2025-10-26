package net.jandie1505.bedwars.config;

import net.jandie1505.bedwars.constants.NamespacedKeys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomItemValues {
    String AUTOMATICALLY_IGNITING_TNT = "automatically_igniting_tnt";
    String AUTO_BRIDGE = "auto_bridge";
    String BASE_DEFENDER_SPAWN_EGG = "base_defender_spawn_egg";
    String BATTLEGROUND_PLATTFORM = "battleground_plattform";
    String BLACK_HOLE = "black_hole";
    String CREEPER_ARROW = "creeper_arrow";
    String DOG_SPAWN_EGG = "dog_spawn_egg";
    String EFFECT_IMMUNITY_POTION = "effect_immunity_potion";
    String ENHANCED_FIREBALL = "enhanced_fireball";
    String ENVIRONMENT_SCANNER = "environment_scanner";
    String FIREBALL = "fireball";
    String GRAPPLING_HOOK = "grappling_hook";
    String MOBILE_CASTLE = "mobile_castle";
    String SAFETY_PLATTFORM = "safety_plattform";
    String SAFETY_PLATTFORM_ENHANCED = "safety_plattform_enhanced";
    String SINGLE_USE_JETPACK = "single_use_jetpack";
    String SNOW_DEFENDER_SPAWN_EGG = "snow_defender_spawn_egg";
    String SPAWN_DUST = "spawn_dust";
    String STEALTH_POTION = "stealth_potion";
    String ZAPPER = "zapper";

    static boolean isCustomItem(@Nullable ItemStack item, @NotNull String customItemId) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        String specialItemType = meta.getPersistentDataContainer().get(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING);
        if (specialItemType == null) return false;

        return specialItemType.equals(customItemId);
    }

}
