package net.jandie1505.bedwars;

import net.jandie1505.bedwars.commands.BedwarsCommand;
import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.lobby.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private List<UUID> bypassingPlayers;
    private GamePart game;
    private int exceptionCount;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.yml");
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.exceptionCount = 0;

        this.getCommand("bedwars").setExecutor(new BedwarsCommand(this));
        this.getCommand("bedwars").setTabCompleter(new BedwarsCommand(this));

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

            try {

                if (this.game != null) {

                    try {

                        GameStatus gameStatus = this.game.tick();

                        if (gameStatus == GameStatus.NEXT_STATUS) {
                            this.game = this.game.getNextStatus();
                        } else if (gameStatus == GameStatus.ABORT) {
                            this.game = null;
                        }

                    } catch (Exception e) {
                        this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                        this.game = null;
                    }

                }

                if (this.exceptionCount > 0) {
                    this.exceptionCount--;
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.exceptionCount++;
            }

            if (this.exceptionCount >= 3) {
                this.getServer().getPluginManager().disablePlugin(this);
            }

        }, 0, 10);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public boolean addBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.add(playerId);
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

    public boolean isPlayerBypassing(UUID playerId) {
        return this.bypassingPlayers.contains(playerId);
    }

    public void stopGame() {
        this.game = null;
    }

    public void startGame() {
        if (this.game == null) {
            this.game = new Lobby(this);
        }
    }

    public GamePart getGame() {
        return this.game;
    }
}
