package net.jandie1505.bedwars.game.entities.entities;

import net.chaossquad.mclib.entity.SingleUseManagedEntity;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.base.ManagedEntity;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class EndgameWither extends ManagedEntity<Wither> {
    private final int teamId;
    private int targetTimer;

    public EndgameWither(Game game, Location location, int teamId) {
        super(game, game.getWorld().spawn(location.clone(), Wither.class));
        this.teamId = teamId;
        this.targetTimer = 0;

        BedwarsTeam team = this.getGame().getTeam(this.teamId);
        if (team != null) {
            this.getEntity().setCustomName(team.getData().chatColor() + "§lENDGAME WITHER §r§7(" + team.getData().name() + ")");
        } else {
            this.getEntity().setCustomName("§7§lENDGAME WITHER");
        }

        this.getEntity().addScoreboardTag("bedwars.endgamewither");
        this.getEntity().setCustomNameVisible(true);
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 0, false, false, false));

        this.scheduleRepeatingTask(this::targetSelectionTask, 1, 20, "endgame_wither_target_selection");
        this.scheduleRepeatingTask(this::targetTimerTask, 1, 20, "endgame_wither_target_timer");
    }

    // TASKS

    private void targetSelectionTask() {
        this.selectTarget(Wither.Head.CENTER);
        this.selectTarget(Wither.Head.LEFT);
        this.selectTarget(Wither.Head.RIGHT);
    }

    private void targetTimerTask() {

        if (this.targetTimer > 60) {
            this.getEntity().setTarget(Wither.Head.CENTER, null);
            this.getEntity().setTarget(Wither.Head.LEFT, null);
            this.getEntity().setTarget(Wither.Head.RIGHT, null);
        } else {
            this.targetTimer++;
        }

    }

    // EVENTS

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (event.getEntity() == this.getEntity().getTarget(Wither.Head.CENTER)) {
            this.getEntity().setTarget(Wither.Head.CENTER, null);
            return;
        }

        if (event.getEntity() == this.getEntity().getTarget(Wither.Head.LEFT)) {
            this.getEntity().setTarget(Wither.Head.LEFT, null);
            return;
        }

        if (event.getEntity() == this.getEntity().getTarget(Wither.Head.RIGHT)) {
            this.getEntity().setTarget(Wither.Head.RIGHT, null);
            return;
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() != this.getEntity()) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!this.isValidTarget(damager)) return;

        this.getEntity().setTarget(Wither.Head.CENTER, damager);
    }

    @EventHandler
    public void onEntityTargetSelect(EntityTargetEvent event) {
        if (event.getEntity() != this.getEntity()) return;
        event.setCancelled(true);
    }

    // UTILITIES

    private void selectTarget(Wither.Head head) {
        if (this.getEntity().getTarget(head) != null && this.isValidTarget(this.getEntity().getTarget(head))) return;

        this.getEntity().setTarget(null);

        List<LivingEntity> nearbyEntities = this.getEntity().getNearbyEntities(100, 100, 100).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(this::isValidTarget)
                .toList();

        if (nearbyEntities.isEmpty()) return;

        LivingEntity nextTarget = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));
        this.getEntity().setTarget(nextTarget);

    }

    public boolean isValidTarget(LivingEntity entity) {
        if (entity == null || entity.isDead()) return false;

        if (entity instanceof Player player) {
            PlayerData playerData = this.getGame().getPlayerData(player);
            if (playerData == null) return false;
            if (!playerData.isAlive()) return false;
            return this.teamId < 0 || playerData.getTeam() != this.teamId;
        }

        if (entity instanceof IronGolem ironGolem) {
            ManagedEntity<?> managedEntity = this.getGame().getManagedEntityByEntity(ironGolem);
            if (!(managedEntity instanceof BaseDefender baseDefender)) return false;
            if (baseDefender.toBeRemoved()) return false;
            return this.teamId < 0 || baseDefender.getTeamId() != this.teamId;
        }

        return false;
    }

    // GETTER

    public int getTeamId() {
        return this.teamId;
    }

}
