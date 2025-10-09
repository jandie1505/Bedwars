package net.jandie1505.bedwars.game.game.entities.entities;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.base.ExpiringManagedEntity;
import net.jandie1505.bedwars.game.game.entities.base.ManagedEntity;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class SnowDefender extends ExpiringManagedEntity<Snowman> {
    private final int teamId;

    public SnowDefender(Game game, Location location, int teamId) {
        super(game, game.getWorld().spawn(location.clone(), Snowman.class), 300);
        this.teamId = teamId;

        this.getEntity().addScoreboardTag("bedwars.snowdefender");
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 1, false, false, false));
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3600*20, 0, false, false, false));
        this.getEntity().setCustomNameVisible(true);

        AttributeInstance maxHealth = this.getEntity().getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) maxHealth.setBaseValue(20.0);

        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::nameTask, 1, 20, this::toBeRemoved, "snow_defender_name");
        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::entityTargetUpdateTask, 1, 20, this::toBeRemoved, "snow_defender_target_update");
        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::targetTimerTask, 1, 15*20, this::toBeRemoved, "snow_defender_target_timer");
    }

    // TASKS

    private void nameTask() {
        BedwarsTeam team = this.getGame().getTeam(this.teamId);
        if (team != null) {
            this.getEntity().customName(Component.empty()
                    .append(Component.text("SNOW DEFENDER", team.getChatColor()))
                    .append(Component.text(" (" + this.getTime() + "s)", NamedTextColor.GRAY))
            );
        } else {
            this.getEntity().customName(Component.empty()
                    .append(Component.text("SNOW DEFENDER", NamedTextColor.WHITE, TextDecoration.STRIKETHROUGH))
                    .append(Component.text(" (" + this.getTime() + "s)", NamedTextColor.GRAY))
            );
        }
    }

    private void entityTargetUpdateTask() {
        if (this.getEntity().getTarget() != null && this.isValidTarget(this.getEntity().getTarget())) return;

        this.getEntity().setTarget(null);

        List<LivingEntity> nearbyEntities = this.getEntity().getNearbyEntities(30, 30, 30).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(this::isValidTarget)
                .toList();

        if (nearbyEntities.isEmpty()) return;

        LivingEntity nextTarget = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));
        this.getEntity().setTarget(nextTarget);
        this.nameTask();

    }

    private void targetTimerTask() {
        this.getEntity().setTarget(null);
    }

    // EVENTS

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.getEntity().getTarget() != event.getEntity()) return;
        this.getEntity().setTarget(null);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() != this.getEntity()) return;
        if (!(event.getDamager() instanceof LivingEntity livingEntity)) return;
        if (!this.isValidTarget(livingEntity)) return;

        this.getEntity().setTarget(livingEntity);
    }

    @EventHandler
    public void onEntityTargetSelect(EntityTargetEvent event) {
        if (event.getEntity() != this.getEntity()) return;
        event.setCancelled(true);
    }

    // UTILITIES

    /**
     * Returns if the specified entity is a valid target for the snow defender.
     * @param entity entity
     * @return true if entity is valid target
     */
    private boolean isValidTarget(LivingEntity entity) {
        if (entity == null) return false;
        if (entity.isDead()) return false;

        // Check if entity is player of other team
        if (entity instanceof Player player) {
            PlayerData playerData = this.getGame().getPlayerData(player);
            if (playerData == null) return false;
            if (!playerData.isAlive()) return false;
            return this.teamId < 0 || playerData.getTeam() != this.teamId;
        }

        // Check if entity is base defender of other team
        if (entity instanceof IronGolem ironGolem) {
            ManagedEntity<?> managedEntity = this.getGame().getManagedEntityByEntity(ironGolem);
            if (!(managedEntity instanceof BaseDefender baseDefender)) return false;
            if (baseDefender.toBeRemoved()) return false;
            return baseDefender.getTeamId() < 0 || baseDefender.getTeamId() != this.teamId;
        }

        // Check if entity is endgame wither of other team
        if (entity instanceof Wither wither) {
            ManagedEntity<?> managedEntity = this.getGame().getManagedEntityByEntity(wither);
            if (!(managedEntity instanceof EndgameWither endgameWither)) return false;
            if (endgameWither.toBeRemoved()) return false;
            return endgameWither.getTeamId() < 0 || endgameWither.getTeamId() != this.teamId;
        }

        return false;
    }

    // GETTER

    public int getTeamId() {
        return teamId;
    }

}
