package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.map.BedwarsTeam;
import net.jandie1505.bedwars.lobby.map.LobbyMapData;
import net.jandie1505.bedwars.lobby.map.LobbyTeamData;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Game implements GamePart {
    private final Bedwars plugin;
    private final World world;
    private final LobbyMapData lobbyMapData;
    private final List<BedwarsTeam> teams;
    private final Map<UUID, PlayerData> players;
    private int maxTime;
    private int timeStep;
    private int time;

    public Game(Bedwars plugin, World world, LobbyMapData lobbyMapData, List<LobbyTeamData> teams, int maxTime) {
        this.plugin = plugin;
        this.world = world;
        this.lobbyMapData = lobbyMapData;
        this.teams = Collections.synchronizedList(new ArrayList<>());

        for (LobbyTeamData teamData : List.copyOf(teams)) {
            this.teams.add(new BedwarsTeam(this, teamData));
        }

        this.players = Collections.synchronizedMap(new HashMap<>());
        this.maxTime = maxTime;
        this.time = this.maxTime;
    }

    @Override
    public GameStatus tick() {

        // STOP IF WORLD NOT LOADED

        if (this.world == null || !this.plugin.getServer().getWorlds().contains(this.world)) {
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

            BedwarsTeam team = null;
            try {
                team = this.teams.get(playerData.getTeam());
            } catch (IndexOutOfBoundsException ignored) {
                // Player is getting removed when exception is thrown because teamData will then be null
            }

            if (team == null) {
                this.players.remove(player.getUniqueId());
                continue;
            }

            // Player respawn

            if (playerData.isAlive()) {

                if (playerData.getRespawnCountdown() != this.lobbyMapData.getRespawnCountdown()) {
                    playerData.setRespawnCountdown(this.lobbyMapData.getRespawnCountdown());
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

            // Scoreboard

            Scoreboard scoreboard = playerData.getScoreboard();

            for (String name : List.copyOf(scoreboard.getEntries())) {
                scoreboard.resetScores(name);
            }

            if (scoreboard.getObjective("sidebardisplay") == null) {
                scoreboard.registerNewObjective("sidebardisplay", Criteria.DUMMY, "§6§lBEDWARS");
            }

            Objective sidebardisplay = scoreboard.getObjective("sidebardisplay");
            List<String> sidebarDisplayStrings = new ArrayList<>();

            sidebarDisplayStrings.add("");

            for (BedwarsTeam iTeam : this.getTeams()) {

                String teamStatusIndicator = "";

                if (iTeam.isAlive()) {
                    if (iTeam.hasBed() > 1) {
                        teamStatusIndicator = "§a" + iTeam.hasBed() + "\u2713";
                    } else if (iTeam.hasBed() == 1) {
                        teamStatusIndicator = "§a\u2713";
                    } else {
                        teamStatusIndicator = "§6" + iTeam.getPlayers().size();
                    }
                } else {
                    teamStatusIndicator = "§c\u274C";
                }

                 if (iTeam == team) {
                     teamStatusIndicator = teamStatusIndicator + " §7(you)";
                 }

                sidebarDisplayStrings.add(team.getColor() + team.getName() + ": §r" + teamStatusIndicator);

            }

            sidebarDisplayStrings.add("");

            sidebarDisplayStrings.add("Kills: §a" + playerData.getKills());
            sidebarDisplayStrings.add("Beds broken: §a" + playerData.getBedsBroken());
            sidebarDisplayStrings.add("Deaths: §a" + playerData.getDeaths());

            sidebarDisplayStrings.add("");

            int reverseIsidebar = sidebarDisplayStrings.size();
            for (String sidebarEntry : sidebarDisplayStrings) {

                if (sidebarEntry.equalsIgnoreCase("")) {
                    String paragraphs = "§";
                    for (int i = 0; i < reverseIsidebar; i++) {
                        paragraphs = paragraphs + "§";
                    }
                    sidebardisplay.getScore(paragraphs).setScore(reverseIsidebar);
                } else {
                    sidebardisplay.getScore(sidebarEntry).setScore(reverseIsidebar);
                }

                reverseIsidebar--;
            }

            if (sidebardisplay.getDisplaySlot() != DisplaySlot.SIDEBAR) {
                sidebardisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            if (player.getScoreboard() != scoreboard) {
                player.setScoreboard(scoreboard);
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
        player.teleport(this.teams.get(playerData.getTeam()).getRandomSpawnpoint());
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

    public LobbyMapData getMapData() {
        return this.lobbyMapData;
    }

    public Map<UUID, PlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }

    public World getWorld() {
        return this.world;
    }

    public List<BedwarsTeam> getTeams() {
        return List.copyOf(this.teams);
    }
}
