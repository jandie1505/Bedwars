package net.jandie1505.bedwars.game.lobby;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.game.builder.GameBuilder;
import net.jandie1505.bedwars.game.game.player.upgrades.types.ArmorUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.UpgradableItemUpgrade;
import net.jandie1505.bedwars.game.lobby.commands.LobbyPlayersSubcommand;
import net.jandie1505.bedwars.game.lobby.commands.LobbyStartSubcommand;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.TeamData;
import net.jandie1505.bedwars.game.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.game.lobby.commands.LobbyValueSubcommand;
import net.jandie1505.bedwars.game.lobby.commands.LobbyVotemapCommand;
import net.jandie1505.bedwars.game.lobby.inventory.VotingMenuListener;
import net.jandie1505.bedwars.game.utils.LobbyChatListener;
import net.jandie1505.bedwars.game.utils.LobbyProtectionsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Lobby extends GamePart {
    private final List<MapData> maps;
    private final Map<UUID, LobbyPlayerData> players;
    private final int mapVoteButtonItemId;
    private final int teamSelectionButtonItemId;
    private final int mapButtonItemId;
    private final List<UUID> loginBypassList;
    private int timeStep;
    private int time;
    private boolean forcestart;
    private MapData selectedMap;
    private boolean mapVoting;
    private int requiredPlayers;
    private boolean timerPaused;
    private boolean lobbyBorderEnabled;
    private int[] lobbyBorder;
    private Location lobbySpawn;

    public Lobby(Bedwars plugin) {
        super(plugin);
        this.maps = new ArrayList<>();
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.mapVoteButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("mapVoteButton", -1);
        this.teamSelectionButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("teamSelectionButton", -1);
        this.mapButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("mapButton", -1);
        this.loginBypassList = Collections.synchronizedList(new ArrayList<>());
        this.timeStep = 0;
        this.time = 120;
        this.forcestart = false;
        this.selectedMap = null;
        this.mapVoting = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("mapVoting", false);
        this.requiredPlayers = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("requiredPlayers", 2);
        this.timerPaused = false;
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

        // Create map data from config

        JSONArray mapArray = this.getPlugin().getMapConfig().getConfig().optJSONArray("maps");

        if (mapArray == null) {
            mapArray = new JSONArray();
        }

        int index = -1;
        for (Object object : mapArray) {
            index++;

            if (!(object instanceof JSONObject map)) {
                this.getPlugin().getLogger().warning("Map Config: Index " + index + " is not a json object");
                continue;
            }
            
            try {
                this.maps.add(MapData.deserializeFromJSON(map));
            } catch (IllegalArgumentException e) {
                this.getPlugin().getLogger().warning("Map Config (at map index + " + index + "): " + e.getMessage());
            }
            
        }
        
        // Cloud system mode

        if (this.mapVoting) {
            this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), "Map Voting");
        } else {
            this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), "Random Map");
        }

        // Commands

        this.addDynamicSubcommand("start", SubcommandEntry.of(new LobbyStartSubcommand(plugin), sender -> Permissions.hasPermission(sender, Permissions.START)));
        this.addDynamicSubcommand("value", SubcommandEntry.of(new LobbyValueSubcommand(this)));
        this.addDynamicSubcommand("players", SubcommandEntry.of(new LobbyPlayersSubcommand(this)));
        this.addDynamicSubcommand("votemap", SubcommandEntry.of(new LobbyVotemapCommand(plugin)));

        // Listeners

        this.registerListener(new LobbyProtectionsListener(this));
        this.registerListener(new VotingMenuListener(this));
        this.registerListener(new LobbyChatListener(this));
        this.getTaskScheduler().runTaskLater(() -> this.getPlugin().getListenerManager().manageListeners(), 2, "listener_reload_on_start");

        // Tasks

        this.getTaskScheduler().scheduleRepeatingTask(this::lobbyTask, 1, 1, "lobby");
    }

    public boolean shouldRun() {
        return true;
    }
    
    // TASKS

    public boolean lobbyTask() {

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            LobbyPlayerData playerData = this.players.get(playerId);

            // Enforce game mode

            if (player.getGameMode() != GameMode.ADVENTURE && !this.getPlugin().isPlayerBypassing(playerId)) {
                player.setGameMode(GameMode.ADVENTURE);
            }

            // Set Health

            if (player.getHealth() < 20) {
                player.setHealth(20);
            }

            // Set Food

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            if (player.getSaturation() < 20) {
                player.setSaturation(20);
            }

            // Display action bar

            if (this.players.size() >= this.requiredPlayers) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + " seconds"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cNot enough players (" + this.players.size() + "/" + this.requiredPlayers + ")"));
            }

            // Messages

            if ((this.time <= 5 || (this.time % 10 == 0)) && this.players.size() >= this.requiredPlayers && this.timeStep >= 20) {
                player.sendMessage("§7The game starts in " + this.time + " seconds");
            }

            // Lobby border

            if (!this.getPlugin().isPlayerBypassing(playerId) && this.lobbyBorderEnabled) {

                Location location = player.getLocation();

                if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
                    player.teleport(this.lobbySpawn);
                }

            }

            // Scoreboard

            if (this.timeStep >= 20) {
                String mapName = "---";

                if (this.selectedMap != null) {
                    mapName = this.selectedMap.name();
                }

                Scoreboard scoreboard = this.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("lobby", Criteria.DUMMY, "");

                objective.setDisplayName("§6§lBEDWARS");

                objective.getScore("§§§§").setScore(7);

                if (this.players.size() >= 2) {

                    objective.getScore("§bStarting in " + this.time).setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §a" + this.players.size() + " / 2").setScore(4);

                } else {

                    objective.getScore("§cNot enough players").setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §c" + this.players.size() + " / " + this.requiredPlayers).setScore(4);

                }

                objective.getScore("§§").setScore(3);
                objective.getScore("§7Map: §a" + mapName).setScore(2);

                objective.getScore("§").setScore(0);

                if (this.selectedMap != null) {
                    if (playerData.getTeam() > 0) {
                        TeamData team = this.selectedMap.teams().get(playerData.getTeam());

                        if (team != null) {
                            objective.getScore("§7Team: " + team.chatColor() + team.name()).setScore(1);
                        } else {
                            objective.getScore("§7Team: §c?").setScore(1);
                        }
                    } else {
                        objective.getScore("§7Team: §a" + "---").setScore(1);
                    }
                }

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);
            }

            // Items

            if (!this.getPlugin().isPlayerBypassing(player.getUniqueId())) {

                ItemStack lobbyVoteHotbarItem = this.getPlugin().getItemStorage().getItem(this.mapVoteButtonItemId);
                ItemStack lobbyTeamSelectionHotbarItem = this.getPlugin().getItemStorage().getItem(this.teamSelectionButtonItemId);

                if (lobbyVoteHotbarItem != null && lobbyTeamSelectionHotbarItem != null) {

                    for (ItemStack item : Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length)) {

                        if (item == null || item.getType() == Material.AIR) {
                            continue;
                        }

                        if (!item.isSimilar(lobbyVoteHotbarItem) && !item.isSimilar(lobbyTeamSelectionHotbarItem)) {
                            player.getInventory().clear();
                        }

                    }

                    if (!player.getInventory().contains(lobbyVoteHotbarItem)) {
                        player.getInventory().setItem(3, lobbyVoteHotbarItem);
                    }

                    if (!player.getInventory().contains(lobbyTeamSelectionHotbarItem)) {
                        player.getInventory().setItem(5, lobbyTeamSelectionHotbarItem);
                    }

                }

            }

        }

        // ADD PLAYERS

        for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {

            if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
                continue;
            }

            if (!this.players.containsKey(player.getUniqueId())) {
                this.players.put(player.getUniqueId(), new LobbyPlayerData());
            }

        }

        // Select Map if timer is 10 or lower

        if (this.selectedMap == null && this.time <= 60) {
            this.autoSelectMap();
        }

        // TIME

        if (this.timeStep >= 20) {
            if (this.time > 0) {

                if (this.players.size() >= this.requiredPlayers) {
                    this.time--;
                } else if (this.time < 60) {
                    this.time++;
                }

            } else {
                this.getPlugin().nextStatus();
                return true;
            }
        }

        // FORCE START

        if (this.forcestart) {
            this.getPlugin().nextStatus();
            return true;
        }

        // TIME STEP

        if (this.timeStep >= 20) {
            this.timeStep = 0;
        } else {
            this.timeStep++;
        }

        return true;
    }

    // LISTENERS

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (this.getPlugin().isPlayerBypassing(event.getPlayer())) {
            event.joinMessage(null);
            return;
        }

        event.joinMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has joined", NamedTextColor.GRAY))
        );

        event.getPlayer().teleport(this.getLobbySpawn().clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (this.getPlugin().isPlayerBypassing(event.getPlayer())) {
            event.quitMessage(null);
            return;
        }

        event.quitMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has left", NamedTextColor.GRAY))
        );
    }
    
    // UTILITIES

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

        this.selectMap(selectedMap);

    }

    private void displayMap() {

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            if (this.selectedMap == null) {
                return;
            }

            player.sendMessage("§bThe map has been set to " + this.selectedMap.name());

        }

    }

    private void createPartyTeams() {

        if (this.selectedMap == null) {
            return;
        }

        if (this.getPlugin().getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("partyandfriends", false)) {

            try {
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer");
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PartyManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PlayerParty");

                // Add all parties on this server to a list

                List<PlayerParty> parties = new ArrayList<>();

                for (UUID playerId : this.getPlayers().keySet()) {
                    Player player = this.getPlugin().getServer().getPlayer(playerId);

                    if (player == null) {
                        continue;
                    }

                    PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(playerId);

                    if (pafPlayer == null) {
                        continue;
                    }

                    PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);

                    if (party == null) {
                        return;
                    }

                    if (!parties.contains(party)) {
                        parties.add(party);
                    }

                }

                // create party teams

                List<Integer> partyTeams = new ArrayList<>();

                for (PlayerParty party : parties) {

                    // check if the party size is higher than the max players per team if enabled

                    int maxPlayers = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("playersPerTeam", -1);

                    if (maxPlayers > 0 && party.getAllPlayers().size() > maxPlayers) {
                        continue;
                    }

                    // iterate through all teams to find the right team for the party

                    for (TeamData team : this.selectedMap.teams()) {
                        int teamId = this.selectedMap.teams().indexOf(team);

                        if (teamId < 0) {
                            continue;
                        }

                        // calculate team size of that team when the party would have joined the team

                        int playersInTeam = party.getAllPlayers().size() + this.getTeamPlayers(teamId).size();

                        // check if the amount of players is higher than the max players per team value if enabled

                        if (maxPlayers > 0 && playersInTeam > maxPlayers) {
                            continue;
                        }

                        // check if this team has been marked that it already contains a party
                        // this is done to put every party into a separate team as long as there are enough empty teams

                        if (partyTeams.contains(teamId)) {

                            boolean otherTeamAvailable = false;

                            for (TeamData anotherTeam : this.selectedMap.teams()) {
                                int otherTeamId = this.selectedMap.teams().indexOf(anotherTeam);

                                if (otherTeamId < 0) {
                                    continue;
                                }

                                if (partyTeams.contains(otherTeamId)) {
                                    continue;
                                }

                                if (this.getTeamPlayers(otherTeamId).isEmpty()) {
                                    otherTeamAvailable = true;
                                    break;
                                }

                            }

                            // continue if another team is available for this party

                            if (otherTeamAvailable) {
                                continue;
                            }

                        }

                        // at this point, the team for the party has been selected

                        // mark team that it contains a party

                        partyTeams.add(teamId);

                        // add party players to team

                        for (PAFPlayer pafPlayer : party.getAllPlayers()) {
                            LobbyPlayerData playerData = this.players.get(pafPlayer.getUniqueId());

                            if (playerData == null) {
                                continue;
                            }

                            playerData.setTeam(teamId);

                        }

                        // break out of the team loop because a team was found for that party

                        break;
                    }

                }

            } catch (ClassNotFoundException ignored) {
                // ignored
            }

        }

    }

    @Override
    public GamePart getNextStatus() {

        if (this.selectedMap == null) {
            this.autoSelectMap();
        }

        if (this.selectedMap == null) {
            this.getPlugin().getLogger().warning("Game stopped because automatic map selection failed");
            return null;
        }

        Game game = new GameBuilder(this.getPlugin()).build(this.selectedMap);

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.getPlayers().get(playerId);
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player != null) {
                player.sendMessage("§bMap: " + this.selectedMap.name());
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

        this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), this.selectedMap.name());

        return game;
    }

    private void updateCloudNetMotdAndSlots(int maxPlayers, String motd) {

        if (this.getPlugin().getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("cloudnet", false)) {

            try {

                try {
                    Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
                    Class.forName("eu.cloudnetservice.modules.bridge.BridgeServiceHelper");
                    Class.forName("eu.cloudnetservice.wrapper.holder.ServiceInfoHolder");

                    BridgeServiceHelper bridgeServiceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);

                    if (bridgeServiceHelper != null) {

                        if (maxPlayers > 0) {
                            bridgeServiceHelper.maxPlayers().set(maxPlayers);
                        }

                        if (motd != null) {
                            bridgeServiceHelper.motd().set(motd);
                        }

                    }

                    ServiceInfoHolder serviceInfoHolder = InjectionLayer.ext().instance(ServiceInfoHolder.class);
                    if (serviceInfoHolder != null) {
                        serviceInfoHolder.publishServiceInfoUpdate();
                    }

                } catch (ClassNotFoundException ignored) {
                    // ignored (cloudnet not installed)
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    
    // OTHER

    public int getMaxPlayers() {

        int teams = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("teamCount", -1);
        int playersPerTeam = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("playersPerTeam", -1);

        if (teams <= 0 || playersPerTeam <= 0) {
            return -1;
        }

        return teams * playersPerTeam;
    }

    public List<UUID> getTeamPlayers(int teamId) {
        List<UUID> teamPlayers = new ArrayList<>();

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (teamId == playerData.getTeam()) {
                teamPlayers.add(playerId);
            }

        }

        return List.copyOf(teamPlayers);
    }

    public void forcestart() {
        this.forcestart = true;
    }

    /**
     * Returns a copy of the internal player data map.
     * @return player data map
     */
    public @NotNull Map<UUID, LobbyPlayerData> getPlayerDataMap() {
        return Map.copyOf(this.players);
    }

    /**
     * Returns a set of players currently registered.
     * @return set of players
     */
    public @NotNull Set<UUID> getRegisteredPlayers() {
        return Map.copyOf(this.players).keySet();
    }

    /**
     * Returns the LobbyPlayerData of the player with the specified uuid.
     * @param playerId player uuid
     * @return player data
     */
    public @Nullable LobbyPlayerData getPlayerData(@Nullable UUID playerId) {
        if (playerId == null) return null;
        return this.players.get(playerId);
    }

    /**
     * Returns the LobbyPlayerData of the specified player.
     * @param player player
     * @return player data
     */
    public @Nullable LobbyPlayerData getPlayerData(@Nullable OfflinePlayer player) {
        if (player == null) return null;
        return this.getPlayerData(player.getUniqueId());
    }

    /**
     * Returns true if the player with the specified uuid is ingame (in the lobby).
     * @param playerId player uuid
     * @return ingame
     */
    public boolean isPlayerIngame(@Nullable UUID playerId) {
        if (playerId == null) return false;
        return this.getPlayerData(playerId) != null;
    }

    @Deprecated(forRemoval = true)
    public Map<UUID, LobbyPlayerData> getPlayers() {
        return this.getPlayerDataMap();
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

    public void addLoginBypassPlayer(UUID playerId) {
        this.loginBypassList.add(playerId);
    }

    public boolean removeLoginBypassPlayer(UUID playerId) {
        return this.loginBypassList.remove(playerId);
    }

    public List<UUID> getLoginBypassList() {
        return List.copyOf(this.loginBypassList);
    }

    public void clearLoginBypassList() {
        this.loginBypassList.clear();
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }

    public MapData getSelectedMap() {
        return this.selectedMap;
    }

    public void selectMap(MapData selectedMap) {
        this.selectedMap = selectedMap;
        this.displayMap();
        this.createPartyTeams();

        if (this.selectedMap != null) {
            this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), this.selectedMap.name());
        } else {

            if (this.mapVoting) {
                this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), "Map Voting");
            } else {
                this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), "Random Map");
            }

        }

    }

    /**
     * Searches a map by its world name.
     * @param worldName world name
     * @return map
     */
    public @Nullable MapData findMapByWorldName(String worldName) {

        for (MapData mapData : this.maps) {
            if (mapData.world().equalsIgnoreCase(worldName)) {
                return mapData;
            }
        }

        return null;
    }

    /**
     * Searches a map by its name.
     * @param mapName map name
     * @return map
     */
    public @Nullable MapData findMapByName(String mapName) {

        for (MapData mapData : this.maps) {
            if (mapData.name().equalsIgnoreCase(mapName)) {
                return mapData;
            }
        }

        return null;
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

    public int getMapButtonItemId() {
        return this.mapButtonItemId;
    }

    public int getMapVoteButtonItemId() {
        return this.mapVoteButtonItemId;
    }

    public int getTeamSelectionButtonItemId() {
        return teamSelectionButtonItemId;
    }

    public Location getLobbySpawn() {
        return this.lobbySpawn.clone();
    }
}
