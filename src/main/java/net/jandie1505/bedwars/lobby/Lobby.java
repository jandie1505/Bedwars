package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.map.MapData;
import net.jandie1505.bedwars.game.map.TeamData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lobby implements GamePart {
    private final Bedwars plugin;
    private int timeStep;
    private int time;
    private List<UUID> players;
    private World map;

    public Lobby(Bedwars plugin) {
        this.plugin = plugin;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.map = null;
    }

    @Override
    public GameStatus tick() {

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + " seconds"));

        }

        // ADD PLAYERS

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

            if (!this.plugin.getBypassingPlayers().contains(player.getUniqueId())) {
                this.players.add(player.getUniqueId());
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
        return new Game(
                this.plugin,
                new MapData(
                        this.plugin.getServer().createWorld(new WorldCreator("map")),
                        List.of(),
                        5
                ),
                900
        );
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }
}
