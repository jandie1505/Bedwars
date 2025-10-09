package net.jandie1505.bedwars.game.game;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.ConfigSetup;
import net.jandie1505.bedwars.constants.ConfigKeys;
import net.jandie1505.bedwars.game.game.commands.*;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.endlobby.Endlobby;
import net.jandie1505.bedwars.game.game.constants.GameConfigKeys;
import net.jandie1505.bedwars.game.game.entities.base.ManagedEntity;
import net.jandie1505.bedwars.game.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.game.entities.entities.ShopVillager;
import net.jandie1505.bedwars.game.game.entities.entities.UpgradeVillager;
import net.jandie1505.bedwars.game.game.events.GamePlayerRespawnEvent;
import net.jandie1505.bedwars.game.game.generators.Generator;
import net.jandie1505.bedwars.game.game.generators.GeneratorData;
import net.jandie1505.bedwars.game.game.generators.PublicGenerator;
import net.jandie1505.bedwars.game.game.generators.TeamGenerator;
import net.jandie1505.bedwars.game.game.listeners.*;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.shop.ItemShop;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgradeManager;
import net.jandie1505.bedwars.game.game.shop.entries.QuickBuyMenuEntry;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.shop.gui.ShopGUI;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.TeamData;
import net.jandie1505.bedwars.game.game.team.gui.TeamGUI;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrapManager;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeActionData;
import net.jandie1505.bedwars.game.game.timeactions.provider.TimeActionCreator;
import net.jandie1505.bedwars.game.game.world.BlockProtectionSystem;
import net.jandie1505.bedwars.utilities.ItemSimilarityKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Game extends GamePart implements ManagedListener {
    private final World world;

    @NotNull private final String name;
    private final int respawnCountdown;
    private final int maxTime;
    private final int spawnBlockPlaceProtection;
    private final int villagerBlockPlaceProtection;
    @NotNull private final ImmutableLocation centerLocation;
    private final int mapRadius;

    private final List<BedwarsTeam> teams;
    private final Map<UUID, PlayerData> players;
    private final List<Generator> generators;
    private final List<TimeAction> timeActions;
    private final BlockProtectionSystem blockProtectionSystem;
    private final Map<UUID, Scoreboard> playerScoreboards;
    @NotNull private final ItemShop itemShop;
    @NotNull private final ShopGUI shopGUI;
    @NotNull private final PlayerUpgradeManager playerUpgradeManager;
    @NotNull private final TeamUpgradeManager teamUpgradeManager;
    @NotNull private final TeamGUI teamGUI;
    @NotNull private final TeamTrapManager teamTrapManager;
    private final List<ManagedEntity<?>> managedEntities;
    private int timeStep;
    private int time;
    private int publicEmeraldGeneratorLevel;
    private int publicDiamondGeneratorLevel;
    private boolean prepared;
    private BedwarsTeam winner;
    private boolean noWinnerEnd;

    // ----- INIT -----

    public Game(Bedwars plugin, World world, MapData data, Map<String, ShopEntry> shopEntries, Map<String, UpgradeEntry> playerUpgradeEntries, @Nullable Map<Integer, QuickBuyMenuEntry> defaultQuickBuyMenu, @NotNull Map<String, UpgradeEntry> teamUpgradeEntries, @NotNull Map<String, TeamGUI.TrapEntry> teamTrapEntries) {
        super(plugin);
        this.world = world;

        this.getConfig().merge(ConfigSetup.loadDataStorage(this.getPlugin(), "game.yml"), true);
        this.getConfig().merge(this.getPlugin().config().getSection("game"), true);

        this.name = data.name();
        this.respawnCountdown = data.respawnCountdown();
        this.maxTime = data.maxTime();
        this.spawnBlockPlaceProtection = data.spawnBlockPlaceProtection();
        this.villagerBlockPlaceProtection = data.villagerBlockPlaceProtection();
        this.centerLocation = new ImmutableLocation(WorldUtils.locationWithWorld(data.centerLocation().mutableCopy(), this.world));
        this.mapRadius = data.mapRadius();

        this.teams = Collections.synchronizedList(new ArrayList<>());
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.generators = Collections.synchronizedList(new ArrayList<>());
        this.timeActions = Collections.synchronizedList(new ArrayList<>());
        this.blockProtectionSystem = new BlockProtectionSystem(this);
        this.playerScoreboards = Collections.synchronizedMap(new HashMap<>());
        this.itemShop = new ItemShop(this);
        this.shopGUI = new ShopGUI(this, defaultQuickBuyMenu);
        this.playerUpgradeManager = new PlayerUpgradeManager(this, () -> false);
        this.teamUpgradeManager = new TeamUpgradeManager(this, () -> false);
        this.teamGUI = new TeamGUI(this, teamUpgradeEntries, teamTrapEntries, () -> false);
        this.teamTrapManager = new TeamTrapManager(this, () -> false);
        this.managedEntities = Collections.synchronizedList(new ArrayList<>());
        this.time = this.maxTime;
        this.publicEmeraldGeneratorLevel = 0;
        this.publicDiamondGeneratorLevel = 0;
        this.prepared = false;
        this.winner = null;
        this.noWinnerEnd = false;

        // SHOP

        this.itemShop.getItems().putAll(shopEntries);
        this.itemShop.getUpgrades().putAll(playerUpgradeEntries);

        // TEAMS

        this.setupTeams(data.teams());

        // GLOBAL GENERATORS

        this.setupGlobalGenerators(data.globalGenerators());

        // TIME ACTIONS

        this.setupTimeActions(data.timeActions());

        // WORLD BORDER

        this.world.getWorldBorder().setCenter(this.centerLocation.mutableCopy());
        this.world.getWorldBorder().setSize(this.mapRadius * 2);

        // GAME RULES

        this.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

        // PREPARE GAME

        this.getTaskScheduler().runTaskLater(this::prepareGame, 1, "prepare_game");

        // COMMANDS

        this.addDynamicSubcommand("value", SubcommandEntry.of(new GameValueSubcommand(this)));
        this.addDynamicSubcommand("players", SubcommandEntry.of(new GamePlayersSubcommand(this)));
        this.addDynamicSubcommand("teams", SubcommandEntry.of(new GameTeamsSubcommand(this)));
        this.addDynamicSubcommand("teleport-to-map", SubcommandEntry.of(new GameTeleportToMapSubcommand(this)));
        this.addDynamicSubcommand("finish", SubcommandEntry.of(new GameFinishSubcommand(this)));
        this.addDynamicSubcommand("shop", SubcommandEntry.of(new GameShopSubcommand(this)));

        // TASKS

        this.getTaskScheduler().scheduleRepeatingTask(this::playerScoreboardTask, 1, 20, "player_scoreboards");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerGameModeTask, 1, 1, "player_gamemodes");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerVisibilityTask, 1, 1, "player_visibility");
        this.getTaskScheduler().scheduleRepeatingTask(this::teleportSpectatorsTask, 1, 20, "player_teleport_spectators");

        this.getTaskScheduler().scheduleRepeatingTask(this::playerCleanupTask, 1, 20, "ingame_player_cleanup");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerAliveStatusTask, 1, 20, "ingame_player_alive_status");
        this.getTaskScheduler().scheduleRepeatingTask(this::inventoryTickTask, 1, 1, "ingame_player_inventory");
        this.getTaskScheduler().scheduleRepeatingTask(this::groupItemsTask, 1, 10*20, "group_items");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerValuesTask, 1, 200, "ingame_player_values");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerTimerTask, 1, 1, "player_timers");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerTrackerTask, 1, 100, "ingame_player_player_tracker");
        this.getTaskScheduler().scheduleRepeatingTask(this::tntParticleTask, 1, 20, "ingame_player_tnt_particles");

        this.getTaskScheduler().scheduleRepeatingTask(this::scoreboardCleanup, 1, 1, "scoreboard_cleanup");
        this.getTaskScheduler().scheduleRepeatingTask(this::generatorTick, 1, 1, "generators");
        this.getTaskScheduler().scheduleRepeatingTask(this::timeActions, 1, 20, "time_actions");
        this.getTaskScheduler().scheduleRepeatingTask(this::cleanupManagedEntitiesTask, 1, 10*20, "cleanup_managed_entities");
        this.getTaskScheduler().scheduleRepeatingTask(this::gameEndConditions, 1, 1, "game_end_conditions");
        this.getTaskScheduler().scheduleRepeatingTask(this::gameEndCheck, 1, 1, "game_end_check");
        this.getTaskScheduler().scheduleRepeatingTask(this::timeTask, 1, 20, "time");
        this.getTaskScheduler().scheduleRepeatingTask(this::backwardCompatibleTimeStepTask, 1, 1, "time_step");

        // EVENTS

        this.registerListener(this);
        this.registerListener(new GameInventoryManagementListener(this));
        this.registerListener(new GameDeathListener(this));
        this.registerListener(new SpecialItemListeners(this));
        this.registerListener(new GameMiscListener(this));
        this.registerListener(new GameProtectionsForNotIngamePlayersListener(this));
        this.registerListener(new GameChatListener(this));
        this.getTaskScheduler().runTaskLater(() -> this.getPlugin().getListenerManager().manageListeners(), 2, "listener_reload_on_start");
    }

    private void setupTeams(@NotNull List<TeamData> teams) {

        for (TeamData teamData : List.copyOf(teams)) {

            // Create and add team
            BedwarsTeam team = new BedwarsTeam(this, teamData);
            this.teams.add(team);

            // Setup team generators
            for (GeneratorData generatorData : teamData.generators()) {
                this.generators.add(new TeamGenerator(
                        this,
                        generatorData,
                        team
                ));
            }

            // Setup shop villagers
            for (Location location : teamData.shopVillagerLocations()) {
                new ShopVillager(this, WorldUtils.locationWithWorld(location, this.getWorld()), team.getId());
            }

            // Setup upgrade villagers
            for (Location location : teamData.upgradeVillagerLocations()) {
                new UpgradeVillager(this, WorldUtils.locationWithWorld(location, this.getWorld()), team.getId());
            }

        }

    }

    private void setupGlobalGenerators(@NotNull List<GeneratorData> globalGenerators) {

        for (GeneratorData generatorData : globalGenerators) {
            this.generators.add(new PublicGenerator(
                    this,
                    generatorData
            ));
        }

    }

    private void setupTimeActions(@NotNull List<TimeActionData> timeActionDataList) {
        TimeActionCreator timeActionCreator = new TimeActionCreator(this);

        for (TimeActionData timeActionData : timeActionDataList) {
            try {
                TimeAction timeAction = timeActionCreator.createTimeAction(timeActionData);
                if (timeAction == null) {
                    this.getPlugin().getLogger().warning("Couldn't create time action: Invalid type");
                    continue;
                }
                this.timeActions.add(timeAction);
            } catch (Exception e) {
                this.getPlugin().getLogger().log(Level.WARNING, "Couldn't create time action", e);
                continue;
            }
        }

        Collections.sort(this.timeActions);
    }

    // ----- ? -----

    @Override
    public boolean shouldExecute() {
        return this.world != null && this.getPlugin().getServer().getWorlds().contains(world);
    }

    // PLAYER TASKS

    /**
     * This task handles the player scoreboard.
     */
    private void playerScoreboardTask() {

        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {

            if (!this.playerScoreboards.containsKey(player.getUniqueId())) {
                this.playerScoreboards.put(player.getUniqueId(), this.getPlugin().getServer().getScoreboardManager().getNewScoreboard());
            }

            this.scoreboardTick(
                    player,
                    this.getSidebar(this.players.get(player.getUniqueId()))
            );

        }

    }

    /**
     * This task handles player game modes.
     * Bypassing players -> Do not modify game mode
     * Spectator -> Spectator mode
     * Ingame alive -> Survival mode
     * Ingame dead -> Spectator mode
     */
    private void playerGameModeTask() {

        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
            if (this.getPlugin().isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) return;

            PlayerData playerData = this.getPlayers().get(player.getUniqueId());
            if (playerData != null) {

                if (playerData.isAlive()) {

                    if (player.getGameMode() != GameMode.SURVIVAL) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                } else {

                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SPECTATOR);
                    }

                }

            } else {
                player.setGameMode(GameMode.SPECTATOR);
            }

        }

    }

    /**
     * This task handles player visibility.
     * Bypassing players should see all players.
     * Ingame players should only see other ingame players.
     * Spectators should only see ingame players.
     */
    private void playerVisibilityTask() {
        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
            for (Player otherPlayer : this.getPlugin().getServer().getOnlinePlayers()) {

                if (this.getPlugin().isPlayerBypassing(player.getUniqueId()) && !player.canSee(otherPlayer)) {

                    player.showPlayer(this.getPlugin(), otherPlayer);

                } else if (this.players.containsKey(otherPlayer.getUniqueId()) && !player.canSee(otherPlayer)) {

                    player.showPlayer(this.getPlugin(), otherPlayer);

                } else if (!this.players.containsKey(otherPlayer.getUniqueId()) && player.canSee(otherPlayer)) {

                    player.hidePlayer(this.getPlugin(), otherPlayer);

                }

            }
        }
    }

    /**
     * This task teleports spectators to the game map.
     */
    private void teleportSpectatorsTask() {
        Location loc = this.centerLocation.mutableCopy();
        loc.setWorld(this.world);

        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
            if (player.getWorld() == loc.getWorld()) continue;

            PlayerData playerData = this.getPlayers().get(player.getUniqueId());
            if (playerData != null) continue;

            player.teleport(loc);
        }

    }

    // INGAME PLAYERS TASKS

    /**
     * Cleans up ingame players.
     */
    private void playerCleanupTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.players.get(playerId);
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (playerData == null) {
                this.players.remove(playerId);
                continue;
            }

            BedwarsTeam team = this.getTeam(playerData.getTeam());

            // Cleanup when player has no team

            if (team == null) {
                this.players.remove(playerId);
                continue;
            }

            // Set alive to false when player is offline

            if (player == null) {
                playerData.setAlive(false);
                continue;
            }

        }

    }

    /**
     * Handles player alive status.
     */
    private void playerAliveStatusTask() {

        for (Player player : this.getOnlinePlayers()) {

            // Get player data
            PlayerData playerData = this.getPlayerData(player);
            if (playerData == null) continue;

            // Get team
            BedwarsTeam team = this.getTeam(playerData.getTeam());
            if (team == null) return;

            if (playerData.isAlive()) { // PLAYER IS ALIVE

                // Reset respawn countdown
                if (playerData.getRespawnCountdown() < this.respawnCountdown) {
                    playerData.setRespawnCountdown(this.respawnCountdown);
                }

                // Set gamemode to survival
                if (!this.getPlugin().isPlayerBypassing(player) && player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

            } else { // PLAYER IS DEAD

                // Set gamemode to spectator
                if (!this.getPlugin().isPlayerBypassing(player) && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                // Respawn process
                if (team.canRespawn()) { // Team still has a bed

                    if (playerData.getRespawnCountdown() > 0) {

                        player.showTitle(Title.title(
                                Component.text("DEAD", NamedTextColor.RED, TextDecoration.BOLD),
                                Component.text("You will respawn in " + playerData.getRespawnCountdown() + " seconds!", NamedTextColor.RED),
                                Title.Times.times(Duration.ZERO, Duration.ofMillis(1250), Duration.ZERO)
                        ));

                        playerData.setRespawnCountdown(playerData.getRespawnCountdown() - 1);

                    } else {
                        this.respawnPlayer(player);
                    }

                } else { // Team has no bed
                    player.sendActionBar(Component.text("You are dead", NamedTextColor.RED));
                }

            }

        }

    }

    private void playerTimerTask() {

        for (PlayerData playerData : this.players.values()) {

            Iterator<Map.Entry<String, Integer>> i = playerData.getTimers().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, Integer> entry = i.next();
                int nv = entry.getValue() - 1;

                if (nv <= 0) {
                    i.remove();
                    return;
                }

                entry.setValue(nv);
            }

        }

    }

    /**
     * Handles inventory management for ingame players.
     */
    private void inventoryTickTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;
            BedwarsTeam team = this.getTeam(playerData.getTeam());
            if (team == null) continue;
            this.inventoryTick(player, playerData, team);
        }

    }

    /**
     * Handles player values (food level, regeneration rate, ...)
     */
    private void playerValuesTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;

            // Food Level

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            if (player.getSaturation() < 20) {
                player.setSaturation(20);
            }

            // Natural Regeneration

            if (player.getSaturatedRegenRate() != 5 * 20) {
                player.setSaturatedRegenRate(5 * 20);
            }

            if (player.getUnsaturatedRegenRate() != 0) {
                player.setUnsaturatedRegenRate(0);
            }

        }

    }

    /**
     * Handles player tracker for players.
     */
    private void playerTrackerTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;
            BedwarsTeam team = this.getTeam(playerData.getTeam());
            if (team == null) continue;

            if (playerData.getTrackingTarget() != null) {
                Player trackingPlayer = this.getPlugin().getServer().getPlayer(playerData.getTrackingTarget());

                if (trackingPlayer != null) {
                    player.setCompassTarget(trackingPlayer.getCompassTarget());
                } else {
                    playerData.setTrackingTarget(null);
                }

            } else {
                player.setCompassTarget(player.getLocation());
            }

        }

    }

    /**
     * Handles tnt particles above player heads.
     */
    private void tntParticleTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;

            if (this.getConfig().optBoolean(GameConfigKeys.TNT_PARTICLES, false) && player.getInventory().contains(Material.TNT) && playerData.getMilkTimer() <= 0) {
                player.getWorld().spawnParticle(Particle.DUST, player.getLocation().clone().add(0, 2.5, 0), 20, 0, 0, 0, 1, new Particle.DustOptions(Color.RED, 1.0F));
            }

        }

    }

    // TASKS

    private void gameEndCheck() {

        if (this.winner != null) {
            this.getPlugin().nextStatus();
            return;
        }

        if (this.noWinnerEnd) {
            this.getPlugin().nextStatus();
            return;
        }

    }

    /**
     * Will hopefully be replaced soon.
     */
    @Deprecated
    private void backwardCompatibleTimeStepTask() {

        if (this.timeStep >= 20) {
            this.timeStep = 0;
        } else {
            this.timeStep++;
        }

    }

    private void timeTask() {

        if (this.time > 0) {
            this.time--;
        } else {
            this.getPlugin().nextStatus();
        }

    }

    /**
     * Cleans up scoreboards of players which are not online
     */
    private void scoreboardCleanup() {

        for (UUID playerId : this.getPlayerScoreboards().keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player == null) {
                this.playerScoreboards.remove(playerId);
            }
        }

    }

    /**
     * Runs the tick function of all generators
     */
    private void generatorTick() {

        for (Generator generator : this.getGenerators()) {
            generator.tick();
        }

    }

    /**
     * Run time actions when the time comes
     */
    private void timeActions() {

        for (TimeAction timeAction : this.getTimeActions()) {

            if (this.time <= timeAction.getData().time() && !timeAction.isCompleted()) {
                timeAction.run();
            }

        }

    }

    private void gameEndConditions() {

        List<BedwarsTeam> aliveTeams = new ArrayList<>();

        for (BedwarsTeam team : this.getTeams()) {

            if (team.isAlive()) {
                aliveTeams.add(team);
            }

        }

        if (this.getPlugin().config().optBoolean(ConfigKeys.TESTING_MODE, false)) {

            if (aliveTeams.size() == 1) {

                for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {
                    player.sendActionBar(Component.empty().append(Component.text("GAME END CONDITION TRIGGERED: ", NamedTextColor.AQUA).append(Component.text(aliveTeams.getFirst().getName(), aliveTeams.getFirst().getChatColor())).append(Component.text(" has won", NamedTextColor.AQUA))));
                }

                return;
            }

            if (aliveTeams.size() < 1) {

                for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {
                    player.sendActionBar(Component.empty().append(Component.text("GAME END CONDITION TRIGGERED: ", NamedTextColor.AQUA).append(Component.text("No team has won", NamedTextColor.RED))));
                }

                return;
            }

        } else {

            if (aliveTeams.size() == 1) {
                this.winner = aliveTeams.get(0);
                return;
            }

            if (aliveTeams.size() < 1) {
                this.noWinnerEnd = true;
                return;
            }

        }

    }

    private void cleanupManagedEntitiesTask() {
        for (ManagedEntity<?> managedEntity : this.getManagedEntities()) {

            if (managedEntity == null || managedEntity.toBeRemoved()) {
                this.managedEntities.remove(managedEntity);
                continue;
            }

        }
    }

    /**
     * @deprecated TODO: Rewrite most parts of it
     */
    @Deprecated
    private void inventoryTick(Player player, PlayerData playerData, BedwarsTeam team) {

        // Item management

        for (int slot = 0; slot < player.getInventory().getSize() + 1; slot++) {
            ItemStack item;

            if (slot < player.getInventory().getSize()) {
                item = player.getInventory().getItem(slot);
            } else {
                item = player.getItemOnCursor();
            }

            if (item == null) {
                continue;
            }

            // Replace Wool

            replaceBlockWithTeamColor(item, team);

        }

    }

    private void groupItemsTask() {
        if (!this.getConfig().optBoolean(GameConfigKeys.INVENTORY_SORT, false)) return;

        for (Player player : this.getOnlinePlayers()) {
            this.groupItemsAlgorithm(player.getInventory());
        }

    }

    /**
     * This algorithm groups item stacks to ensure that stacks are always as full as possible and always moves items in the inventory to the first available position in the inventory.<br/>
     *<br/>
     * Use case: A player has several stacks of fireballs, one of them in the hotbar, the others in the rest of the inventory.
     * Normally, these fireballs in the hotbar slot would now decrease as the player shoots them.
     * However, this algorithm always moves the fireballs from the rest of the inventory to the slot with the fireball in the hotbar, as that slot comes before the others.
     * This means that the player does not have to manually go into the inventory and move fireballs to the hotbar slot.
     * @param inventory player inventory
     */
    private void groupItemsAlgorithm(@NotNull Inventory inventory) {

        Map<ItemSimilarityKey, LinkedList<Integer>> movedSlotsMap = new HashMap<>();

        for (int firstSlotBeforeProcessing = -1; firstSlotBeforeProcessing < Math.min(inventory.getSize(), 36); firstSlotBeforeProcessing++) {
            int firstSlot = firstSlotBeforeProcessing;
            if (firstSlot == -1) firstSlot = 40; // Put offhand in front of other slots to prevent emptying the offhand slot.

            ItemStack firstItem = inventory.getItem(firstSlot);

            if (firstItem == null || firstItem.getType() == Material.AIR) continue;

            // TODO: Replace this hard-coded list with a configurable one which items are affected and which not.
            if (!(firstItem.getType().name().endsWith("WOOL") || firstItem.getType().name().endsWith("GLASS") || firstItem.getType() == Material.BLAZE_ROD || firstItem.getType() == Material.FIRE_CHARGE || firstItem.getType() == Material.SNOWBALL || firstItem.getType() == Material.ENDER_PEARL || firstItem.getType() == Material.GOLDEN_APPLE)) continue;

            if (firstItem.getAmount() >= firstItem.getMaxStackSize()) continue;

            for (int secondSlotBeforeProcessing = firstSlotBeforeProcessing + 1; secondSlotBeforeProcessing < Math.min(inventory.getSize(), 36); secondSlotBeforeProcessing++) {
                int secondSlot = secondSlotBeforeProcessing;
                if (secondSlot == -1) secondSlot = 40; // Put offhand in front of other slots to prevent emptying the offhand slot.

                ItemStack secondItem = inventory.getItem(secondSlot);

                if (secondItem == null || secondItem.getType() == Material.AIR) continue;
                if (!secondItem.isSimilar(firstItem)) continue;

                int maxMoveableItemCount = firstItem.getMaxStackSize() - firstItem.getAmount();
                int moveableItemCount = Math.min(maxMoveableItemCount, secondItem.getAmount());

                firstItem.setAmount(firstItem.getAmount() + moveableItemCount);
                secondItem.setAmount(secondItem.getAmount() - moveableItemCount);

                // Slot claiming system (moves items forward to the first slots that were completely emptied after moving the item)
                LinkedList<Integer> movedSlots = movedSlotsMap.computeIfAbsent(ItemSimilarityKey.of(firstItem), k -> new LinkedList<>());
                if (secondItem.isEmpty()) {
                    movedSlots.add(secondSlot);
                } else {

                    if (!movedSlots.isEmpty()) {
                        int firstFreeSlot = movedSlots.pop();
                        inventory.setItem(firstFreeSlot, secondItem.clone());
                        inventory.setItem(secondSlot, new ItemStack(Material.AIR));
                    }

                }

            }



        }

    }

    private void scoreboardTick(Player player, List<String> sidebar) {

        // GET SCOREBOARD

        Scoreboard scoreboard = this.playerScoreboards.get(player.getUniqueId());

        if (scoreboard == null) {
            return;
        }

        // RESET SCOREBOARD

        for (String name : List.copyOf(scoreboard.getEntries())) {
            scoreboard.resetScores(name);
        }

        // SIDEBAR

        if (scoreboard.getObjective("sidebardisplay") == null) {
            scoreboard.registerNewObjective("sidebardisplay", Criteria.DUMMY, "§6§lBEDWARS");
        }

        Objective sidebardisplay = scoreboard.getObjective("sidebardisplay");

        List<String> sidebarDisplayStrings = List.copyOf(sidebar);

        int reverseIsidebar = sidebarDisplayStrings.size();
        for (String sidebarEntry : sidebarDisplayStrings) {

            if (sidebarEntry.equalsIgnoreCase("")) {
                String paragraphs = "§";
                for (int i = 0; i < reverseIsidebar; i++) {
                    paragraphs = paragraphs + "§";
                }
                sidebardisplay.getScore(paragraphs).setScore(reverseIsidebar);
            } else {
                sidebardisplay.getScore(sidebarEntry).setScore(reverseIsidebar);
            }

            reverseIsidebar--;
        }

        if (sidebardisplay.getDisplaySlot() != DisplaySlot.SIDEBAR) {
            sidebardisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // TEAMS

        for (BedwarsTeam bedwarsTeam : this.getTeams()) {

            Team team = scoreboard.getTeam(String.valueOf(bedwarsTeam.getId()));

            if (team == null) {

                team = scoreboard.registerNewTeam(String.valueOf(bedwarsTeam.getId()));
                team.displayName(Component.text(bedwarsTeam.getName()));
                team.color(bedwarsTeam.getChatColor());
                team.setAllowFriendlyFire(false);
                team.setCanSeeFriendlyInvisibles(true);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

            }

            for (UUID teamPlayerId : bedwarsTeam.getPlayers()) {

                Player teamPlayer = this.getPlugin().getServer().getPlayer(teamPlayerId);

                if (teamPlayer == player && this.getPlugin().isPlayerBypassing(player.getUniqueId())) {

                    if (team.getEntries().contains(player.getName())) {
                        team.removeEntry(player.getName());
                    }

                    continue;
                }

                if (teamPlayer == null) {
                    continue;
                }

                if (!team.getEntries().contains(teamPlayer.getName())) {
                    team.addEntry(teamPlayer.getName());
                }

            }

        }

        Team spectatorTeam = scoreboard.getTeam("spectator");

        if (spectatorTeam == null) {

            spectatorTeam = scoreboard.registerNewTeam("spectator");
            spectatorTeam.setDisplayName("SPECTATOR");
            spectatorTeam.setColor(ChatColor.DARK_GRAY);
            spectatorTeam.setAllowFriendlyFire(false);
            spectatorTeam.setCanSeeFriendlyInvisibles(false);
            spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            spectatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        }

        for (Player teamPlayer : this.getPlugin().getServer().getOnlinePlayers()) {

            if (teamPlayer == player && this.getPlugin().isPlayerBypassing(player.getUniqueId())) {

                if (spectatorTeam.getEntries().contains(player.getName())) {
                    spectatorTeam.removeEntry(player.getName());
                }

                continue;
            }

            if (this.players.containsKey(teamPlayer.getUniqueId())) {
                continue;
            }

            if (!spectatorTeam.getEntries().contains(teamPlayer.getName())) {
                spectatorTeam.addEntry(teamPlayer.getName());
            }

        }

        // SET SCOREBOARD

        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }
    }

    public List<String> getSidebar(PlayerData playerData) {
        List<String> sidebarDisplayStrings = new ArrayList<>();

        sidebarDisplayStrings.add("");

        int timeActionCount = 0;
        for (TimeAction timeAction : this.getTimeActions()) {

            if (timeActionCount >= 2) {
                break;
            }

            if (timeAction.getScoreboardText() == null || timeAction.isCompleted()) {
                continue;
            }

            int inTime = this.time - timeAction.getData().time();

            sidebarDisplayStrings.add(timeAction.getScoreboardText() + " §rin §a" + Bedwars.getDurationFormat(inTime));

            timeActionCount++;
        }

        if (timeActionCount < 2) {
            sidebarDisplayStrings.add("Game End in §a" + Bedwars.getDurationFormat(this.time));
        }

        sidebarDisplayStrings.add("");

        for (BedwarsTeam iTeam : this.getTeams()) {

            String teamStatusIndicator = "";

            if (iTeam.isAlive()) {
                if (iTeam.hasBed() > 1) {
                    teamStatusIndicator = "§a" + iTeam.hasBed() + "§l\u2713";
                } else if (iTeam.hasBed() == 1) {
                    teamStatusIndicator = "§a§l\u2713";
                } else {
                    teamStatusIndicator = "§6" + iTeam.getPlayers().size();
                }
            } else {
                teamStatusIndicator = "§c\u274C";
            }

            if (playerData != null && iTeam == this.getTeams().get(playerData.getTeam())) {
                teamStatusIndicator = teamStatusIndicator + " §7(you)";
            }

            sidebarDisplayStrings.add(Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(iTeam.getChatColor()), ChatColor.BLACK) + iTeam.getName() + "§r: " + teamStatusIndicator);

        }

        if (playerData != null) {
            sidebarDisplayStrings.add("");
            sidebarDisplayStrings.add("Kills: §a" + playerData.getKills());
            sidebarDisplayStrings.add("Beds broken: §a" + playerData.getBedsBroken());
            sidebarDisplayStrings.add("Deaths: §a" + playerData.getDeaths());
        } else {
            sidebarDisplayStrings.add("");
            sidebarDisplayStrings.add("You are");
            sidebarDisplayStrings.add("spectator");
        }

        sidebarDisplayStrings.add("");

        return List.copyOf(sidebarDisplayStrings);
    }

    public void prepareGame() {
        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {

            if (!this.players.containsKey(player.getUniqueId())) {
                continue;
            }

            PlayerData playerData = this.players.get(player.getUniqueId());

            if (playerData.getTeam() >= this.getTeams().size()) {
                continue;
            }

            BedwarsTeam team = this.getTeams().get(playerData.getTeam());

            if (team == null) {
                continue;
            }

            player.sendMessage("§7You are in " + Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(team.getChatColor()), ChatColor.BLACK) + "Team " + team.getChatColor() + "§7.");

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);

            player.getInventory().clear();

            for (PotionEffect potionEffect : List.copyOf(player.getActivePotionEffects())) {
                player.removePotionEffect(potionEffect.getType());
            }

        }

        // CLOUDSYSTEM MODE

        if (this.getPlugin().isCloudSystemMode()) {

            // Custom command

            String customCommand = this.getConfig().optString(ConfigKeys.CLOUDSYSTEM_INGAME_COMMAND, "");

            if (customCommand != null && !customCommand.isEmpty()) {
                this.getPlugin().getServer().dispatchCommand(this.getPlugin().getServer().getConsoleSender(), customCommand);
            }

            // CloudNet ingame state

            if (this.getPlugin().config().optBoolean(ConfigKeys.INTEGRATION_CLOUDNET, false)) {

                try {

                    try {
                        Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
                        Class.forName("eu.cloudnetservice.modules.bridge.BridgeServiceHelper");

                        BridgeServiceHelper bridgeServiceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);

                        if (bridgeServiceHelper != null) {
                            bridgeServiceHelper.changeToIngame();
                            this.getPlugin().getLogger().info("Changed server to ingame state (CloudNet)");
                        }
                    } catch (ClassNotFoundException ignored) {
                        // ignored (cloudnet not installed)
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        this.prepared = true;
    }

    @Override
    public GamePart getNextStatus() {

        for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {

            if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
                continue;
            }

            player.getInventory().clear();

        }

        return new Endlobby(this.getPlugin(), this);
    }

    // EVENTS

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerData playerData = this.getPlayer(event.getPlayer().getUniqueId());
        if (playerData == null) return;

        Location location = event.getPlayer().getLocation();

        if (location.getY() < -64) {
            location.setY(-64);
        }

        event.setRespawnLocation(location);

    }

    // UTILITIES

    public boolean respawnPlayer(Player player) {

        if (player == null || !this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        PlayerData playerData = this.getPlayerData(player);
        if (playerData == null) return false;

        GamePlayerRespawnEvent event = new GamePlayerRespawnEvent(this, player, playerData);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        playerData.setAlive(true);
        player.teleport(WorldUtils.locationWithWorld(this.teams.get(playerData.getTeam()).getRandomSpawnpoint(), this.getWorld()));
        player.setGameMode(GameMode.SURVIVAL);
        player.resetTitle();

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);

        player.getInventory().clear();

        playerData.setFireballCooldown(0);
        playerData.setTrapCooldown(0);

        player.sendMessage("§bRespawning...");

        return true;
    }

    public boolean addPlayer(UUID playerId, int team) {
        Player player = this.getPlugin().getServer().getPlayer(playerId);

        if (player != null) {
            player.getInventory().clear();
        }

        return this.players.put(playerId, new PlayerData(this, team)) != null;
    }

    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    /**
     * Returns a copy of the internal player data map.
     * @return player data map
     */
    public final @NotNull Map<UUID, PlayerData> getPlayerDataMap() {
        return Map.copyOf(this.players);
    }

    /**
     * Returns an unmodifiable set of the registered players.
     * @return registered players
     */
    public final @NotNull Set<UUID> getRegisteredPlayers() {
        return Map.copyOf(this.players).keySet();
    }

    /**
     * Returns the player data of the player with the specified uuid.
     * @param playerId player uuid
     * @return player data
     */
    public final PlayerData getPlayerData(@Nullable UUID playerId) {
        if (playerId == null) return null;
        return this.players.get(playerId);
    }

    /**
     * Returns the player data of the specified player.
     * @param player player
     * @return player data
     */
    public final PlayerData getPlayerData(@Nullable OfflinePlayer player) {
        if (player == null) return null;
        return this.getPlayerData(player.getUniqueId());
    }

    /**
     * Returns the player data map.
     * @return player data map
     * @deprecated Use {@link #getPlayerDataMap()}
     */
    @Deprecated(forRemoval = true)
    public Map<UUID, PlayerData> getPlayers() {
        return this.getPlayerDataMap();
    }

    /**
     * Returns the player data of the specified player uuid.
     * @param playerId player uuid
     * @return player data
     * @deprecated Use {@link #getPlayerData(UUID)}
     */
    @Deprecated(forRemoval = true)
    public PlayerData getPlayer(UUID playerId) {
        return this.getPlayerData(playerId);
    }

    /**
     * Returns if the player with the specified uuid is ingame.
     * @param playerId player uuid
     * @return ingame
     */
    public final boolean isPlayerIngame(@Nullable UUID playerId) {
        if (playerId == null) return false;
        return this.players.containsKey(playerId);
    }

    public World getWorld() {
        return this.world;
    }

    /**
     * Returns the map name.
     * @return map name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the respawn countdown.
     * @return respawn countdown
     */
    public int getRespawnCountdown() {
        return respawnCountdown;
    }

    /**
     * Returns the max time.
     * @return max time
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * Returns the spawn block place protection radius.
     * @return spawn block place protection radius
     */
    public int getSpawnBlockPlaceProtection() {
        return spawnBlockPlaceProtection;
    }

    /**
     * Returns the villager block place protection radius.
     * @return villager block place protection radius
     */
    public int getVillagerBlockPlaceProtection() {
        return villagerBlockPlaceProtection;
    }

    /**
     * Returns the map center location.
     * @return center location
     */
    public @NotNull ImmutableLocation getCenterLocation() {
        return centerLocation;
    }

    /**
     * Returns the map radius.
     * @return map radius
     */
    public int getMapRadius() {
        return mapRadius;
    }

    public List<BedwarsTeam> getTeams() {
        return List.copyOf(this.teams);
    }

    public BedwarsTeam getTeam(int id) {

        if (id < this.teams.size()) {
            return this.teams.get(id);
        } else {
            return null;
        }

    }

    public List<Generator> getGenerators() {
        return List.copyOf(this.generators);
    }

    public int getTime() {
        return this.time;
    }

    public int getPublicEmeraldGeneratorLevel() {
        return this.publicEmeraldGeneratorLevel;
    }

    public void setPublicEmeraldGeneratorLevel(int publicEmeraldGeneratorLevel) {
        this.publicEmeraldGeneratorLevel = publicEmeraldGeneratorLevel;
    }

    public int getPublicDiamondGeneratorLevel() {
        return this.publicDiamondGeneratorLevel;
    }

    public void setPublicDiamondGeneratorLevel(int publicDiamondGeneratorLevel) {
        this.publicDiamondGeneratorLevel = publicDiamondGeneratorLevel;
    }

    public List<TimeAction> getTimeActions() {
        return List.copyOf(this.timeActions);
    }

    public BlockProtectionSystem getBlockProtectionSystem() {
        return this.blockProtectionSystem;
    }

    @Deprecated(forRemoval = true)
    public Set<Location> getPlayerPlacedBlocks() {
        return this.blockProtectionSystem.getPlayerPlacedBlocks().stream().map(vector -> vector.toLocation(this.world)).collect(Collectors.toSet());
    }

    public Map<UUID, Scoreboard> getPlayerScoreboards() {
        return Map.copyOf(this.playerScoreboards);
    }

    /**
     * Returns the item shop.
     * @return item shop
     */
    public @NotNull ItemShop getItemShop() {
        return this.itemShop;
    }

    /**
     * Returns the shop gui.
     * @return shop gui
     */
    public @NotNull ShopGUI getShopGUI() {
        return this.shopGUI;
    }

    /**
     * Returns the player upgrade manager.
     * @return player upgrade manager
     */
    public @NotNull PlayerUpgradeManager getPlayerUpgradeManager() {
        return this.playerUpgradeManager;
    }

    /**
     * Returns the team upgrade manager.
     * @return team upgrade manager
     */
    public @NotNull TeamUpgradeManager getTeamUpgradeManager() {
        return teamUpgradeManager;
    }

    /**
     * Returns the team gui.
     * @return team gui
     */
    public @NotNull TeamGUI getTeamGUI() {
        return this.teamGUI;
    }

    /**
     * Returns the team trap manager
     * @return TeamTrapManager
     */
    public @NotNull TeamTrapManager getTeamTrapManager() {
        return this.teamTrapManager;
    }

    public Location buildLocationWithWorld(Location old) {
        return new Location(
                this.world,
                old.getX(),
                old.getY(),
                old.getZ(),
                old.getYaw(),
                old.getPitch()
        );
    }

    private int getUpgradeLevel(int level, List<Integer> levels) {

        if (level <= 0) {
            return 0;
        }

        if (level - 1 < levels.size()) {
            return levels.get(level - 1);
        } else {
            return levels.get(levels.size() - 1);
        }

    }

    public void setTime(int time) {
        this.time = time;
    }

    // MANAGED ENTITIES

    /**
     * Returns a list of managed entities.
     * @return list of managed entities
     */
    public List<ManagedEntity<?>> getManagedEntities() {
        return List.copyOf(this.managedEntities).stream()
                .filter(managedEntity -> !managedEntity.toBeRemoved())
                .toList();
    }

    /**
     * Returns the managed entity of the specified entity.
     * Returns null if the entity is not a managed entity.
     * @param entity entity
     * @return {@link ManagedEntity} or null
     */
    public ManagedEntity<?> getManagedEntityByEntity(Entity entity) {
        for (ManagedEntity<?> managedEntity : this.getManagedEntities()) {
            if (managedEntity.toBeRemoved()) continue;

            if (managedEntity.getEntity() == entity) {
                return managedEntity;
            }

        }

        return null;
    }

    /**
     * Adds a new managed entity.
     * @param managedEntity {@link ManagedEntity}
     */
    public void addManagedEntity(ManagedEntity<?> managedEntity) {
        this.managedEntities.add(managedEntity);
    }

    /**
     * Removes a managed entity.
     * @param managedEntity {@link ManagedEntity}
     */
    public void removeManagedEntity(BaseDefender managedEntity) {
        this.managedEntities.remove(managedEntity);
    }

    //

    public boolean isNoWinnerEnd() {
        return this.noWinnerEnd;
    }

    public BedwarsTeam getWinner() {
        return this.winner;
    }

    public void stopGame() {
        this.winner = null;
        this.noWinnerEnd = true;
        this.getPlugin().nextStatus();
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    public static void replaceBlockWithTeamColor(ItemStack item, BedwarsTeam team) {

        String[] array = item.getType().name().split("_");
        String typeSuffix = array[array.length - 1];

        if (!typeSuffix.equals("WOOL") && !typeSuffix.equals("GLASS")) {
            return;
        }

        if (typeSuffix.equals("GLASS")) {
            typeSuffix = "STAINED_GLASS";
        }

        String blockColor = Bedwars.getBlockColorString(Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(team.getChatColor()), ChatColor.BLACK));

        if (blockColor == null) {
            return;
        }

        Material material = Material.getMaterial(blockColor + "_" + typeSuffix);

        if (material == null) {
            return;
        }

        if (item.getType() == material) {
            return;
        }

        item.setType(material);

    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().equals("shopgui")) return;
        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().openInventory(shopGUI.getInventory(event.getPlayer(), 0));
            }
        }.runTask(this.getPlugin());
    }

}
