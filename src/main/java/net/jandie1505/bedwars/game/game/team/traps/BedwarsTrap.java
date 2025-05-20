package net.jandie1505.bedwars.game.game.team.traps;

import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class BedwarsTrap {
    private final BedwarsTeam team;

    public BedwarsTrap(BedwarsTeam team) {
        this.team = team;
    }

    protected abstract void run(Player player);

    public void trigger(Player player) {
        this.run(player);

        player.sendTitle("", "§c" + this.getName() +  " triggered", 0, 2*20, 0);
        player.sendMessage("§cYou triggered " + this.getName());
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);

        for (UUID pid : this.getTeam().getPlayers()) {
            Player p = this.getTeam().getGame().getPlugin().getServer().getPlayer(pid);

            if (p == null) {
                continue;
            }

            p.sendMessage("§bYour §c" + this.getName() + " §bhas been triggered");

        }
    }

    public abstract String getName();

    public abstract int getItemId();

    public BedwarsTeam getTeam() {
        return this.team;
    }
}
