package net.jandie1505.bedwars.game.team.traps;

import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MiningFatigueTrap extends BedwarsTrap {
    public MiningFatigueTrap(BedwarsTeam team) {
        super(team);
    }

    @Override
    protected void run(Player player) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20*20, 1, true, true));

    }

    @Override
    public String getName() {
        return "Mining Fatigue Trap";
    }

    @Override
    public int getItemId() {
        return this.getTeam().getGame().getTeamUpgradesConfig().getMiningFatigueTrap();
    }
}
