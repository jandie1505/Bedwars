package net.jandie1505.bedwars.game.endlobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.config.JSONLoader;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.utils.LobbyChatListener;
import net.jandie1505.bedwars.game.utils.LobbyProtectionsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
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

import java.io.File;
import java.time.Duration;
import java.util.List;
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

        JSONObject lobbyConfig = JSONLoader.loadJSONFromFile(new File(this.getPlugin().getDataFolder(), "lobby.json"));
        if (lobbyConfig.isEmpty()) lobbyConfig = DefaultConfigValues.getLobbyConfig();

        this.lobbyBorderEnabled = lobbyConfig.optJSONObject("border", new JSONObject()).optBoolean("enable", false);
        this.lobbyBorder = new int[]{
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("x1", -10),
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("y1", -10),
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("z1", -10),
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("x2", 10),
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("y2", 10),
                lobbyConfig.optJSONObject("border", new JSONObject()).optInt("z2", 10)
        };
        this.lobbySpawn = new Location(
                this.getPlugin().getServer().getWorlds().get(0),
                lobbyConfig.optJSONObject("spawnpoint", new JSONObject()).optInt("x", 0),
                lobbyConfig.optJSONObject("spawnpoint", new JSONObject()).optInt("y", 0),
                lobbyConfig.optJSONObject("spawnpoint", new JSONObject()).optInt("z", 0),
                lobbyConfig.optJSONObject("spawnpoint", new JSONObject()).optFloat("yaw", 0.0F),
                lobbyConfig.optJSONObject("spawnpoint", new JSONObject()).optFloat("pitch", 0.0F)
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
                player.sendMessage(Component.empty().append(Component.text("Winner: ", NamedTextColor.GRAY)).append(this.game.getWinner().getFormattedName()));
            } else {
                player.sendMessage(Component.text("No team has won the game", NamedTextColor.GRAY));
            }

            // get player data

            PlayerData playerData = this.game.getPlayerData(player);

            if (playerData == null) {
                continue;
            }

            // stats message

            player.sendMessage("§b§lYour stats§r§7\n Kills: §a" + playerData.getKills() + "§7\n Deaths: §c" + playerData.getDeaths() + "§7\n Beds broken: §6" + playerData.getBedsBroken());

            BedwarsTeam team = this.game.getTeam(playerData.getTeam());

            if (team != null) {
                player.sendMessage(Component.empty().append(Component.text("Your team: ", NamedTextColor.GRAY)).append(team.getFormattedName()));
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

        PlayerData playerData = this.game.getPlayerData(player);

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

            BedwarsTeam winner = this.game.getWinner();
            if (winner != null) {

                if (winner.isMember(player)) {
                    player.showTitle(Title.title(
                            Component.text("VICTORY"),
                            Component.text("Your team has won the game"),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
                    ));
                } else {
                    player.showTitle(Title.title(
                            Component.text("Team " + winner.getName(), winner.getChatColor()),
                            Component.text("has won!", NamedTextColor.GRAY),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
                    ));
                }

            } else {
                player.showTitle(Title.title(
                        Component.text("No team", NamedTextColor.GRAY),
                        Component.text("has won!", NamedTextColor.GRAY),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
                ));
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
