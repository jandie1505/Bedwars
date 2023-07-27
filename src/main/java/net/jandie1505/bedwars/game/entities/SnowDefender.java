package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SnowDefender {
    private final Game game;
    private final Snowman snowman;
    private final int teamId;
    private int lifetime;
    private int targetTimer;

    public SnowDefender(Game game, Snowman snowman, int teamId) {
        this.game = game;
        this.snowman = snowman;
        this.teamId = teamId;
        this.lifetime = 300;
    }

    public void tick() {

        // CHECKS

        if (this.snowman == null) {
            return;
        }

        if (this.snowman.isDead()) {
            return;
        }

        // LIFETIME

        if (this.lifetime > 0) {
            this.lifetime--;
        } else {
            this.snowman.remove();
            return;
        }

        // ENTITY VALUES

        if (!this.snowman.getScoreboardTags().contains("snowdefender")) {
            this.snowman.addScoreboardTag("snowdefender");
        }

        if (!this.snowman.hasPotionEffect(PotionEffectType.SPEED)) {
            this.snowman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 0, false, false));
        }

        if (!this.snowman.hasPotionEffect(PotionEffectType.REGENERATION)) {
            this.snowman.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3600*20, 0, false, false));
        }

        if (this.snowman.getMaxHealth() < 10) {
            this.snowman.setMaxHealth(10);
        }

        if (!this.snowman.isCustomNameVisible()) {
            this.snowman.setCustomNameVisible(true);
        }

        // TEAM

        BedwarsTeam team = this.game.getTeam(this.teamId);

        if (team == null) {
            this.snowman.remove();
            return;
        }

        // NAME

        this.snowman.setCustomName(team.getChatColor() + "SNOW DEFENDER" + this.getTargetName() + " (" + this.lifetime + ")");

        // TARGET

        if (this.isValidTarget(this.snowman.getTarget()) && this.targetTimer < 10) {
            this.targetTimer++;
            return;
        }

        this.targetTimer = 0;
        this.snowman.setTarget(null);

        List<Entity> nearbyEntities = List.copyOf(this.snowman.getWorld().getNearbyEntities(this.snowman.getLocation(), 15, 15, 15));

        if (nearbyEntities.isEmpty()) {
            return;
        }

        for (Entity entity : nearbyEntities) {

            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            if (this.isValidTarget((LivingEntity) entity)) {
                this.snowman.setTarget((LivingEntity) entity);
                break;
            }

        }

    }

    private boolean isValidTarget(LivingEntity entity) {

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

        if (entity instanceof Wither) {
            EndgameWither endgameWither = this.game.getEndgameWitherByEntity((Wither) entity);

            if (endgameWither == null) {
                return false;
            }

            if (endgameWither.canBeRemoved()) {
                return false;
            }

            if (endgameWither.getTeamId() == this.teamId) {
                return false;
            }

            return true;
        }

        return false;
    }

    private String getTargetName() {

        if (this.snowman.getTarget() == null) {
            return "";
        }

        if (this.snowman.getTarget() instanceof Player) {
            PlayerData playerData = this.game.getPlayers().get(this.snowman.getTarget().getUniqueId());

            if (playerData == null) {
                return " §7--> " + ((Player) this.snowman.getTarget()).getDisplayName();
            }

            BedwarsTeam team = this.game.getTeam(playerData.getTeam());

            if (team == null) {
                return " §7--> " + ((Player) this.snowman.getTarget()).getDisplayName();
            }

            return " §7--> " + team.getChatColor() + ((Player) this.snowman.getTarget()).getDisplayName();
        }

        if (this.snowman.getTarget() instanceof IronGolem) {
            BaseDefender baseDefender = this.game.getBaseDefenderByEntity((IronGolem) this.snowman.getTarget());

            if (baseDefender == null) {
                return " §7--> ?";
            }

            BedwarsTeam team = this.game.getTeam(baseDefender.getTeamId());

            if (team == null) {
                return " §7--> BASE DEFENDER";
            }

            return " §7--> " + team.getChatColor() + "BASE DEFENDER";
        }

        if (this.snowman.getTarget() instanceof Wither) {
            EndgameWither endgameWither = this.game.getEndgameWitherByEntity((Wither) this.snowman.getTarget());

            if (endgameWither == null) {
                return " §7--> ?";
            }

            BedwarsTeam team = this.game.getTeam(endgameWither.getTeamId());

            if (team == null) {
                return " §7--> ENDGAME WITHER";
            }

            return " §7--> " + team.getChatColor() + "ENDGAME WITHER";
        }

        return " §7--> ?";
    }

    public boolean canBeRemoved() {
        return this.snowman == null || this.snowman.isDead();
    }

    public Game getGame() {
        return game;
    }

    public Golem getGolem() {
        return snowman;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getLifetime() {
        return lifetime;
    }
}
