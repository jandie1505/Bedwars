package net.jandie1505.bedwars;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventListener implements Listener {
    private final Bedwars plugin;

    public EventListener(Bedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        if (this.plugin.getGame() instanceof Game && event.getEntity() instanceof Player && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getEntity().getUniqueId())) {

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getUniqueId());

            playerData.setAlive(false);

            if (Boolean.FALSE.equals(((Player) event.getEntity()).getPlayer().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    ((Player) event.getEntity()).spigot().respawn();
                }, 1);
            }

        }

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {

            Location location = event.getPlayer().getLocation();

            if (location.getY() < -64) {
                location.setY(-64);
            }

            event.setRespawnLocation(location);

        }

    }

}
