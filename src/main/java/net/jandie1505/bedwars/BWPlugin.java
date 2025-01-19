package net.jandie1505.bedwars;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class BWPlugin extends JavaPlugin {
    @Nullable
    private Bedwars plugin;

    public BWPlugin() {
        this.plugin = null;
    }

    @Override
    public void onEnable() {
        this.plugin = new Bedwars(this);
    }

    @Override
    public void onDisable() {

        try {
            if (this.plugin != null) this.plugin.onDisable();
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to call onDisable in Bedwars", e);
        }

        this.plugin = null;
    }
}
