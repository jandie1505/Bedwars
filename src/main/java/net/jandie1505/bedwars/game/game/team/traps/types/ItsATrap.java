package net.jandie1505.bedwars.game.game.team.traps.types;

import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrap;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ItsATrap extends TeamTrap {

    public ItsATrap(@NotNull TeamTrapManager manager, @NotNull String id) {
        super(manager, id);
    }

    @Override
    protected void onTrigger(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30*20, 1, true, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30*20, 1, true, true, true));

        player.sendMessage(Component.empty().appendNewline()
                .append(Component.text("⚠ You have triggered", NamedTextColor.RED)).appendSpace()
                .append(Component.text("It's a Trap", NamedTextColor.YELLOW)).appendSpace()
                .append(Component.text("of")).appendSpace()
                .append(Component.text("Team", team.getChatColor())).appendSpace()
                .append(team.getFormattedName())
                .append(Component.text("!", NamedTextColor.RED)).appendNewline()
        );

    }

}
