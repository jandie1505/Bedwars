package net.jandie1505.bedwars.game.game.team.traps.types;

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

public class CountermeasuresTrap extends TeamTrap {

    public CountermeasuresTrap(@NotNull TeamTrapManager manager, @NotNull String id) {
        super(manager, id);
    }

    @Override
    protected void onTrigger(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData) {

        for (Player member : team.getOnlineMembers()) {

            member.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 30*20, 1, true, true, true));
            member.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 1, true, true, true));
            member.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 1, true, true, true));
            member.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60*20, 1, true, true, true));

            member.sendMessage(Component.empty().appendNewline()
                    .append(Component.text("ATTENTION!", NamedTextColor.GREEN, TextDecoration.BOLD)).appendNewline()
                    .append(Component.text("Countermeasures have been activated!", NamedTextColor.GREEN)).appendNewline()
            );

            member.showTitle(Title.title(
                    Component.empty(),
                    Component.text("Countermeasures activated!", NamedTextColor.GREEN),
                    Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
            ));

            player.playSound(player.getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1);

        }

    }

}
