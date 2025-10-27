package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnvironmentScannerHandler implements ManagedListener {
    @NotNull private final Game game;

    public EnvironmentScannerHandler(@NotNull Game game) {
        this.game = game;
        this.game.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!CustomItemValues.isCustomItem(event.getItem(), CustomItemValues.ENVIRONMENT_SCANNER)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerData playerData = this.game.getPlayerData(player);
        if(playerData == null) return;

        int cooldown = playerData.getTimer(PlayerTimers.ENVIRONMENT_SCANNER_COOLDOWN);
        if (cooldown > 0) {
            player.sendRichMessage("<red>You need to wait <yellow>" + String.format("%.2f", ((double) cooldown / 20.0)) + "s<red> to use the environment scanner again!");
            player.playSound(player.getLocation().clone(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1, 2);
            return;
        }

        @Nullable Player nearestPlayer = this.findNearestPlayer(player, playerData);
        if (nearestPlayer == null) {
            this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "environment_scanner", 40, Component.text("✖ No player found!", NamedTextColor.DARK_RED));
            player.playSound(player.getLocation().clone(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1, 2);
            playerData.setTimer(PlayerTimers.ENVIRONMENT_SCANNER_COOLDOWN, 5*20);
            return;
        }

        nearestPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60*20, 1));
        this.showMessages(player, nearestPlayer);
        playerData.setTimer(PlayerTimers.ENVIRONMENT_SCANNER_COOLDOWN, 30*20);

        event.getItem().setAmount(event.getItem().getAmount() - 1);
    }

    private @Nullable Player findNearestPlayer(@NotNull Player player, @NotNull PlayerData playerData) {

        @Nullable Player nearestPlayer = null;
        double nearestDistance = Integer.MAX_VALUE;
        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (otherPlayer == player) continue;

            PlayerData otherPlayerData = this.game.getPlayerData(otherPlayer);
            if (otherPlayerData == null) continue;
            if (otherPlayerData.getTeam() == playerData.getTeam()) continue;

            if (otherPlayer.hasPotionEffect(PotionEffectType.GLOWING)) continue;

            double distance = player.getLocation().distanceSquared(otherPlayer.getLocation());
            if (distance < nearestDistance) {
                nearestPlayer = otherPlayer;
                nearestDistance = distance;
            }

        }

        return nearestPlayer;
    }

    private void showMessages(@NotNull Player player, @NotNull Player nearestPlayer) {

        PlayerData nearestPlayerData = this.game.getPlayerData(nearestPlayer);
        if (nearestPlayerData == null) return;

        BedwarsTeam nearestPlayerTeam = this.game.getTeam(nearestPlayerData.getTeam());
        if (nearestPlayerTeam == null) return;

        this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "environment_scanner", 60, Component.empty()
                .append(Component.text("\uD83D\uDD0E Found ", NamedTextColor.GREEN))
                .append(nearestPlayer.displayName()).color(nearestPlayerTeam.getChatColor())
                .append(Component.text("!", NamedTextColor.GREEN))
        );
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8f, 1f);
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
