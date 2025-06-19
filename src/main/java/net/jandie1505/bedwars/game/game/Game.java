package net.jandie1505.bedwars.game.game;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.endlobby.Endlobby;
import net.jandie1505.bedwars.game.game.entities.base.ManagedEntity;
import net.jandie1505.bedwars.game.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.game.entities.entities.ShopVillager;
import net.jandie1505.bedwars.game.game.entities.entities.UpgradeVillager;
import net.jandie1505.bedwars.game.game.generators.Generator;
import net.jandie1505.bedwars.game.game.generators.GeneratorData;
import net.jandie1505.bedwars.game.game.generators.PublicGenerator;
import net.jandie1505.bedwars.game.game.generators.TeamGenerator;
import net.jandie1505.bedwars.game.game.listeners.*;
import net.jandie1505.bedwars.game.game.menu.shop.old.ArmorConfig;
import net.jandie1505.bedwars.game.game.menu.shop.old.ItemShop;
import net.jandie1505.bedwars.game.game.menu.shop.ItemShopNew;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.TeamData;
import net.jandie1505.bedwars.game.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.game.game.team.traps.BedwarsTrap;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeActionData;
import net.jandie1505.bedwars.game.game.timeactions.provider.TimeActionCreator;
import net.jandie1505.bedwars.game.game.world.BlockProtectionSystem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Game extends GamePart implements ManagedListener {
    private final World world;
    private final MapData data;
    private final List<BedwarsTeam> teams;
    private final Map<UUID, PlayerData> players;
    private final List<Generator> generators;
    private final List<TimeAction> timeActions;
    private final TimeActionCreator timeActionCreator;
    private final BlockProtectionSystem blockProtectionSystem;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final ItemShop itemShop;
    private final ArmorConfig armorConfig;
    private final TeamUpgradesConfig teamUpgradesConfig;
    private final List<ManagedEntity<?>> managedEntities;
    private final ItemShopNew itemShopNew;
    private int timeStep;
    private int time;
    private int publicEmeraldGeneratorLevel;
    private int publicDiamondGeneratorLevel;
    private boolean prepared;
    private BedwarsTeam winner;
    private boolean noWinnerEnd;

    public Game(Bedwars plugin, World world, MapData data, JSONObject shopConfig, ArmorConfig armorConfig, TeamUpgradesConfig teamUpgradesConfig) {
        super(plugin);
        this.world = world;
        this.data = data;
        this.teams = Collections.synchronizedList(new ArrayList<>());
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.generators = Collections.synchronizedList(new ArrayList<>());
        this.timeActions = Collections.synchronizedList(new ArrayList<>());
        this.timeActionCreator = new TimeActionCreator(this);
        this.blockProtectionSystem = new BlockProtectionSystem(this);
        this.playerScoreboards = Collections.synchronizedMap(new HashMap<>());
        this.itemShop = new ItemShop(this);
        this.armorConfig = armorConfig;
        this.teamUpgradesConfig = teamUpgradesConfig;
        this.managedEntities = Collections.synchronizedList(new ArrayList<>());
        this.itemShopNew = new ItemShopNew(this);
        this.time = this.data.maxTime();
        this.publicEmeraldGeneratorLevel = 0;
        this.publicDiamondGeneratorLevel = 0;
        this.prepared = false;
        this.winner = null;
        this.noWinnerEnd = false;

        for (TeamData teamData : List.copyOf(this.data.teams())) {
            BedwarsTeam team = new BedwarsTeam(this, teamData);

            this.teams.add(team);

            for (GeneratorData generatorData : teamData.generators()) {
                this.generators.add(new TeamGenerator(
                        this,
                        generatorData,
                        team
                ));
            }
        }

        for (GeneratorData generatorData : this.data.globalGenerators()) {
            this.generators.add(new PublicGenerator(
                    this,
                    generatorData
            ));
        }

        for (TimeActionData timeActionData : this.data.timeActions()) {
            try {
                TimeAction timeAction = this.timeActionCreator.createTimeAction(timeActionData);
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

        for (BedwarsTeam team : this.getTeams()) {

            for (Location location : team.getData().shopVillagerLocations()) {
                new ShopVillager(this, WorldUtils.locationWithWorld(location, this.getWorld()), team.getId());
            }

            for (Location location : team.getData().upgradeVillagerLocations()) {
                new UpgradeVillager(this, WorldUtils.locationWithWorld(location, this.getWorld()), team.getId());
            }

        }

        this.itemShop.initEntries(shopConfig);

        this.world.getWorldBorder().setCenter(this.data.centerLocation());
        this.world.getWorldBorder().setSize(this.data.mapRadius() * 2);

        this.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

        // PREPARE GAME

        this.getTaskScheduler().runTaskLater(this::prepareGame, 1, "prepare_game");

        // TASKS

        this.getTaskScheduler().scheduleRepeatingTask(this::playerScoreboardTask, 1, 20, "player_scoreboards");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerGameModeTask, 1, 1, "player_gamemodes");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerVisibilityTask, 1, 1, "player_visibility");
        this.getTaskScheduler().scheduleRepeatingTask(this::teleportSpectatorsTask, 1, 20, "player_teleport_spectators");

        this.getTaskScheduler().scheduleRepeatingTask(this::playerCleanupTask, 1, 20, "ingame_player_cleanup");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerAliveStatusTask, 1, 20, "ingame_player_alive_status");
        this.getTaskScheduler().scheduleRepeatingTask(this::inventoryTickTask, 1, 1, "ingame_player_inventory");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerTeamUpgradeTask, 1, 20, "ingame_player_team_upgrades");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerValuesTask, 1, 200, "ingame_player_values");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerCooldownTask, 1, 1, "ingame_player_cooldowns");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerTrackerTask, 1, 100, "ingame_player_player_tracker");
        this.getTaskScheduler().scheduleRepeatingTask(this::tntParticleTask, 1, 20, "ingame_player_tnt_particles");

        this.getTaskScheduler().scheduleRepeatingTask(this::scoreboardCleanup, 1, 1, "scoreboard_cleanup");
        this.getTaskScheduler().scheduleRepeatingTask(this::generatorTick, 1, 1, "generators");
        this.getTaskScheduler().scheduleRepeatingTask(this::timeActions, 1, 20, "time_actions");
        this.getTaskScheduler().scheduleRepeatingTask(this::cleanupManagedEntitiesTask, 1, 10*20, "cleanup_managed_entities");
        this.getTaskScheduler().scheduleRepeatingTask(this::traps, 1, 1, "traps");
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
        this.getTaskScheduler().runTaskLater(() -> this.getPlugin().getListenerManager().manageListeners(), 2, "listener_reload_on_start");
    }

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
        Location loc = this.data.centerLocation().mutableCopy();
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

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;
            BedwarsTeam team = this.getTeam(playerData.getTeam());
            if (team == null) continue;

            // Check if alive or not

            if (playerData.isAlive()) {

                // PLAYER IS ALIVE

                // Set respawn countdown to default

                if (playerData.getRespawnCountdown() != this.data.respawnCountdown()) {
                    playerData.setRespawnCountdown(this.data.respawnCountdown());
                }

                // Set gamemode to survival

                if (!this.getPlugin().isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

            } else {

                // PLAYER IS NOT ALIVE

                // Set spectator

                if (!this.getPlugin().isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                // Respawn process

                if (team.hasBed() > 0) {

                    // TEAM STILL HAS BED

                    // Count down or respawn if countdown is 0
                    if (playerData.getRespawnCountdown() > 0) {

                        player.sendTitle("§c§lDEAD", "§7§lYou will respawn in " + playerData.getRespawnCountdown() + " seconds", 0, 25, 0);
                        player.sendMessage("§7Respawn in " + playerData.getRespawnCountdown() + " seconds");

                        playerData.setRespawnCountdown(playerData.getRespawnCountdown() - 1);

                    } else {

                        this.respawnPlayer(player);

                    }

                } else {

                    // TEAM HAS NO BED

                    // Display you are dead message, no respawn
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You are dead"));

                }

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
     * Handles team upgrade behaviors on players.
     */
    private void playerTeamUpgradeTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;
            BedwarsTeam team = this.getTeam(playerData.getTeam());
            if (team == null) continue;

            // Heal Pool Upgrade

            int healPoolUpgrade = this.getUpgradeLevel(team.getHealPoolUpgrade(), this.teamUpgradesConfig.getHealPoolUpgrade().getUpgradeLevels());

            if (healPoolUpgrade > 0 && Bedwars.getBlockDistance(WorldUtils.locationWithWorld(team.getData().baseCenter(), this.getWorld()), player.getLocation()) <= team.getData().baseRadius() && !player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, healPoolUpgrade - 1));
            }

            // Haste Team Upgrade

            int hasteUpgradeLevel = this.getUpgradeLevel(team.getHasteUpgrade(), this.teamUpgradesConfig.getHasteUpgrade().getUpgradeLevels());

            if (hasteUpgradeLevel > 0) {

                if (player.getPotionEffect(PotionEffectType.HASTE) == null || player.getPotionEffect(PotionEffectType.HASTE).getAmplifier() != hasteUpgradeLevel - 1) {
                    player.removePotionEffect(PotionEffectType.HASTE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 3600 * 20, hasteUpgradeLevel - 1));
                }

            } else {

                if (player.hasPotionEffect(PotionEffectType.HASTE)) {
                    player.removePotionEffect(PotionEffectType.HASTE);
                }

            }

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
     * Handles player item cooldowns.
     */
    private void playerCooldownTask() {

        for (UUID playerId : this.getPlayers().keySet()) {
            PlayerData playerData = this.getPlayer(playerId);
            if (playerData == null) continue;
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player == null) continue;

            // Fireball Cooldown

            if (playerData.getFireballCooldown() > 0) {
                playerData.setFireballCooldown(playerData.getFireballCooldown() - 1);
            }

            // Trap Cooldown

            if (playerData.getTrapCooldown() > 0) {
                playerData.setTrapCooldown(playerData.getTrapCooldown() - 1);
            }

            // milk timer

            if (playerData.getMilkTimer() > 0) {
                playerData.setMilkTimer(playerData.getMilkTimer() - 1);
            }

            // Iron golem timer

            if (playerData.getIronGolemCooldown() > 0) {
                playerData.setIronGolemCooldown(playerData.getIronGolemCooldown() - 1);
            }

            // Zapper Cooldown

            if (playerData.getZapperCooldown() > 0) {
                playerData.setZapperCooldown(playerData.getZapperCooldown() - 1);
            }

            // Black Hole Cooldown

            if(playerData.getBlackHoleCooldown() > 0) {
                playerData.setBlackHoleCooldown(playerData.getBlackHoleCooldown() - 1);
            }

            // Teleport to Base Cooldown

            if (playerData.getTeleportToBaseCooldown() > 0) {

                if (playerData.getTeleportToBaseCooldown() == 1) {
                    BedwarsTeam team = this.getTeam(playerData.getTeam());
                    Location teleportLocation = team.getRandomSpawnpoint();
                    player.teleport(teleportLocation);
                }

                playerData.setTeleportToBaseCooldown(playerData.getTeleportToBaseCooldown() - 1);
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

            if (this.getPlugin().getConfigManager().getConfig().optBoolean("tntParticles", false) && player.getInventory().contains(Material.TNT) && playerData.getMilkTimer() <= 0) {
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

    private void traps() {

        for (BedwarsTeam team : this.getTeams()) {

            team.shiftTraps();

            if (team.hasPrimaryTraps()) {

                List<Entity> entitiesInRadius = List.copyOf(this.world.getNearbyEntities(team.getData().baseCenter(), team.getData().baseRadius(), team.getData().baseRadius(), team.getData().baseRadius()));

                for (Entity entity : entitiesInRadius) {

                    if(!(entity instanceof Player)) {
                        continue;
                    }

                    Player player = (Player) entity;
                    PlayerData playerData = this.players.get(player.getUniqueId());

                    if (playerData == null) {
                        continue;
                    }

                    if (!playerData.isAlive()) {
                        continue;
                    }

                    if (playerData.getTrapCooldown() > 0) {
                        continue;
                    }

                    if (playerData.getMilkTimer() > 0) {
                        return;
                    }

                    if (team.getPlayers().contains(player.getUniqueId())) {
                        continue;
                    }

                    for (int i = 0; i < team.getPrimaryTraps().length; i++) {
                        BedwarsTrap trap = team.getPrimaryTraps()[i];

                        if (trap == null) {
                            continue;
                        }

                        trap.trigger(player);
                        playerData.setTrapCooldown(30*20);
                        team.getPrimaryTraps()[i] = null;

                    }

                    break;
                }

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

        if (this.getPlugin().getConfigManager().getConfig().optBoolean("testingMode", false)) {

            if (aliveTeams.size() == 1) {

                for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b§lGAME END CONDITION TRIGGERED: §r" + aliveTeams.get(0).getData().chatColor() + "Team " + aliveTeams.get(0).getData().name() + " §bhas won"));
                }

                return;
            }

            if (aliveTeams.size() < 1) {

                for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b§lGAME END CONDITION TRIGGERED: §r§cNo team has won"));
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

    private void inventoryTick(Player player, PlayerData playerData, BedwarsTeam team) {

        // Item management

        boolean inventoryDefaultSwordMissing = true;
        boolean inventoryPickaxeUpgradeMissing = true;
        boolean inventoryShearsUpgradeMissing = true;

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

            // Group Items

            if (this.getPlugin().getConfigManager().getConfig().optBoolean("inventorySort", false)) {

                if ((item.getType().name().endsWith("WOOL") || item.getType().name().endsWith("GLASS") || item.getType() == Material.BLAZE_ROD || item.getType() == Material.FIRE_CHARGE || item.getType() == Material.SNOWBALL || item.getType() == Material.ENDER_PEARL || item.getType() == Material.GOLDEN_APPLE) && slot < player.getInventory().getSize() && this.timeStep >= 20) {

                    for (int slot2 = 0; slot2 < player.getInventory().getSize(); slot2++) {
                        ItemStack item2 = player.getInventory().getItem(slot2);

                        // Slot must not be offhand

                        if (slot == 40) {
                            continue;
                        }

                        // Target slot must be lower (or the offhand slot) than the origin slot

                        if (slot <= slot2 && slot2 != 40) {
                            continue;
                        }

                        // item must not be null

                        if (item2 == null) {
                            continue;
                        }

                        //  items must be similar

                        if (!item.isSimilar(item2)) {
                            continue;
                        }

                        for (int amount = 0; amount < item.getAmount(); amount++) {

                            if (item2.getAmount() >= item2.getMaxStackSize()) {
                                break;
                            }

                            if (item.getAmount() <= 0) {
                                break;
                            }

                            item2.setAmount(item2.getAmount() + 1);
                            item.setAmount(item.getAmount() - 1);

                        }

                    }

                }

            }

            // item ids

            int itemId = this.getPlugin().getItemStorage().getItemId(item);

            if (itemId < 0) {
                continue;
            }

            // Default Sword Condition

            if ((item.getType().toString().endsWith("SWORD") || (item.getType().toString().endsWith("AXE") && !item.getType().toString().endsWith("PICKAXE"))) && this.itemShop.getDefaultWeapon() != null && itemId != this.itemShop.getDefaultWeapon()) {
                inventoryDefaultSwordMissing = false;
            } else if (this.itemShop.getDefaultWeapon() != null && itemId == this.itemShop.getDefaultWeapon()) {
                inventoryDefaultSwordMissing = false;
            }

            // Sharpness Team Upgrade

            if (item != null && (item.getType().toString().endsWith("SWORD") || item.getType().toString().endsWith("AXE"))) {

                if (item.getItemMeta() == null) {
                    item.setItemMeta(this.getPlugin().getServer().getItemFactory().getItemMeta(item.getType()));
                }

                int enchantmentLevel = 0;

                if (team.getAttackDamageUpgrade() > 0 && team.getAttackDamageUpgrade() - 1 < this.teamUpgradesConfig.getSharpnessUpgrade().getUpgradeLevels().size()) {
                    enchantmentLevel = this.teamUpgradesConfig.getSharpnessUpgrade().getUpgradeLevels().get(team.getAttackDamageUpgrade() - 1);
                }

                if (enchantmentLevel > 0) {

                    Integer level = item.getItemMeta().getEnchants().get(Enchantment.SHARPNESS);

                    if (level == null || level != enchantmentLevel) {
                        ItemMeta meta = item.getItemMeta();
                        meta.addEnchant(Enchantment.SHARPNESS, enchantmentLevel, true);
                        item.setItemMeta(meta);
                    }

                } else {

                    if (item.getItemMeta().getEnchants().containsKey(Enchantment.SHARPNESS)) {
                        ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchantment.SHARPNESS);
                        item.setItemMeta(meta);
                    }

                }

            }

            // Protection Team Upgrade

            if (item != null && this.getPlugin().getItemStorage().isArmorItem(item)) {

                if (item.getItemMeta() == null) {
                    item.setItemMeta(this.getPlugin().getServer().getItemFactory().getItemMeta(item.getType()));
                }

                int enchantmentLevel = 0;

                if (team.getProtectionUpgrade() > 0 && team.getProtectionUpgrade() - 1 < this.teamUpgradesConfig.getProtectionUpgrade().getUpgradeLevels().size()) {
                    enchantmentLevel = this.teamUpgradesConfig.getProtectionUpgrade().getUpgradeLevels().get(team.getProtectionUpgrade() - 1);
                }

                if (enchantmentLevel > 0) {

                    Integer level = item.getItemMeta().getEnchants().get(Enchantment.PROTECTION);

                    if (level == null || level != enchantmentLevel) {
                        ItemMeta meta = item.getItemMeta();
                        meta.addEnchant(Enchantment.PROTECTION, enchantmentLevel, true);
                        item.setItemMeta(meta);
                    }

                } else {

                    if (item.getItemMeta().getEnchants().containsKey(Enchantment.PROTECTION)) {
                        ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchantment.PROTECTION);
                        item.setItemMeta(meta);
                    }

                }

            }

            // Default Sword

            if (this.itemShop.getDefaultWeapon() != null && itemId == this.itemShop.getDefaultWeapon()) {

                for (int slot2 = 0; slot2 < player.getInventory().getSize(); slot2 ++) {
                    ItemStack item2 = player.getInventory().getItem(slot2);

                    if (item2 == null) {
                        continue;
                    }

                    int item2Id = this.getPlugin().getItemStorage().getItemId(item2);

                    if (item2Id < 0) {
                        continue;
                    }

                    if ((item2.getType().toString().endsWith("SWORD") || (item2.getType().toString().endsWith("AXE") && !item2.getType().toString().endsWith("PICKAXE"))) && item2Id != itemId) {
                        Bedwars.removeItemCompletely(player.getInventory(), item);
                        break;
                    }

                }

                continue;
            }

            // Pickaxe Player Upgrade

            if (this.itemShop.getPickaxeUpgrade() != null && this.itemShop.getPickaxeUpgrade().getUpgradeItemIds().contains(itemId)) {

                if (this.itemShop.getPickaxeUpgrade().getItemId(playerData.getPickaxeUpgrade()) == itemId) {
                    inventoryPickaxeUpgradeMissing = false;
                    continue;
                }

                Bedwars.removeItemCompletely(player.getInventory(), item);

                if (slot >= player.getInventory().getSize()) {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                }

                continue;
            }

            // Shears Player Upgrade

            if (this.itemShop.getShearsUpgrade() != null && this.itemShop.getShearsUpgrade().getUpgradeItemIds().contains(itemId)) {

                if (this.itemShop.getShearsUpgrade().getItemId(playerData.getShearsUpgrade()) == itemId) {
                    inventoryShearsUpgradeMissing = false;
                    continue;
                }

                Bedwars.removeItemCompletely(player.getInventory(), item);

                if (slot >= player.getInventory().getSize()) {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                }

                continue;
            }

        }

        // Default Sword

        if (this.itemShop.getDefaultWeapon() != null && this.itemShop.getDefaultWeaponItem() != null && inventoryDefaultSwordMissing) {
            player.getInventory().addItem(this.itemShop.getDefaultWeaponItem());
        }

        // Pickaxe Player Upgrade

        if (this.itemShop.getPickaxeUpgrade() != null && playerData.getPickaxeUpgrade() > 0 && inventoryPickaxeUpgradeMissing) {
            ItemStack item = this.itemShop.getPickaxeUpgrade().getItem(playerData.getPickaxeUpgrade());

            if (item != null) {
                player.getInventory().addItem(item);
            }
        }

        // Shears Player Upgrade

        if (this.itemShop.getShearsUpgrade() != null && playerData.getShearsUpgrade() > 0 && inventoryShearsUpgradeMissing) {
            ItemStack item = this.itemShop.getShearsUpgrade().getItem(playerData.getShearsUpgrade());

            if (item != null) {
                player.getInventory().addItem(item);
            }
        }

        // Armor management

        if (this.armorConfig.isEnableArmorSystem() && this.itemShop.getArmorUpgrade() != null) {

            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.getInventory().setBoots(new ItemStack(Material.AIR));
                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
            } else {

                // Boots

                int bootsItemId;

                if (playerData.getArmorUpgrade() > 0) {
                    bootsItemId = this.itemShop.getArmorUpgrade().getItemId(playerData.getArmorUpgrade());
                } else {
                    bootsItemId = this.armorConfig.getDefaultBoots();
                }

                if (this.getPlugin().getItemStorage().getItemId(player.getInventory().getBoots()) != bootsItemId) {

                    ItemStack item = this.getPlugin().getItemStorage().getItem(bootsItemId);

                    if (item != null) {

                        if (item.getItemMeta() instanceof LeatherArmorMeta) {
                            item = this.getPlugin().getItemStorage().colorArmor(item, team.getData().color());
                        }

                        player.getInventory().setBoots(item);
                    }

                }

                // Leggings

                int leggingsItemId;

                if (this.armorConfig.isCopyLeggings() && playerData.getArmorUpgrade() > 0) {
                    leggingsItemId = this.itemShop.getArmorUpgrade().getItemId(playerData.getArmorUpgrade());
                } else {
                    leggingsItemId = this.armorConfig.getDefaultLeggings();
                }

                if (this.getPlugin().getItemStorage().getItemId(player.getInventory().getLeggings()) != leggingsItemId) {

                    ItemStack item = this.getPlugin().getItemStorage().getItem(leggingsItemId);

                    if (item != null) {

                        if (leggingsItemId != this.armorConfig.getDefaultLeggings()) {
                            item = this.getPlugin().getItemStorage().copyItemMeta(item, this.getPlugin().getItemStorage().getArmorPiece(item.getType(), 2));
                        }

                        if (item.getItemMeta() instanceof LeatherArmorMeta) {
                            item = this.getPlugin().getItemStorage().colorArmor(item, team.getData().color());
                        }

                        player.getInventory().setLeggings(item);
                    }

                }

                // Chestplate

                int chestplateItemId;

                if (this.armorConfig.isCopyChestplate() && playerData.getArmorUpgrade() > 0) {
                    chestplateItemId = this.itemShop.getArmorUpgrade().getItemId(playerData.getArmorUpgrade());
                } else {
                    chestplateItemId = this.armorConfig.getDefaultChestplate();
                }

                if (this.getPlugin().getItemStorage().getItemId(player.getInventory().getChestplate()) != chestplateItemId) {

                    ItemStack item = this.getPlugin().getItemStorage().getItem(chestplateItemId);

                    if (item != null) {

                        if (chestplateItemId != this.armorConfig.getDefaultChestplate()) {
                            item = this.getPlugin().getItemStorage().copyItemMeta(item, this.getPlugin().getItemStorage().getArmorPiece(item.getType(), 1));
                        }

                        if (item.getItemMeta() instanceof LeatherArmorMeta) {
                            item = this.getPlugin().getItemStorage().colorArmor(item, team.getData().color());
                        }

                        player.getInventory().setChestplate(item);
                    }

                }

                // Helmet

                int helmetItemId;

                if (this.armorConfig.isCopyHelmet() && playerData.getArmorUpgrade() > 0) {
                    helmetItemId = this.itemShop.getArmorUpgrade().getItemId(playerData.getArmorUpgrade());
                } else {
                    helmetItemId = this.armorConfig.getDefaultHelmet();
                }

                if (this.getPlugin().getItemStorage().getItemId(player.getInventory().getHelmet()) != helmetItemId) {

                    ItemStack item = this.getPlugin().getItemStorage().getItem(helmetItemId);

                    if (item != null) {

                        if (helmetItemId != this.armorConfig.getDefaultHelmet()) {
                            item = this.getPlugin().getItemStorage().copyItemMeta(item, this.getPlugin().getItemStorage().getArmorPiece(item.getType(), 0));
                        }

                        if (item.getItemMeta() instanceof LeatherArmorMeta) {
                            item = this.getPlugin().getItemStorage().colorArmor(item, team.getData().color());
                        }

                        player.getInventory().setHelmet(item);
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
                team.setDisplayName(bedwarsTeam.getData().name());
                team.setColor(bedwarsTeam.getData().chatColor());
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

            sidebarDisplayStrings.add(iTeam.getData().chatColor() + iTeam.getData().name() + "§r: " + teamStatusIndicator);

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

            player.sendMessage("§7You are in " + team.getData().chatColor() + "Team " + team.getData().chatColor().name() + "§7.");

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

            String customCommand = this.getPlugin().getConfigManager().getConfig().optJSONObject("cloudSystemMode", new JSONObject()).optString("switchToIngameCommand", "");

            if (!customCommand.equalsIgnoreCase("")) {
                this.getPlugin().getServer().dispatchCommand(this.getPlugin().getServer().getConsoleSender(), customCommand);
            }

            // CloudNet ingame state

            if (this.getPlugin().getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("cloudnet", false)) {

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

        PlayerData playerData = this.players.get(player.getUniqueId());

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

    public MapData getData() {
        return this.data;
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

    public ItemShop getItemShop() {
        return this.itemShop;
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

    public TeamUpgradesConfig getTeamUpgradesConfig() {
        return this.teamUpgradesConfig;
    }

    public ArmorConfig getArmorConfig() {
        return this.armorConfig;
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

    public ItemShopNew getItemShopNew() {
        return itemShopNew;
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

        String blockColor = Bedwars.getBlockColorString(team.getData().chatColor());

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
}
