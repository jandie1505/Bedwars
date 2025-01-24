package net.jandie1505.bedwars.game.timeactions.actions;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DestroyBedsAction extends TimeAction {
    public static final TimeActionData.DataAccessor<Boolean> DISABLE_BEDS = new TimeActionData.DataAccessor<>("disableBeds");
    private final boolean disableBeds;

    public DestroyBedsAction(Game game, TimeActionData data) {
        super(game, data);
        this.disableBeds = Objects.requireNonNullElse(data.getDataField(DISABLE_BEDS), false);
    }

    @Override
    protected void onRun() {
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

    @Override
    public BaseComponent[] getMessage() {
        return new BaseComponent[]{new TextComponent("§cAll beds have been destroyed." + (this.disableBeds ? " §cThey cannot be replaced" : ""))};
    }

    @Override
    public String getScoreboardText() {
        return "Beds gone";
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
