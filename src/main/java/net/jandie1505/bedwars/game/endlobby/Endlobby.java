package net.jandie1505.bedwars.game.endlobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.utils.LobbyChatListener;
import net.jandie1505.bedwars.game.utils.LobbyProtectionsListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Endlobby extends GamePart {
    private final boolean lobbyBorderEnabled;
    private final int[] lobbyBorder;
    private final Location lobbySpawn;
    private final Game game;
    private int tick;
    private int time;

    public Endlobby(Bedwars plugin, Game game) {
        super(plugin);

        this.lobbyBorderEnabled = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optBoolean("enable", false);
        this.lobbyBorder = new int[]{
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x2", 10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y2", 10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z2", 10)
        };
        this.lobbySpawn = new Location(
                this.getPlugin().getServer().getWorlds().get(0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("x", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("y", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("z", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("yaw", 0.0F),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("pitch", 0.0F)
        );

        this.game = game;

        for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {

            // teleport

            player.teleport(this.lobbySpawn);

            // continue if game is null

            if (this.game == null) {
                player.sendMessage("§7No team has won");
                continue;
            }

            // winning team message

            if (this.game.getWinner() != null) {
                player.sendMessage("§7Winner: " + this.game.getWinner().getData().chatColor() + this.game.getWinner().getData().name());
            } else {
                player.sendMessage("§7No team has won the game");
            }

            // get player data

            PlayerData playerData = this.game.getPlayers().get(player.getUniqueId());

            if (playerData == null) {
                continue;
            }

            // stats message

            player.sendMessage("§b§lYour stats§r§7\n Kills: §a" + playerData.getKills() + "§7\n Deaths: §c" + playerData.getDeaths() + "§7\n Beds broken: §6" + playerData.getBedsBroken());

            BedwarsTeam team = this.game.getTeam(playerData.getTeam());

            if (team != null) {
                player.sendMessage("§7 Your team: §9" + team.getData().chatColor() + team.getData().name());
            }

            if (this.game.getWinner() != null && this.game.getWinner().getId() == playerData.getTeam()) {
                playerData.setRewardPoints(playerData.getRewardPoints() + this.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("victory", 500));
                this.getPlugin().givePointsToPlayer(player, playerData.getRewardPoints(), "§6Reward for this game: + {points} points");
            }

        }

        this.tick = 0;
        this.time = 30;

        // Listeners

        this.registerListener(new LobbyProtectionsListener(this));
        this.registerListener(new LobbyChatListener(this));
        this.getTaskScheduler().runTaskLater(() -> this.getPlugin().getListenerManager().manageListeners(), 2, "listener_reload_on_start");

        // Tasks

        this.getTaskScheduler().scheduleRepeatingTask(this::endlobbyTask, 1, 1, "endlobby");
    }

    public boolean shouldRun() {
        return true;
    }

    public boolean endlobbyTask() {

        // PLAYERS

        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {

            // Gamemode

            this.gamemode(player);

            // Health

            if (player.getHealthScale() < 20) {
                player.setHealth(20);
            }

            // Saturation

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            // Messages

            if (this.tick >= 20) {
                this.messages(player);
            }

            // Actionbar

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6--- Game end in " + this.time + " seconds ---"));

            // Scoreboard

            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            // lobby location

            this.lobbyLocation(player);
            this.inventory(player);

        }

        // TIME

        if (this.tick >= 20) {
            if (this.time < 0) {
                this.getPlugin().nextStatus();
                return true;
            } else {
                this.time--;
            }
        }

        // TICK

        if (this.tick >= 20) {
            this.tick = 0;
        } else {
            this.tick++;
        }

        // RETURN

        return true;
    }

    private void gamemode(Player player) {

        if (player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }

        if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
            return;
        }

        player.setGameMode(GameMode.ADVENTURE);

    }

    private void messages(Player player) {

        if (this.game == null) {
            player.sendTitle("", "§7No team has won", 0, 25, 0);
            return;
        }

        PlayerData playerData = this.game.getPlayers().get(player.getUniqueId());

        if (this.time >= 0 && this.time <= 15) {

            if (playerData != null) {

                player.sendTitle(
                        "§a" + playerData.getKills() + " §7|§c " + playerData.getDeaths() + " §7|§6 " + playerData.getBedsBroken(),
                        "§7Your stats: §aK §7|§c D §7|§6 BEDS",
                        0,
                        25,
                        0);

            }

            return;
        }

        if (this.time >= 16 && this.time <= 30) {

            if (this.game.getWinner() != null) {

                if (this.game.getWinner().getPlayers().contains(player.getUniqueId())) {
                    player.sendTitle("§6§lVICTORY", "§7§lYour team has won the game", 0, 25, 0);
                } else {
                    player.sendTitle(this.game.getWinner().getData().chatColor() + "Team " + this.game.getWinner().getData().name(), "§7has won", 0, 25, 0);
                }

            } else {
                player.sendTitle("", "§7§lNo team has won", 0, 25, 0);
            }

            return;
        }

    }

    private void lobbyLocation(Player player) {

        if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
            return;
        }

        if (!this.lobbyBorderEnabled) {
            return;
        }

        Location location = player.getLocation();

        if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
            player.teleport(this.lobbySpawn);
        }

    }

    private void inventory(Player player) {

        if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
            return;
        }

        player.getInventory().clear();

    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }

    // ----- PLAYERS -----

    /**
     * Returns a set of players currently registered.
     * @return set of players
     */
    public @NotNull Set<UUID> getRegisteredPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    /**
     * Returns true if the player with the specified uuid is ingame (in the lobby).
     * @param playerId player uuid
     * @return ingame
     */
    public boolean isPlayerIngame(@Nullable UUID playerId) {
        return playerId != null;
    }

    // ----- NEXT STATUS -----

    @Override
    public GamePart getNextStatus() {

        if (this.getPlugin().isCloudSystemMode()) {
            this.getPlugin().getLogger().info("Cloudsystem mode enabled: Shutting down server");
            this.getPlugin().getServer().shutdown();
        }

        return null;
    }
}
