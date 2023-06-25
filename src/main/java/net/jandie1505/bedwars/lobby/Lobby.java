package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.shop.ArmorConfig;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.lobby.setup.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Lobby implements GamePart {
    private final Bedwars plugin;
    private final List<MapData> maps;
    private final Map<UUID, LobbyPlayerData> players;
    private int timeStep;
    private int time;
    private boolean forcestart;
    private MapData selectedMap;
    private boolean mapVoting;
    private int requiredPlayers;
    private boolean timerPaused;

    public Lobby(Bedwars plugin) {
        this.plugin = plugin;
        this.maps = new ArrayList<>();
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.timeStep = 0;
        this.time = 60;
        this.forcestart = false;
        this.selectedMap = null;

        this.mapVoting = this.plugin.getConfigManager().getConfig().optBoolean("mapVoting", false);
        this.requiredPlayers = this.plugin.getConfigManager().getConfig().optInt("requiredPlayers", 2);
        this.timerPaused = false;

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

            int spawnBlockPlaceProtectionRadius = map.optInt("spawnBlockPlaceProtectionRadius", -1);

            if (spawnBlockPlaceProtectionRadius < 0) {
                this.logMissingMapConfigItem("spawnBlockPlaceProtectionRadius", index, name);
                continue;
            }

            int villagerBlockPlaceProtectionRadius = map.optInt("villagerBlockPlaceProtectionRadius", -1);

            if (villagerBlockPlaceProtectionRadius < 0) {
                this.logMissingMapConfigItem("villagerBlockPlaceProtectionRadius", index, name);
                continue;
            }

            int mapRadius = map.optInt("mapRadius", -1);

            if (mapRadius < 0) {
                this.logMissingMapConfigItem("mapRadius", index, name);
                continue;
            }

            Location centerLocation = this.buildLocationFromJSONObject(map.optJSONObject("center", new JSONObject()), false);

            if (centerLocation == null) {
                this.logMissingMapConfigItem("center", index, name);
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

                int baseRadius = team.optInt("baseRadius", -1);

                if (baseRadius < 0) {
                    this.plugin.getLogger().warning("Map Config: Missing baseRadius of team " + teamName + " of map " + name + " (" + index + ")");
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

                List<Location> shopVillagerLocations = this.buildLocationList(shopVillagerLocationArray, true);

                JSONArray upgradeVillagerLocationArray = team.optJSONArray("upgradeVillagers");

                if (upgradeVillagerLocationArray == null) {
                    this.plugin.getLogger().warning("Map Config: Missing upgradeVillagers of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> upgradeVillagerLocations = this.buildLocationList(upgradeVillagerLocationArray, true);

                teams.add(new LobbyTeamData(teamName, teamChatColor, teamColor, teamSpawnpoints, baseRadius, teamBedLocations, teamGenerators, shopVillagerLocations, upgradeVillagerLocations));
            }

            JSONArray globalGeneratorArray = map.optJSONArray("globalGenerators");

            if (globalGeneratorArray == null) {
                this.logMissingMapConfigItem("teams", index, name);
                continue;
            }

            List<LobbyGeneratorData> globalGenerators = this.buildGeneratorList(globalGeneratorArray);

            List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions = new ArrayList<>();
            List<LobbyDestroyBedsTimeActionData> destroyBedsTimeActions = new ArrayList<>();
            List<LobbyWorldborderChangeTimeActionData> worldborderChangeTimeActions = new ArrayList<>();
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
                    case "DESTROY_BEDS":
                        destroyBedsTimeActions.add(new LobbyDestroyBedsTimeActionData(time, timeActionData.optBoolean("disableBeds", false)));
                        break;
                    case "WORLDBORDER_CHANGE":

                        int radius = timeActionData.optInt("radius", -1);

                        if (radius < 0) {
                            this.plugin.getLogger().warning("Map Config: Wrong radius of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        String chatMessage = timeActionData.optString("chatMessage");

                        if (chatMessage == null) {
                            this.plugin.getLogger().warning("Map Config: Wrong chatMessage of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        String scoreboardText = timeActionData.optString("scoreboardText");

                        if (scoreboardText == null) {
                            this.plugin.getLogger().warning("Map Config: Wrong scoreboardText of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        worldborderChangeTimeActions.add(new LobbyWorldborderChangeTimeActionData(time, radius, chatMessage, scoreboardText));
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
                    generatorUpgradeTimeActions,
                    destroyBedsTimeActions,
                    worldborderChangeTimeActions,
                    spawnBlockPlaceProtectionRadius,
                    villagerBlockPlaceProtectionRadius,
                    centerLocation,
                    mapRadius
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

        float yaw = spawnpoint.optFloat("yaw", Float.MIN_VALUE);

        if (yaw == Float.MIN_VALUE) {
            this.plugin.getLogger().warning("Map Config: Wrong yaw in a spawnpoint in team");
            return new Location(null, x, y, z);
        }

        float pitch = spawnpoint.optFloat("pitch", Float.MIN_VALUE);

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

            if (this.players.size() >= this.requiredPlayers) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + " seconds"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cNot enough players (" + this.players.size() + "/" + this.requiredPlayers + ")"));
            }

        }

        // ADD PLAYERS

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

            if (!this.players.containsKey(player.getUniqueId())) {
                this.players.put(player.getUniqueId(), new LobbyPlayerData());
            }

        }

        // Select Map if timer is 10 or lower

        if (this.selectedMap == null && this.time <= 10) {
            this.autoSelectMap();
            this.displayMap();
        }

        // TIME

        if (this.timeStep >= 20) {
            if (this.time > 0) {

                if (this.players.size() >= this.requiredPlayers) {
                    this.time--;
                } else if (this.time < 90) {
                    this.time++;
                }

            } else {
                return GameStatus.NEXT_STATUS;
            }
        }

        // FORCE START

        if (this.forcestart) {
            return GameStatus.NEXT_STATUS;
        }

        // TIME STEP

        if (this.timeStep >= 20) {
            this.timeStep = 0;
        } else {
            this.timeStep++;
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

        TeamUpgradesConfig teamUpgradesConfig = new TeamUpgradesConfig(
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("sharpness", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("protection", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("haste", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("generators", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("healpool", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("dragonbuff", new JSONObject())),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("noTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("alarmTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("itsATrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("miningFatigueTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("countermeasuresTrap")
        );

        Game game = new Game(
                this.plugin,
                world,
                selectedMap.getTeams(),
                selectedMap.getGlobalGenerators(),
                selectedMap.getGeneratorUpgradeTimeActions(),
                selectedMap.getDestroyBedsTimeActions(),
                selectedMap.getWorldBorderChangeTimeActions(),
                new JSONObject(shopConfig.optJSONObject("itemShop").toString()),
                armorConfig,
                teamUpgradesConfig,
                selectedMap.getRespawnCooldown(),
                selectedMap.getMaxTime(),
                selectedMap.getSpawnBlockPlaceProtection(),
                selectedMap.getVillagerBlockPlaceProtection(),
                selectedMap.getCenterLocation(),
                selectedMap.getMapRadius()
        );

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.getPlayers().get(playerId);
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player != null) {
                player.sendMessage("§bMap: " + this.selectedMap.getName());
            }

            if (playerData.getTeam() < 0) {
                continue;
            }

            game.addPlayer(playerId, playerData.getTeam());
            this.players.remove(playerId);
        }

        for (UUID playerId : this.getPlayers().keySet()) {
            List<BedwarsTeam> teams = new ArrayList<>(game.getTeams());

            if (teams.isEmpty()) {
                continue;
            }

            teams.sort(Comparator.comparingInt(o -> o.getPlayers().size()));

            BedwarsTeam lowestPlayerCount = teams.get(0);

            game.addPlayer(playerId, lowestPlayerCount.getId());
            this.players.remove(playerId);
        }

        return game;
    }

    private TeamUpgrade getErrorUpgrade() {
        return new TeamUpgrade(-1, List.of(), List.of(), List.of());
    }

    private TeamUpgrade buildTeamUpgrade(JSONObject teamUpgrade) {

        int itemId = teamUpgrade.optInt("item", -1);

        if (itemId < 0) {
            this.plugin.getLogger().warning("Shop Config: Missing/wrong item in team upgrade");
            return this.getErrorUpgrade();
        }

        JSONArray priceListArray = teamUpgrade.optJSONArray("prices");

        if (priceListArray == null) {
            this.plugin.getLogger().warning("Shop Config: Missing/Wrong prices in team upgrade");
            return this.getErrorUpgrade();
        }

        List<Integer> prices = new ArrayList<>();
        List<Material> currencies = new ArrayList<>();

        for (int i = 0; i < priceListArray.length(); i++) {

            int price = priceListArray.optInt(i, -1);

            if (price < 0) {
                this.plugin.getLogger().warning("Shop Config: Wrong price in prices in team upgrade");
                return this.getErrorUpgrade();
            }

            prices.add(price);
            currencies.add(Material.DIAMOND);

        }

        JSONArray levelArray = teamUpgrade.optJSONArray("levels");

        List<Integer> levels = new ArrayList<>();

        if (levelArray == null) {

            for (int i = 0; i < prices.size(); i++) {
                levels.add(-1);
            }

        } else {

            for (int i = 0; i < levelArray.length(); i++) {

                int level = levelArray.optInt(i);

                if (level < 0) {
                    level = -1;
                }

                levels.add(level);

            }

        }

        return new TeamUpgrade(itemId, List.copyOf(prices), List.copyOf(currencies), List.copyOf(levels));
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

    public boolean addPlayer(UUID playerId) {
        if (this.players.containsKey(playerId)) {
            return false;
        }

        this.players.put(playerId, new LobbyPlayerData());
        return true;
    }

    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }

    public MapData getSelectedMap() {
        return this.selectedMap;
    }

    public void selectMap(MapData selectedMap) {
        this.selectedMap = selectedMap;
    }

    public boolean isMapVoting() {
        return this.mapVoting;
    }

    public void setMapVoting(boolean mapVoting) {
        this.mapVoting = mapVoting;
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    public void setRequiredPlayers(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    public boolean isTimerPaused() {
        return timerPaused;
    }

    public void setTimerPaused(boolean timerPaused) {
        this.timerPaused = timerPaused;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
