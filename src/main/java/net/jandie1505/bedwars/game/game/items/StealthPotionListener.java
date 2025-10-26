package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class StealthPotionListener implements ManagedListener {
    @NotNull private final Game game;

    public StealthPotionListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {

        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;

        String specialItemType = meta.getPersistentDataContainer().get(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING);
        if (specialItemType == null) return;
        if (!specialItemType.equals(CustomItemValues.STEALTH_POTION)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        int timerBefore = playerData.getTimer(PlayerTimers.TRAP_IMMUNITY);
        playerData.setTimer(PlayerTimers.TRAP_IMMUNITY, timerBefore + (30*20));

        event.getPlayer().getInventory().setItemInMainHand(null);

        event.getPlayer().sendRichMessage("<yellow>You are now invisible to traps for <aqua>" + (playerData.getTimer(PlayerTimers.TRAP_IMMUNITY) / 20) + " seconds<yellow>!");
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
