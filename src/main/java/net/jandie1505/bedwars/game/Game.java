package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.map.MapData;
import org.bukkit.World;

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

    public Bedwars getPlugin() {
        return this.plugin;
    }
}
