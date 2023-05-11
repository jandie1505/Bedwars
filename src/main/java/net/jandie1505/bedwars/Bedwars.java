package net.jandie1505.bedwars;

import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private World waitingLobby;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.yml");
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}
