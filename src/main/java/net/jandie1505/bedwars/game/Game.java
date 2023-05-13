package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.map.MapData;
import net.jandie1505.bedwars.game.map.TeamData;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game implements GamePart {
    private final Bedwars plugin;
    private final MapData mapData;
    private final Map<UUID, PlayerData> players;
    private int maxTime;
    private int timeStep;
    private int time;

    public Game(Bedwars plugin, MapData mapData, int maxTime) {
        this.plugin = plugin;
        this.mapData = mapData;
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.maxTime = maxTime;
        this.time = this.maxTime;
    }

    @Override
    public GameStatus tick() {

        // STOP IF WORLD NOT LOADED

        if (this.mapData.getWorld() == null || !this.plugin.getServer().getWorlds().contains(this.mapData.getWorld())) {
            this.plugin.getLogger().warning("Bedwars game end because world is not loaded");
            return GameStatus.ABORT;
        }

        // PLAYER MANAGEMENT

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {

            if (!this.players.containsKey(player.getUniqueId())) {
                continue;
            }

            PlayerData playerData = this.players.get(player.getUniqueId());

            // Check player for invalid values

            TeamData teamData = null;
            try {
                teamData = this.mapData.getTeams().get(playerData.getTeam());
            } catch (IndexOutOfBoundsException ignored) {
                // Player is getting removed when exception is thrown because teamData will then be null
            }

            if (teamData == null) {
                this.players.remove(player.getUniqueId());
                continue;
            }

            // Player respawn

            if (playerData.isAlive()) {

                if (playerData.getRespawnCountdown() != this.mapData.getRespawnCountdown()) {
                    playerData.setRespawnCountdown(this.mapData.getRespawnCountdown());
                }

                if (!this.plugin.isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

            } else {

                if (!this.plugin.isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (this.timeStep >= 1) {
                    if (playerData.getRespawnCountdown() > 0) {

                        player.sendTitle("§c§lDEAD", "§7§lYou will respawn in " + playerData.getRespawnCountdown() + " seconds", 0, 20, 0);
                        player.sendMessage("§7Respawn in " + playerData.getRespawnCountdown() + " seconds");

                        playerData.setRespawnCountdown(playerData.getRespawnCountdown() - 1);

                    } else {

                        this.respawnPlayer(player);

                    }
                }

            }
        }

        // TIME

        if (this.timeStep >= 1) {
            if (this.time > 0) {
                this.time--;
            } else {
                return GameStatus.NEXT_STATUS;
            }
        }

        // TIME STEP

        if (this.timeStep >= 1) {
            this.timeStep = 0;
        } else {
            this.timeStep = 1;
        }

        return GameStatus.NORMAL;
    }

    @Override
    public GamePart getNextStatus() {
        return null;
    }

    public boolean respawnPlayer(Player player) {

        if (player == null || !this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        PlayerData playerData = this.players.get(player.getUniqueId());

        playerData.setAlive(true);
        player.teleport(this.mapData.getTeams().get(playerData.getTeam()).getRandomSpawnpoint());
        player.resetTitle();

        player.sendMessage("§bRespawning...");

        return true;
    }

    public boolean addPlayer(UUID playerId, int team) {
        return this.players.put(playerId, new PlayerData(team)) != null;
    }

    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public MapData getMapData() {
        return this.mapData;
    }

    public Map<UUID, PlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }
}
