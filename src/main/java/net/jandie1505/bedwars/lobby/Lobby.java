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
    private List<MapData> maps;

    public Lobby(Bedwars plugin) {
        this.plugin = plugin;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.map = null;
        this.forcestart = false;
        this.maps = new ArrayList<>();

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
                    this.plugin.getLogger().warning("Map Config: Missing color of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                String teamChatColorString = team.optString("chatColor");

                if (teamChatColorString == null) {
                    this.plugin.getLogger().warning("Map Config: Missing chatColor of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                ChatColor teamChatColor = ChatColor.valueOf(teamChatColorString);

                if (teamChatColor == null) {
                    this.plugin.getLogger().warning("Map Config: Wrong chatColor of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                JSONArray teamSpawnpointArray = team.optJSONArray("spawnpoints");

                if (teamSpawnpointArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing spawnpoints of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                List<Location> teamSpawnpoints = this.buildLocationList(teamSpawnpointArray, true);

                JSONArray bedLocationsArray = team.optJSONArray("bedLocations");

                if (bedLocationsArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing spawnpoints of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                List<Location> teamBedLocations = this.buildLocationList(bedLocationsArray, false);

                JSONArray teamGeneratorsArray = team.optJSONArray("generators");

                if (teamGeneratorsArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing teamGenerators of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                List<LobbyGeneratorData> teamGenerators = this.buildGeneratorList(teamGeneratorsArray);

                JSONArray shopVillagerLocationArray = team.optJSONArray("shopVillagers");

                if (shopVillagerLocationArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing shopVillagers of team " + name + " of map " + map + " (" + index + ")");
                    continue;
                }

                List<Location> shopVillagerLocations = this.buildLocationList(shopVillagerLocationArray, false);

                JSONArray upgradeVillagerLocationArray = team.optJSONArray("upgradeVillagers");

                if (upgradeVillagerLocationArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing upgradeVillagers of team " + name + " of map " + map + " (" + index + ")");
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
                List.of(
                        new LobbyTeamData(
                                "Green",
                                ChatColor.GREEN,
                                Color.LIME,
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
                                Color.RED,
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
                new JSONObject(shopConfig.optJSONObject("itemShop").toString()),
                armorConfig,
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
