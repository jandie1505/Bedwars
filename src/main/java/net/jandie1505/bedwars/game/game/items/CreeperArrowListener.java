package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CreeperArrowListener implements ManagedListener {
    @NotNull private final Game game;

    public CreeperArrowListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onProjectileHit(@NotNull ProjectileHitEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Arrow arrow)) return;

        ItemStack item = arrow.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, "").equals(CustomItemValues.CREEPER_ARROW)) return;

        this.game.getTaskScheduler().runTaskLater(() -> {
            Creeper creeper = this.game.getWorld().spawn(event.getEntity().getLocation().clone(), Creeper.class);
            creeper.getPersistentDataContainer().set(NamespacedKeys.ENTITY_TARGETING_ENABLED, PersistentDataType.BOOLEAN, true);
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10*20, 0, true, true, true));
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 1, true, true, true));
        }, 1, "creeper_arrow_spawn");

    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
