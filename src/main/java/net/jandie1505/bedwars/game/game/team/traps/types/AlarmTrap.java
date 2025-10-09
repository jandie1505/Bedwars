package net.jandie1505.bedwars.game.game.team.traps.types;

import net.chaossquad.mclib.scheduler.TaskScheduler;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrap;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Trap which reveals invisible players, gives them glowing and shows an alarm message to all team members.
 */
public class AlarmTrap extends TeamTrap {

    public AlarmTrap(@NotNull TeamTrapManager manager, @NotNull String id) {
        super(manager, id);
    }

    @Override
    protected void onTrigger(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData) {

        // Remove invisibility
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        // Glowing
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60*20, 1, true, false, true));

        // Get Team
        BedwarsTeam playerTeam = this.getManager().getGame().getTeam(playerData.getTeam());
        if (playerTeam == null) return;

        // Warning message for team
        for (Player member : team.getOnlineMembers()) {

            member.sendMessage(Component.empty().appendNewline()
                    .append(Component.text("WARNING!", NamedTextColor.RED, TextDecoration.BOLD)).appendNewline()
                    .append(Component.text("The alarm trap has been triggered by", NamedTextColor.RED)).appendSpace()
                    .append(player.displayName().color(playerTeam.getChatColor()))
                    .append(Component.text("!", NamedTextColor.RED)).appendNewline()
            );

            member.showTitle(Title.title(
                    Component.text("⚠", NamedTextColor.RED),
                    Component.text("Alarm trap triggered!", NamedTextColor.RED),
                    Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
            ));

            this.playWarningSound(player);

        }

        // Warning message for triggerer
        player.sendMessage(Component.empty().appendNewline()
                .append(Component.text("⚠ You have triggered the alarm trap of Team", NamedTextColor.RED)).appendSpace()
                .append(team.getFormattedName())
                .append(Component.text("!", NamedTextColor.RED)).appendNewline()
        );

        player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1);

    }

    private void playWarningSound(@NotNull Player player) {
        TaskScheduler scheduler = this.getManager().getGame().getTaskScheduler();
        scheduler.runTaskLater(() -> player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1), 1, "warning_sound_" + player.getUniqueId() + "_1");
        scheduler.runTaskLater(() -> player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1), 3, "warning_sound_" + player.getUniqueId() + "_2");
        scheduler.runTaskLater(() -> player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1), 5, "warning_sound_" + player.getUniqueId() + "_3");
        scheduler.runTaskLater(() -> player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1), 7, "warning_sound_" + player.getUniqueId() + "_4");
        scheduler.runTaskLater(() -> player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1), 9, "warning_sound_" + player.getUniqueId() + "_5");

    }

}
