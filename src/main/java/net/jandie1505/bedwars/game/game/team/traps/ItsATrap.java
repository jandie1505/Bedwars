package net.jandie1505.bedwars.game.game.team.traps;

import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItsATrap extends BedwarsTrap {
    public ItsATrap(BedwarsTeam team) {
        super(team);
    }

    @Override
    protected void run(Player player) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8*20, 1, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 8*20, 1, true, true));

    }

    @Override
    public String getName() {
        return "It's a Trap";
    }

    @Override
    public int getItemId() {
        return this.getTeam().getGame().getTeamUpgradesConfig().getItsATrap();
    }
}
