package net.jandie1505.bedwars.game.map;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapData {
    private final World world;
    private final List<TeamData> teams;
    private final List<SpawnpointData> spawnpoints;

    public MapData(World world) {
        this.world = world;
        this.teams = Collections.synchronizedList(new ArrayList<>());
        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
    }
}
