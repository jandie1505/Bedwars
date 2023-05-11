package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
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

    public int tick() {

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            PlayerData playerData = this.players.get(playerId);

            // Check player for invalid values

            TeamData teamData = null;
            try {
                teamData = this.mapData.getTeams().get(playerData.getTeam());
            } catch (IndexOutOfBoundsException ignored) {
                // Player is getting removed when exception is thrown
            }

            if (teamData == null) {
                this.players.remove(playerId);
                continue;
            }

            // Player respawn

            if (playerData.isAlive()) {

                if (playerData.getRespawnCountdown() != this.mapData.getRespawnCountdown()) {
                    playerData.setRespawnCountdown(this.mapData.getRespawnCountdown());
                }

                if (player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

            } else {

                player.sendTitle("§c§lDEAD", "§7§lYou will respawn in " + playerData.getRespawnCountdown() + " seconds", 0, 20, 0);
                player.sendMessage("§7Respawn in " + playerData.getRespawnCountdown() + " seconds");

                if (player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (this.timeStep >= 1) {
                    if (playerData.getRespawnCountdown() > 0) {
                        playerData.setRespawnCountdown(playerData.getRespawnCountdown() - 1);
                    } else {
                        this.respawnPlayer(player);
                    }
                }

            }
        }

        // TIME

        if (this.timeStep >= 1) {
            this.time--;
        }

        // TIME STEP

        if (this.timeStep >= 1) {
            this.timeStep = 0;
        } else {
            this.timeStep = 1;
        }

        return 0;
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

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public Map<UUID, PlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }
}
