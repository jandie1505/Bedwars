package net.jandie1505.bedwars.game.map;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.lobby.map.LobbyTeamData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;

import java.util.*;

public class BedwarsTeam {
    private final Game game;
    private final String name;
    private final ChatColor color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;

    public BedwarsTeam(Game game, LobbyTeamData teamData) {
        this.game = game;
        this.name = teamData.getName();
        this.color = teamData.getColor();
        this.spawnpoints = Collections.synchronizedList(new ArrayList<>(teamData.getSpawnpoints()));
        this.bedLocations = Collections.synchronizedList(new ArrayList<>(teamData.getBedLocations()));
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public List<Location> getSpawnpoints() {
        return List.copyOf(this.spawnpoints);
    }

    public Location getRandomSpawnpoint() {

        if (this.spawnpoints.isEmpty()) {
            return null;
        }

        return this.spawnpoints.get(new Random().nextInt(this.spawnpoints.size()));
    }

    public List<Location> getBedLocations() {
        return List.copyOf(this.bedLocations);
    }

    public int getId() {
        return this.game.getTeams().indexOf(this);
    }

    public List<UUID> getPlayers() {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : this.game.getPlayers().keySet()) {
            PlayerData playerData = this.game.getPlayers().get(playerId);

            if (playerData.getTeam() == this.getId()) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    public int hasBed() {
        int beds = 0;

        for (Location bedLocation : this.getBedLocations()) {

            if (bedLocation.getBlock().getBlockData() instanceof Bed) {
                beds++;
            }

        }

        return beds;
    }

    public boolean isAlive() {
        return this.getPlayers().size() > 0;
    }

}
