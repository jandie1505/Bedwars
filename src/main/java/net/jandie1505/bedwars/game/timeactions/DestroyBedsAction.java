package net.jandie1505.bedwars.game.timeactions;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DestroyBedsAction extends TimeAction {
    private final boolean disableBeds;

    public DestroyBedsAction(Game game, int time, boolean disableBeds) {
        super(game, time, "§cAll beds have been destroyed (replaceable=" + !disableBeds + ")", "Beds gone " + DestroyBedsAction.isBedReplaceable(disableBeds));
        this.disableBeds = disableBeds;
    }

    @Override
    protected void run() {
        for (Player player : this.getGame().getPlugin().getServer().getOnlinePlayers()) {

            PlayerData playerData = this.getGame().getPlayers().get(player.getUniqueId());

            if (playerData != null) {

                BedwarsTeam team = this.getGame().getTeams().get(playerData.getTeam());

                if (team == null) {
                    continue;
                }

                if (team.hasBed() > 0) {
                    player.playSound(player, Sound.ENTITY_WITHER_DEATH, 1, 1);
                    player.sendTitle("§cBED DESTROYED", "§7All beds have been destroyed", 10, 60, 10);
                } else {
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                }

            } else {
                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
            }

        }

        for (BedwarsTeam team : this.getGame().getTeams()) {
            team.destroyBeds();

            if (this.disableBeds) {
                team.disableBed();
            }
        }
    }

    public boolean isDisableBeds() {
        return this.disableBeds;
    }

    public static String isBedReplaceable(boolean disableBeds) {
        if (disableBeds) {
            return "[I]";
        } else {
            return "";
        }
    }
}
