package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseDefender {
    private final Game game;
    private final IronGolem ironGolem;
    private final int teamId;
    private int lifetime;

    public BaseDefender(Game game, IronGolem ironGolem, int teamId) {
        this.game = game;
        this.ironGolem = ironGolem;
        this.teamId = teamId;
        this.lifetime = 300;
    }

    public void tick() {

        // CHECKS

        if (this.ironGolem == null) {
            return;
        }

        if (this.ironGolem.isDead()) {
            return;
        }

        // LIFETIME

        if (this.lifetime > 0) {
            this.lifetime--;
        } else {
            this.ironGolem.remove();
            return;
        }

        // ENTITY VALUES

        if (!this.ironGolem.getScoreboardTags().contains("basedefender")) {
            this.ironGolem.addScoreboardTag("basedefender");
        }

        if (!this.ironGolem.hasPotionEffect(PotionEffectType.SPEED)) {
            this.ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 0, false, false));
        }

        if (!this.ironGolem.isCustomNameVisible()) {
            this.ironGolem.setCustomNameVisible(true);
        }

        // TEAM

        BedwarsTeam team = this.game.getTeam(teamId);

        if (team == null) {
            return;
        }

        // NAME

        this.ironGolem.setCustomName(team.getChatColor() + "§lBASE DEFENDER §7--> " + this.getTargetName() + " §7§l(" + this.lifetime + ")");

        // TARGET

        if (this.ironGolem.getTarget() != null) {

            if (this.ironGolem.getTarget() instanceof Player) {
                PlayerData playerData = this.game.getPlayers().get(this.ironGolem.getTarget().getUniqueId());

                if (((Player) this.ironGolem.getTarget()).isOnline() && playerData != null && playerData.isAlive()) {
                    return;
                }
            }

            this.ironGolem.setTarget(null);
        }

        List<Entity> nearbyEntities = new ArrayList<>(this.ironGolem.getNearbyEntities(15, 15, 15));

        for (Entity entity : List.copyOf(nearbyEntities)) {

            if (!(entity instanceof Player)) {
                nearbyEntities.remove(entity);
                continue;
            }

            Player player = (Player) entity;

            PlayerData playerData = this.game.getPlayers().get(player.getUniqueId());

            if (playerData == null) {
                nearbyEntities.remove(entity);
                continue;
            }

            if (!playerData.isAlive()) {
                nearbyEntities.remove(entity);
                continue;
            }

            if (playerData.getTeam() == this.teamId) {
                nearbyEntities.remove(entity);
                continue;
            }

        }

        if (nearbyEntities.isEmpty()) {
            return;
        }

        if (nearbyEntities.contains(this.ironGolem.getTarget())) {
            return;
        }

        Entity randomEntity = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));

        if (!(randomEntity instanceof Player)) {
            return;
        }

        this.ironGolem.setTarget((Player) randomEntity);

    }

    private String getTargetName() {

        if (this.ironGolem.getTarget() == null) {
            return "§7NONE";
        }

        if (!(this.ironGolem.getTarget() instanceof Player)) {
            return "§7" + this.ironGolem.getTarget().getName();
        }

        PlayerData playerData = this.game.getPlayers().get(this.ironGolem.getTarget().getUniqueId());

        if (playerData == null) {
            return "§7" + ((Player) this.ironGolem.getTarget()).getDisplayName();
        }

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());

        if (team == null) {
            return "§7" + ((Player) this.ironGolem.getTarget()).getDisplayName();
        }

        return team.getChatColor() + ((Player) this.ironGolem.getTarget()).getDisplayName();

    }

    public boolean canBeRemoved() {
        return this.game == null || this.ironGolem == null || this.ironGolem.isDead();
    }

    public Game getGame() {
        return game;
    }

    public IronGolem getIronGolem() {
        return ironGolem;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
