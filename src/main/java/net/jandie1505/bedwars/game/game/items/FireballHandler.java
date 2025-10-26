package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class FireballHandler implements ManagedListener {
    @NotNull private final Game game;

    public FireballHandler(@NotNull Game game) {
        this.game = game;
        this.game.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteractForFireball(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();

        boolean isFireball = CustomItemValues.isCustomItem(event.getItem(), CustomItemValues.FIREBALL);
        boolean isEnhancedFireball = CustomItemValues.isCustomItem(event.getItem(), CustomItemValues.ENHANCED_FIREBALL);
        if (!isFireball && !isEnhancedFireball) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        int cooldown = playerData.getTimer(PlayerTimers.FIREBALL_COOLDOWN);
        if (cooldown > 0) {
            player.sendRichMessage("<red>You need to wait <yellow>" + String.format("%.2f", ((double) cooldown / 20.0)) + "s<red> to use the fireball again!");
            player.playSound(player.getLocation().clone(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1, 2);
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        if (isEnhancedFireball) {
            this.spawnEnhancedFireball(player);
            playerData.setTimer(PlayerTimers.FIREBALL_COOLDOWN, 50);
        } else {
            this.spawnFireball(player);
            playerData.setTimer(PlayerTimers.FIREBALL_COOLDOWN, 30);
        }

        player.getWorld().playSound(player, Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.5f, 1f);

    }

    private void spawnFireball(@NotNull Player player) {

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setShooter(player);
        fireball.setDirection(player.getEyeLocation().getDirection());
        fireball.setYield(2);
        fireball.setIsIncendiary(false);
        fireball.setTicksLived(3*20);

    }

    private void spawnEnhancedFireball(@NotNull Player player) {

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setShooter(player.getPlayer());
        fireball.setDirection(player.getEyeLocation().getDirection().multiply(2));
        fireball.setYield(4);
        fireball.setIsIncendiary(false);
        fireball.setTicksLived(10*20);

    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
