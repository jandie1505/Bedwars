package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectImmunityListener implements ManagedListener {
    @NotNull private final Game game;

    public EffectImmunityListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onEntityPotionEffectForEffectImmunity(EntityPotionEffectEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (this.game.getPlugin().isPlayerBypassing(player)) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        if (playerData.getTimer(PlayerTimers.EFFECT_IMMUNITY) <= 0) return;

        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {

        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;

        String specialItemType = meta.getPersistentDataContainer().get(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING);
        if (specialItemType == null) return;
        if (!specialItemType.equals(CustomItemValues.EFFECT_IMMUNITY_POTION)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        int timerBefore = playerData.getTimer(PlayerTimers.EFFECT_IMMUNITY);
        playerData.setTimer(PlayerTimers.EFFECT_IMMUNITY, timerBefore + (30*20));

        event.getPlayer().getInventory().setItemInMainHand(null);

        if (timerBefore > 0) {
            event.getPlayer().sendRichMessage("<yellow>You have increased your effect immunity to <aqua>" + (playerData.getTimer(PlayerTimers.EFFECT_IMMUNITY) / 20) + " seconds<yellow>!");
        } else {
            event.getPlayer().sendRichMessage("<yellow>You are now immune to potion effects for the next <aqua>" + (playerData.getTimer(PlayerTimers.EFFECT_IMMUNITY) / 20) + " seconds<yellow>!");
        }
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
