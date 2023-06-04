package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorData;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorUpgradeTimeActionData;
import net.jandie1505.bedwars.lobby.setup.LobbyTeamData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

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
    private boolean forcestart;

    public Lobby(Bedwars plugin) {
        this.plugin = plugin;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.map = null;
        this.forcestart = false;
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

        // FORCE START

        if (this.forcestart) {
            return GameStatus.NEXT_STATUS;
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
        World world = this.plugin.loadWorld("map");

        if (world == null) {
            return null;
        }

        Game game = new Game(
                this.plugin,
                world,
                List.of(
                        new LobbyTeamData(
                                "Green",
                                ChatColor.GREEN,
                                List.of(
                                        new Location(world, 55, 1, 0, 0, 0)
                                ),
                                List.of(
                                        new Location(world, 44, 1, 0)
                                ),
                                List.of(
                                        new LobbyGeneratorData(
                                                new Location(world, 63.5, 1, 7.5),
                                                new ItemStack(Material.IRON_INGOT),
                                                List.of(
                                                        0.5
                                                )
                                        ),
                                        new LobbyGeneratorData(
                                                new Location(world, 63.5, 1, -7.5),
                                                new ItemStack(Material.GOLD_INGOT),
                                                List.of(
                                                        8.0
                                                )
                                        )
                                ),
                                List.of(
                                        new Location(world, 55.5, 1, -9.5, 0, 0)
                                ),
                                List.of(
                                        new Location(world, 55.0, 1, 9.5, -180, 0)
                                )
                        ),
                        new LobbyTeamData(
                                "Red",
                                ChatColor.RED,
                                List.of(
                                        new Location(world, -63, 1, 0, 0, 0)
                                ),
                                List.of(
                                        new Location(world, -52, 1, 0, 0, 0)
                                ),
                                List.of(
                                        new LobbyGeneratorData(
                                                new Location(world, -70.5, 1, -6.5),
                                                new ItemStack(Material.IRON_INGOT),
                                                List.of(
                                                        0.5
                                                )
                                        ),
                                        new LobbyGeneratorData(
                                                new Location(world, -70.5, 1, 8.5),
                                                new ItemStack(Material.GOLD_INGOT),
                                                List.of(
                                                        8.0
                                                )
                                        )
                                ),
                                List.of(
                                        new Location(world, -63.5, 1, 9.5, -180, 0)
                                ),
                                List.of(
                                        new Location(world, -63.5, 1, -9.5, 0, 0)
                                )
                        )
                ),
                List.of(
                        new LobbyGeneratorData(
                                new Location(world, -8, 1, 8),
                                new ItemStack(Material.EMERALD),
                                List.of(
                                        40.0
                                )
                        ),
                        new LobbyGeneratorData(
                                new Location(world, -8, 1, -8),
                                new ItemStack(Material.DIAMOND),
                                List.of(
                                        40.0
                                )
                        ),
                        new LobbyGeneratorData(
                                new Location(world, 8, 1, -8),
                                new ItemStack(Material.EMERALD),
                                List.of(
                                        40.0
                                )
                        ),
                        new LobbyGeneratorData(
                                new Location(world, 8, 1, 8),
                                new ItemStack(Material.DIAMOND),
                                List.of(
                                        40.0
                                )
                        )
                ),
                List.of(
                        new LobbyGeneratorUpgradeTimeActionData(1, 1, 3540),
                        new LobbyGeneratorUpgradeTimeActionData(2, 1, 3480)
                ),
                new JSONObject(this.plugin.getShopConfig().getConfig().optJSONObject("itemShop").toString()),
                5,
                3600
        );

        for (UUID playerId : this.getPlayers()) {
            game.addPlayer(playerId, 0);
        }

        return game;
    }

    public void forcestart() {
        this.forcestart = true;
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }
}
