package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.game.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SingleUseJetpackHandler implements ManagedListener {
    @NotNull private final Game game;

    public SingleUseJetpackHandler(@NotNull Game game) {
        this.game = game;
        this.game.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!CustomItemValues.isCustomItem(event.getItem(), CustomItemValues.SINGLE_USE_JETPACK)) return;
        this.handleSingleUseJetpack(event.getPlayer(), event.getItem());
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;
        if (!CustomItemValues.isCustomItem(event.getMainHandItem(), CustomItemValues.SINGLE_USE_JETPACK)) return;
        this.handleSingleUseJetpack(event.getPlayer(), event.getPlayer().getInventory().getItemInOffHand());
        event.setCancelled(true);
    }

    private void handleSingleUseJetpack(@NotNull Player player, @NotNull ItemStack item) {

        if (!hasBlockNearby(player)) {
            this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "single_use_jetpack", 60, Component.text("✖ You need to be on ground!", NamedTextColor.DARK_RED));
            player.playSound(player.getLocation().clone(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1, 2);
            return;
        }

        Vector velocity = player.getVelocity().clone();
        velocity.setY(velocity.getY() + 1.5);
        player.setVelocity(velocity);

        player.getWorld().playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 0.8f, 0.75f);

        item.setAmount(item.getAmount() - 1);
    }

    private boolean hasBlockNearby(@NotNull Player player) {

        for (int x = player.getLocation().getBlockX() - 1; x <= player.getLocation().getBlockX() + 1; x++) {
            for (int y = player.getLocation().getBlockY() - 2; y <= player.getLocation().getBlockY(); y++) {
                for (int z = player.getLocation().getBlockZ() - 1; z <= player.getLocation().getBlockZ() + 1; z++) {

                    Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
                    if (block.getType() != Material.AIR) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
