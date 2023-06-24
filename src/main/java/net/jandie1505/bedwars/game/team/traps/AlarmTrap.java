package net.jandie1505.bedwars.game.team.traps;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class AlarmTrap extends BedwarsTrap {

    public AlarmTrap(BedwarsTeam team) {
        super(team);
    }

    @Override
    protected void run(Player player) {

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30*20, 255, false, false));

        for (UUID pid : this.getTeam().getPlayers()) {
            Player p = this.getTeam().getGame().getPlugin().getServer().getPlayer(pid);

            if (p == null) {
                continue;
            }

            p.sendTitle("§c§lALARM!!!", "§7Alarm trap triggered", 0, 3*20, 0);
            p.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);

        }

    }

    @Override
    public String getName() {
        return "Alarm Trap";
    }

    @Override
    public int getItemId() {
        return this.getTeam().getGame().getTeamUpgradesConfig().getAlarmTrap();
    }

}
