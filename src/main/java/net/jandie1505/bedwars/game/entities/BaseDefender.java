package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Location;
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
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600*20, 0, false, false, false));
        this.getEntity().setCustomNameVisible(true);

        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::nameTask, 1, 20, this::toBeRemoved, "base_defender_name");
        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::targetSelectionTask, 1, 20, this::toBeRemoved, "base_defender_target_selection");
    }

    // TASKS

    private void nameTask() {
        BedwarsTeam team = this.getGame().getTeam(teamId);

        if (team != null) {
            this.getEntity().setCustomName(team.getChatColor() + "§lBASE DEFENDER §7--> " + this.getTargetName() + " §7§l(" + this.getTime() + ")");
        } else {
            this.getEntity().setCustomName("§lBASE DEFENDER §7--> " + this.getTargetName() + " §7§l(" + this.getTime() + ")");
        }
    }

    private void targetSelectionTask() {
        if (this.getEntity().getTarget() != null && this.getEntity().getTarget() instanceof Player player && this.isValidTarget(player)) return;
        System.out.println("switching target from " + this.getEntity().getTarget());

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

    private String getTargetName() {
        if (this.getEntity().getTarget() == null) return "§7NONE";
        if (!(this.getEntity().getTarget() instanceof Player player)) return "§7UNKNOWN";

        PlayerData playerData = this.getGame().getPlayers().get(player.getUniqueId());
        if (playerData == null) return "§7" + player.getDisplayName();

        BedwarsTeam team = this.getGame().getTeam(playerData.getTeam());
        if (team == null) return "§7" + player.getDisplayName();

        return team.getChatColor() + player.getDisplayName();
    }

    /**
     * Returns true if the player is a valid target for the iron golem.
     * @param player player
     * @return true if player is valid target
     */
    private boolean isValidTarget(Player player) {
        if (player == null || !player.isOnline() || player.isDead()) return false;

        PlayerData playerData = this.getGame().getPlayer(player.getUniqueId());
        if (playerData == null) return false;
        if (!playerData.isAlive()) return false;

        return this.teamId < 0 || this.teamId != playerData.getTeam();
    }

    // GETTER

    public int getTeamId() {
        return this.teamId;
    }

}
