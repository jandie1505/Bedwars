package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.ArmorConfig;
import net.jandie1505.bedwars.lobby.setup.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Lobby implements GamePart {
    private final Bedwars plugin;
    private int timeStep;
    private int time;
    private Map<UUID, LobbyPlayerData> players;
    private boolean forcestart;
    private List<MapData> maps;
    private MapData selectedMap;
    private boolean mapVoting;

    public Lobby(Bedwars plugin) {
        this.plugin = plugin;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.forcestart = false;
        this.maps = new ArrayList<>();
        this.selectedMap = null;

        JSONArray mapArray = this.plugin.getMapConfig().getConfig().optJSONArray("maps");

        if (mapArray == null) {
            mapArray = new JSONArray();
        }

        int index = -1;
        for (Object object : mapArray) {
            index++;

            if (!(object instanceof JSONObject)) {
                this.plugin.getLogger().warning("Map Config: Index " + index + " is not a json object");
                continue;
            }

            JSONObject map = (JSONObject) object;

            String name = map.optString("name");

            if (name == null) {
                this.plugin.getLogger().warning("Map Config: Missing name of map with index " + index);
                continue;
            }

            String world = map.optString("world");

            if (world == null) {
                this.logMissingMapConfigItem("world", index, name);
                continue;
            }

            int respawnCooldown = map.optInt("respawnCooldown", -1);

            if (respawnCooldown < 0) {
                this.logMissingMapConfigItem("respawnCooldown", index, name);
                continue;
            }

            int maxTime = map.optInt("maxTime", -1);

            if (maxTime < 0) {
                this.logMissingMapConfigItem("maxTime", index, name);
                continue;
            }

            List<LobbyTeamData> teams = new ArrayList<>();
            JSONArray teamsArray = map.optJSONArray("teams");

            if (teamsArray == null) {
                this.logMissingMapConfigItem("teams", index, name);
                continue;
            }

            for (Object object2 : teamsArray) {

                if (!(object2 instanceof JSONObject)) {
                    this.plugin.getLogger().warning("Map Config: A team of map " + name + " (" + index + ") is not a json object");
                    continue;
                }

                JSONObject team = (JSONObject) object2;

                String teamName = team.optString("name");

                if (teamName == null) {
                    this.plugin.getLogger().warning("Map Config: Missing name of a team of " + name + " (" + index + ")");
                    continue;
                }

                Color teamColor;

                try {
                    teamColor = Color.fromRGB(team.optInt("color", -1));
                } catch (IllegalArgumentException e) {
                    this.plugin.getLogger().warning("Map Config: Missing/Wrong color of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                String teamChatColorString = team.optString("chatColor");

                if (teamChatColorString == null) {
                    this.plugin.getLogger().warning("Map Config: Missing chatColor of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                ChatColor teamChatColor = ChatColor.valueOf(teamChatColorString);

                if (teamChatColor == null) {
                    this.plugin.getLogger().warning("Map Config: Wrong chatColor of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                JSONArray teamSpawnpointArray = team.optJSONArray("spawnpoints");

                if (teamSpawnpointArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing spawnpoints of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> teamSpawnpoints = this.buildLocationList(teamSpawnpointArray, true);

                JSONArray bedLocationsArray = team.optJSONArray("bedLocations");

                if (bedLocationsArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing spawnpoints of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> teamBedLocations = this.buildLocationList(bedLocationsArray, false);

                JSONArray teamGeneratorsArray = team.optJSONArray("generators");

                if (teamGeneratorsArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing teamGenerators of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<LobbyGeneratorData> teamGenerators = this.buildGeneratorList(teamGeneratorsArray);

                JSONArray shopVillagerLocationArray = team.optJSONArray("shopVillagers");

                if (shopVillagerLocationArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing shopVillagers of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> shopVillagerLocations = this.buildLocationList(shopVillagerLocationArray, false);

                JSONArray upgradeVillagerLocationArray = team.optJSONArray("upgradeVillagers");

                if (upgradeVillagerLocationArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing upgradeVillagers of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> upgradeVillagerLocations = this.buildLocationList(upgradeVillagerLocationArray, false);

                teams.add(new LobbyTeamData(teamName, teamChatColor, teamColor, teamSpawnpoints, teamBedLocations, teamGenerators, shopVillagerLocations, upgradeVillagerLocations));
            }

            JSONArray globalGeneratorArray = map.optJSONArray("globalGenerators");

            if (globalGeneratorArray == null) {
                this.logMissingMapConfigItem("teams", index, name);
                continue;
            }

            List<LobbyGeneratorData> globalGenerators = this.buildGeneratorList(globalGeneratorArray);

            List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions = new ArrayList<>();
            JSONArray timeActionArray = map.optJSONArray("timeActions");

            if (timeActionArray == null) {
                this.logMissingMapConfigItem("timeActions", index, name);
                continue;
            }

            for (Object object2 : timeActionArray) {

                if (!(object2 instanceof JSONObject)) {
                    this.plugin.getLogger().warning("Map Config: A timeAction of map " + name + " (" + index + ") is not a json object");
                    continue;
                }

                JSONObject timeActionData = (JSONObject) object2;

                String type = timeActionData.optString("type");

                if (type == null) {
                    this.plugin.getLogger().warning("Map Config: Missing type of a timeAction of " + name + " (" + index + ")");
                    continue;
                }

                int time = timeActionData.optInt("time", -1);

                if (time < 0) {
                    this.plugin.getLogger().warning("Map Config: Missing time of a timeAction of " + name + " (" + index + ")");
                    continue;
                }

                switch (type) {
                    case "GENERATOR_UPGRADE":

                        int generatorLevel = timeActionData.optInt("generatorLevel", -1);

                        if (generatorLevel < 0) {
                            this.plugin.getLogger().warning("Map Config: Wrong generatorLevel of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        int generatorType = timeActionData.optInt("generatorType", -1);

                        switch (generatorType) {
                            case 1:
                                generatorUpgradeTimeActions.add(new LobbyGeneratorUpgradeTimeActionData(1, generatorLevel, time));
                                continue;
                            case 2:
                                generatorUpgradeTimeActions.add(new LobbyGeneratorUpgradeTimeActionData(2, generatorLevel, time));
                                continue;
                            default:
                                this.plugin.getLogger().warning("Map Config: Wrong generatorType of a timeAction of " + name + " (" + index + ")");
                                break;
                        }

                        break;
                    default:
                        this.plugin.getLogger().warning("Map Config: Wrong type of a timeAction of " + name + " (" + index + ")");
                        continue;
                }

            }

            this.maps.add(new MapData(
                    name,
                    world,
                    respawnCooldown,
                    maxTime,
                    teams,
                    globalGenerators,
                    generatorUpgradeTimeActions
            ));
        }

    }

    private Location buildLocationFromJSONObject(JSONObject spawnpoint, boolean enableDirections) {

        double x = spawnpoint.optDouble("x", Double.MIN_VALUE);

        if (x == Double.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong x in a spawnpoint in team");
            return null;
        }

        double y = spawnpoint.optDouble("y", Double.MIN_VALUE);

        if (y == Double.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong y in a spawnpoint in team");
            return null;
        }

        double z = spawnpoint.optDouble("z", Double.MIN_VALUE);

        if (z == Double.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong z in a spawnpoint in team");
            return null;
        }

        if (!enableDirections) {
            return new Location(null, x, y, z);
        }

        float yaw = spawnpoint.optFloat("z", Float.MIN_VALUE);

        if (yaw == Float.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong yaw in a spawnpoint in team");
            return new Location(null, x, y, z);
        }

        float pitch = spawnpoint.optFloat("z", Float.MIN_VALUE);

        if (pitch == Float.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong pitch in a spawnpoint in team");
            return new Location(null, x, y, z);
        }

        return new Location(null, x, y, z, yaw, pitch);
    }

    private List<Location> buildLocationList(JSONArray locations, boolean enableDirections) {

        List<Location> returnList = new ArrayList<>();

        for (Object locationObject : locations) {

            if (!(locationObject instanceof JSONObject)) {
                this.plugin.getLogger().warning("Map Config: Wrong location");
                continue;
            }

            Location location = this.buildLocationFromJSONObject((JSONObject) locationObject, enableDirections);

            if (location == null) {
                this.plugin.getLogger().warning("Map Config: Wrong location");
                continue;
            }

            returnList.add(location);

        }

        return returnList;
    }

    private List<LobbyGeneratorData> buildGeneratorList(JSONArray generators) {

        List<LobbyGeneratorData> returnList = new ArrayList<>();

        for (Object generatorObject : generators) {

            if (!(generatorObject instanceof JSONObject)) {
                this.plugin.getLogger().warning("Map Config: Wrong generator");
                continue;
            }

            JSONObject generator = (JSONObject) generatorObject;

            JSONObject locationData = generator.optJSONObject("location");

            if (locationData == null) {
                this.plugin.getLogger().warning("Map Config: Location missing in a generator");
                continue;
            }

            double x = locationData.optDouble("x", Double.MIN_VALUE);

            if (x == Double.MIN_VALUE) {
                this.plugin.getLogger().warning("Map Config: Wrong x in a generator");
                continue;
            }

            double y = locationData.optDouble("y", Double.MIN_VALUE);

            if (y == Double.MIN_VALUE) {
                this.plugin.getLogger().warning("Map Config: Wrong y in a generator");
                continue;
            }

            double z = locationData.optDouble("z", Double.MIN_VALUE);

            if (z == Double.MIN_VALUE) {
                this.plugin.getLogger().warning("Map Config: Wrong z in a generator");
                continue;
            }

            Location generatorLocation = new Location(null, x, y, z);

            Material generatorMaterial = Material.getMaterial(generator.optString("material", ""));

            if (generatorMaterial == null) {
                this.plugin.getLogger().warning("Map Config: Wrong material in a generator");
                continue;
            }

            List<Double> generatorUpgradeSteps = new ArrayList<>();
            JSONArray generatorUpgradeStepArray = generator.optJSONArray("speed");

            if (generatorUpgradeStepArray == null) {
                this.plugin.getLogger().warning("Map Config: Missing speed in a generator");
                continue;
            }

            for (int i = 0; i < generatorUpgradeStepArray.length(); i++) {

                double speed = generatorUpgradeStepArray.optDouble(i, Double.MIN_VALUE);

                if (speed == Double.MIN_VALUE) {
                    this.plugin.getLogger().warning("Map Config: Wrong speed in a generator");
                    continue;
                }

                generatorUpgradeSteps.add(speed);

            }

            returnList.add(new LobbyGeneratorData(generatorLocation, new ItemStack(generatorMaterial), generatorUpgradeSteps));

        }

        return returnList;

    }

    private void logMissingMapConfigItem(String missingItem, int index, String mapName) {
        this.plugin.getLogger().warning("Map Config: Missing " + missingItem + " of map " + mapName + " (" + index + ")");
    }

    @Override
    public GameStatus tick() {

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers().keySet()) {
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
                this.players.put(player.getUniqueId(), new LobbyPlayerData());
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

    private List<MapData> getHighestVotedMaps() {

        // Get map votes

        Map<MapData, Integer> mapVotes = new HashMap<>();

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData.getVote() == null) {
                continue;
            }

            if (mapVotes.containsKey(playerData.getVote())) {
                mapVotes.put(playerData.getVote(), mapVotes.get(playerData.getVote()) + 1);
            } else {
                mapVotes.put(playerData.getVote(), 1);
            }

        }

        // Get list of maps with the highest vote count

        List<MapData> highestVotedMaps = new ArrayList<>();
        int maxVotes = Integer.MIN_VALUE;

        for (Map.Entry<MapData, Integer> entry : mapVotes.entrySet()) {
            int votes = entry.getValue();
            if (votes > maxVotes) {
                maxVotes = votes;
                highestVotedMaps.clear();
                highestVotedMaps.add(entry.getKey());
            } else if (votes == maxVotes) {
                highestVotedMaps.add(entry.getKey());
            }
        }

        return highestVotedMaps;

    }

    private void autoSelectMap() {

        MapData selectedMap = null;

        if (this.mapVoting) {

            List<MapData> highestVotedMaps = this.getHighestVotedMaps();

            if (!highestVotedMaps.isEmpty()) {

                selectedMap = highestVotedMaps.get(new Random().nextInt(highestVotedMaps.size()));

            }

        }

        if (selectedMap == null) {

            if (!this.maps.isEmpty()) {

                selectedMap = this.maps.get(new Random().nextInt(this.maps.size()));

            }

        }

        this.selectedMap = selectedMap;

    }

    private void displayMap() {

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            if (this.selectedMap == null) {
                return;
            }

            player.sendMessage("§bThe map has been set to " + this.selectedMap.getName());

        }

    }

    @Override
    public GamePart getNextStatus() {

        if (this.selectedMap == null) {
            this.autoSelectMap();
            this.displayMap();
        }

        if (this.selectedMap == null) {
            this.plugin.getLogger().warning("Game stopped because automatic map selection failed");
            return null;
        }

        MapData selectedMap = this.selectedMap;

        World world = this.plugin.loadWorld(selectedMap.getWorld());

        if (world == null) {
            return null;
        }

        JSONObject shopConfig = this.plugin.getShopConfig().getConfig();

        ArmorConfig armorConfig = new ArmorConfig(
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("enableArmorSystem", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyHelmet", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyChestplate", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyLeggings", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultHelmet", 125),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultChestplate", 126),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultLeggings", 127),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultBoots", 128)
        );

        Game game = new Game(
                this.plugin,
                world,
                selectedMap.getTeams(),
                selectedMap.getGlobalGenerators(),
                selectedMap.getGeneratorUpgradeTimeActions(),
                new JSONObject(shopConfig.optJSONObject("itemShop").toString()),
                armorConfig,
                selectedMap.getRespawnCooldown(),
                selectedMap.getMaxTime()
        );

        for (UUID playerId : this.getPlayers().keySet()) {
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

    public Map<UUID, LobbyPlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }
}
