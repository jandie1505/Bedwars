package net.jandie1505.bedwars.game.team.traps;

import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CountermeasuresTrap extends BedwarsTrap {
    public CountermeasuresTrap(BedwarsTeam team) {
        super(team);
    }

    @Override
    protected void run(Player player) {

        for (UUID pid : this.getTeam().getPlayers()) {
            Player p = this.getTeam().getGame().getPlugin().getServer().getPlayer(pid);

            if (p == null) {
                continue;
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 30*20, 1, true, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 1, true, true));
            p.sendTitle("", "§acountermeasures enabled", 0, 3*20, 0);
            p.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0);

        }

    }

    @Override
    public String getName() {
        return "Countermeasures Trap";
    }

    @Override
    public int getItemId() {
        return this.getTeam().getGame().getTeamUpgradesConfig().getCountermeasuresTrap();
    }
}
