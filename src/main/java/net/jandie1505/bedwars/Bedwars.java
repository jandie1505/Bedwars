package net.jandie1505.bedwars;

import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private World waitingLobby;
    private List<UUID> bypassingPlayers;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.yml");
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public boolean addBypassingPlayer(Player player) {

        if (player == null) {
            return false;
        }

        return this.bypassingPlayers.add(player.getUniqueId());

    }

    public boolean removeBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.remove(playerId);
    }

    public List<UUID> getBypassingPlayers() {
        return List.copyOf(this.bypassingPlayers);
    }

    public void clearBypassingPlayers() {
        this.bypassingPlayers.clear();
    }
}
