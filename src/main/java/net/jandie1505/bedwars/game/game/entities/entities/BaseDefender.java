package net.jandie1505.bedwars.game.game.entities.entities;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.base.ExpiringManagedEntity;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class BaseDefender extends ExpiringManagedEntity<IronGolem> {
    private final int teamId;

    public BaseDefender(Game game, Location location, int teamId) {
        super(game, game.getWorld().spawn(location.clone(), IronGolem.class), 300);
        this.teamId = teamId;

        this.getEntity().addScoreboardTag("bedwars.basedefender");
        this.getEntity().setCustomNameVisible(true);
        this.getEntity().getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
        this.getEntity().getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(10);

        this.scheduleRepeatingTask(this::nameTask, 1, 20, "base_defender_name");
        this.scheduleRepeatingTask(this::targetSelectionTask, 1, 20, "base_defender_target_selection");
    }

    // TASKS

    private void nameTask() {
        BedwarsTeam team = this.getGame().getTeam(teamId);

        if (team != null) {
            this.getEntity().customName(Component.empty()
                    .append(Component.text("BASE DEFENDER", team.getChatColor(), TextDecoration.BOLD))
                    .append(Component.text(" --> ", NamedTextColor.GRAY))
                    .append(this.getTargetName())
                    .append(Component.text(" (" + this.getTime() + "s)", NamedTextColor.GRAY))
            );
        } else {
            this.getEntity().customName(Component.empty()
                    .append(Component.text("BASE DEFENDER", NamedTextColor.WHITE, TextDecoration.STRIKETHROUGH))
                    .append(Component.text(" --> ", NamedTextColor.GRAY))
                    .append(this.getTargetName())
                    .append(Component.text(" (" + this.getTime() + "s)", NamedTextColor.GRAY))
            );
        }
    }

    private void targetSelectionTask() {
        if (this.getEntity().getTarget() != null && this.getEntity().getTarget() instanceof Player player && this.isValidTarget(player)) return;

        this.getEntity().setTarget(null);

        List<Player> nearbyPlayers = this.getEntity().getNearbyEntities(15, 15, 15).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .filter(this::isValidTarget)
                .toList();

        if (nearbyPlayers.isEmpty()) return;

        Player nextTarget = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
        this.getEntity().setTarget(nextTarget);
        this.nameTask();
    }

    // EVENTS

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.getEntity().getTarget() != event.getEntity()) return;
        this.getEntity().setTarget(null);
        this.nameTask();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() != this.getEntity()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!this.isValidTarget(player)) return;

        this.getEntity().setTarget(player);
        this.nameTask();
    }

    @EventHandler
    public void onEntityTargetSelect(EntityTargetEvent event) {
        if (event.getEntity() != this.getEntity()) return;
        event.setCancelled(true);
    }

    // UTILITIES

    private Component getTargetName() {
        if (this.getEntity().getTarget() == null) return Component.text("NONE", NamedTextColor.GRAY);
        if (!(this.getEntity().getTarget() instanceof Player player)) return Component.text("UNKNOWN", NamedTextColor.GRAY);

        PlayerData playerData = this.getGame().getPlayerData(player);
        if (playerData == null) return player.displayName().color(NamedTextColor.GRAY);

        BedwarsTeam team = this.getGame().getTeam(playerData.getTeam());
        if (team == null) return player.displayName().color(NamedTextColor.GRAY);

        return player.displayName().color(team.getChatColor());
    }

    /**
     * Returns true if the player is a valid target for the iron golem.
     * @param player player
     * @return true if player is valid target
     */
    private boolean isValidTarget(Player player) {
        if (player == null || !player.isOnline() || player.isDead()) return false;

        PlayerData playerData = this.getGame().getPlayerData(player);
        if (playerData == null) return false;
        if (!playerData.isAlive()) return false;

        return this.teamId < 0 || this.teamId != playerData.getTeam();
    }

    // GETTER

    public int getTeamId() {
        return this.teamId;
    }

}
