package net.jandie1505.bedwars;

import net.jandie1505.bedwars.commands.BedwarsCommand;
import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.items.ItemStorage;
import net.jandie1505.bedwars.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.*;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private ConfigManager itemConfig;
    private List<UUID> bypassingPlayers;
    private GamePart game;
    private int exceptionCount;
    private List<World> managedWorlds;
    private ItemStorage itemStorage;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.json");
        this.itemConfig = new ConfigManager(this, DefaultConfigValues.getItemConfig(), true, "items.json");
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.exceptionCount = 0;
        this.managedWorlds = Collections.synchronizedList(new ArrayList<>());
        this.itemStorage = new ItemStorage(this);

        this.getCommand("bedwars").setExecutor(new BedwarsCommand(this));
        this.getCommand("bedwars").setTabCompleter(new BedwarsCommand(this));

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

            try {

                // Manage game

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

                // Manage worlds

                for (World world : List.copyOf(this.managedWorlds)) {

                    if (world == null || !this.getServer().getWorlds().contains(world) || this.getServer().getWorlds().get(0) == world) {
                        this.managedWorlds.remove(world);
                        continue;
                    }

                    if (!(this.game instanceof Lobby || this.game instanceof Game) || (this.game instanceof Game && ((Game) this.game).getWorld() != world)) {
                        this.unloadWorld(world);
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

    public void onDisable() {
        this.getLogger().info("Disabling " + this.getName());

        this.stopGame();

        for (World world : List.copyOf(this.managedWorlds)) {
            this.unloadWorld(world);
        }

        this.getLogger().info(this.getName() + " was successfully disabled");
    }

    public World loadWorld(String name) {

        World world = this.getServer().getWorld(name);

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("World [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ") is already loaded and was added to managed worlds");
            return world;
        }

        world = this.getServer().createWorld(new WorldCreator(name));

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("Loaded world [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ")");
        } else {
            this.getLogger().warning("Error while loading world " + name);
        }

        return world;

    }

    public boolean unloadWorld(World world) {

        if (world == null || this.getServer().getWorlds().get(0) == world || !this.managedWorlds.contains(world) || !this.getServer().getWorlds().contains(world)) {
            return false;
        }

        UUID uid = world.getUID();
        int index = this.getServer().getWorlds().indexOf(world);
        String name = world.getName();

        for (Player player : world.getPlayers()) {
            player.teleport(new Location(this.getServer().getWorlds().get(0), 0, 0, 0));
        }

        boolean success = this.getServer().unloadWorld(world, false);

        if (success) {
            this.managedWorlds.remove(world);
            this.getLogger().info("Unloaded world [" + index + "] " + uid + " (" + name + ")");
        } else {
            this.getLogger().warning("Error white unloading world [" + index + "] " + uid + " (" + name + ")");
        }

        return success;

    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ConfigManager getItemConfig() {
        return this.itemConfig;
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

    public static String getDurationFormat(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;

        if (duration.toHours() > 0) {
            formattedTime = String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        } else {
            formattedTime = String.format("%d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
        }

        return formattedTime;
    }
}
