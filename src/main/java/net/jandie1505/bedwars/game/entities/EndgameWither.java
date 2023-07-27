package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EndgameWither {
    private final Game game;
    private final Wither wither;
    private final int teamId;
    private int targetTimer;

    public EndgameWither(Game game, Wither wither, int teamId) {
        this.game = game;
        this.wither = wither;
        this.teamId = teamId;
        this.targetTimer = 0;
    }

    public void tick() {

        // CHECKS

        if (this.wither == null) {
            return;
        }

        if (this.wither.isDead()) {
            return;
        }

        // ENTITY VALUES

        if (!this.wither.getScoreboardTags().contains("endgameWither")) {
            this.wither.addScoreboardTag("endgameWither");
        }

        if (!this.wither.hasPotionEffect(PotionEffectType.SPEED)) {
            this.wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 0, false, false));
        }

        if (!this.wither.isCustomNameVisible()) {
            this.wither.setCustomNameVisible(true);
        }

        // TEAM

        BedwarsTeam team = this.game.getTeam(this.teamId);

        if (team == null) {
            return;
        }

        // NAME

        this.wither.setCustomName(team.getChatColor() + "ENDGAME WITHER (Team " + team.getName() + ")");

        // TARGET

        if (this.isValidTarget(this.wither.getTarget()) && this.targetTimer < 10) {
            this.targetTimer++;
            return;
        }

        this.targetTimer = 0;
        this.wither.setTarget(null);

        int tries = 0;
        LivingEntity possibleTarget = null;

        while (tries < 3) {
            tries++;

            if (new Random().nextInt(3) == 2) {

                List<IronGolem> list = List.copyOf(this.game.getWorld().getEntitiesByClass(IronGolem.class));

                if (list.isEmpty()) {
                    continue;
                }

                possibleTarget = list.get(new Random().nextInt(list.size()));

            } else {

                List<Player> list = List.copyOf(this.game.getWorld().getEntitiesByClass(Player.class));

                if (list.isEmpty()) {
                    continue;
                }

                possibleTarget = list.get(new Random().nextInt(list.size()));

            }

            if (this.isValidTarget(possibleTarget)) {
                break;
            }

        }

        if (this.isValidTarget(possibleTarget)) {
            this.wither.setTarget(possibleTarget);
        }

    }

    public boolean isValidTarget(LivingEntity entity) {

        if (entity == null) {
            return false;
        }

        if (entity.isDead()) {
            return false;
        }

        if (entity instanceof Player) {

            PlayerData playerData = this.game.getPlayers().get(entity.getUniqueId());

            if (playerData == null) {
                return false;
            }

            if (playerData.getTeam() == this.teamId) {
                return false;
            }

            return playerData.isAlive();
        }

        if (entity instanceof IronGolem) {

            BaseDefender baseDefender = this.game.getBaseDefenderByEntity((IronGolem) entity);

            if (baseDefender == null) {
                return false;
            }

            if (baseDefender.canBeRemoved()) {
                return false;
            }

            if (baseDefender.getTeamId() == this.teamId) {
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean canBeRemoved() {
        return this.wither == null || this.wither.isDead();
    }

    public Game getGame() {
        return this.game;
    }

    public Wither getWither() {
        return this.wither;
    }

    public int getTeamId() {
        return this.teamId;
    }
}
