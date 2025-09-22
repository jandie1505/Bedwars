package net.jandie1505.bedwars.game.game.team.upgrades.types;

import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HealPoolTeamUpgrade extends TeamUpgrade {

    /**
     * Creates a new team upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public HealPoolTeamUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id) {
        super(manager, id);

        this.scheduleRepeatingTask(this::task, 1, 10*20, "task");
    }

    // ----- TASK -----

    private void task(@NotNull BedwarsTeam team, int level) {
        if (level <= 0) return;

        Collection<Player> players = this.getManager().getGame().getWorld().getNearbyEntitiesByType(Player.class, team.getBaseCenter(), team.getBaseRadius(), team.getBaseRadius(), team.getBaseRadius(), player -> {

            PlayerData playerData = this.getManager().getGame().getPlayerData(player);
            if (playerData == null) return false;

            // TODO: Check for effect immunity

            return true;
        });

        players.forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15*20, level - 1, true, true, true)));

    }

}
