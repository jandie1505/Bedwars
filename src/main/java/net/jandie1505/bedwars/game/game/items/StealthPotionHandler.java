package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class StealthPotionHandler implements ManagedListener {
    @NotNull private final Game game;

    public StealthPotionHandler(@NotNull Game game) {
        this.game = game;
        this.game.registerListener(this);
        this.game.getTaskScheduler().scheduleRepeatingTask(this::showStealthPotionActionBar, 1, 20, this, "trap_immunity_action_bar");
    }

    private void showStealthPotionActionBar() {
        this.game.getOnlinePlayers().forEach(player -> {

            PlayerData playerData = this.game.getPlayerData(player);
            if (playerData == null) return;

            int timer = playerData.getTimer(PlayerTimers.TRAP_IMMUNITY);
            if (timer <= 0) return;

            this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "trap_immunity", 25, Component.text("\uD83D\uDD73" + (timer / 20), NamedTextColor.YELLOW));
        });
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {

        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;

        String specialItemType = meta.getPersistentDataContainer().get(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING);
        if (specialItemType == null) return;
        if (!specialItemType.equals(CustomItemValues.STEALTH_POTION)) return;

        event.setCancelled(true);

        if (!event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem())) {
            event.getPlayer().sendRichMessage("<red>You can't use your offhand to consume this item!");
            return;
        }

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
