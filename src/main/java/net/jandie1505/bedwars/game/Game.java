package net.jandie1505.bedwars.game;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.GameStatus;
import net.jandie1505.bedwars.game.generators.Generator;
import net.jandie1505.bedwars.game.generators.PublicGenerator;
import net.jandie1505.bedwars.game.generators.TeamGenerator;
import net.jandie1505.bedwars.game.menu.shop.ArmorConfig;
import net.jandie1505.bedwars.game.menu.shop.ItemShop;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.game.timeactions.DestroyBedsAction;
import net.jandie1505.bedwars.game.timeactions.DiamondGeneratorUpgradeAction;
import net.jandie1505.bedwars.game.timeactions.EmeraldGeneratorUpgradeAction;
import net.jandie1505.bedwars.game.timeactions.TimeAction;
import net.jandie1505.bedwars.lobby.setup.LobbyDestroyBedsTimeActionData;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorData;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorUpgradeTimeActionData;
import net.jandie1505.bedwars.lobby.setup.LobbyTeamData;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.json.JSONObject;

import java.util.*;

public class Game implements GamePart {
    private final Bedwars plugin;
    private final World world;
    private final List<BedwarsTeam> teams;
    private final Map<UUID, PlayerData> players;
    private final List<Generator> generators;
    private final List<TimeAction> timeActions;
    private final int respawnCountdown;
    private final List<Location> playerPlacedBlocks;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final ItemShop itemShop;
    private final ArmorConfig armorConfig;
    private final TeamUpgradesConfig teamUpgradesConfig;
    private final int maxTime;
    private final int spawnBlockPlaceProtection;
    private final int villagerBlockPlaceProtection;
    private int timeStep;
    private int time;
    private int publicEmeraldGeneratorLevel;
    private int publicDiamondGeneratorLevel;
    private boolean prepared;

    public Game(Bedwars plugin, World world, List<LobbyTeamData> teams, List<LobbyGeneratorData> generators, List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions, List<LobbyDestroyBedsTimeActionData> bedDestroyTimeActions, JSONObject shopConfig, ArmorConfig armorConfig, TeamUpgradesConfig teamUpgradesConfig, int respawnCountdown, int maxTime, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection) {
        this.plugin = plugin;
        this.world = world;
        this.teams = Collections.synchronizedList(new ArrayList<>());
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.generators = Collections.synchronizedList(new ArrayList<>());
        this.timeActions = Collections.synchronizedList(new ArrayList<>());
        this.respawnCountdown = respawnCountdown;
        this.playerPlacedBlocks = Collections.synchronizedList(new ArrayList<>());
        this.playerScoreboards = Collections.synchronizedMap(new HashMap<>());
        this.itemShop = new ItemShop(this);
        this.armorConfig = armorConfig;
        this.teamUpgradesConfig = teamUpgradesConfig;
        this.maxTime = maxTime;
        this.spawnBlockPlaceProtection = spawnBlockPlaceProtection;
        this.villagerBlockPlaceProtection = villagerBlockPlaceProtection;
        this.time = this.maxTime;
        this.publicEmeraldGeneratorLevel = 0;
        this.publicDiamondGeneratorLevel = 0;
        this.prepared = false;

        for (LobbyTeamData teamData : List.copyOf(teams)) {
            BedwarsTeam team = new BedwarsTeam(this, teamData);

            this.teams.add(team);

            for (LobbyGeneratorData generatorData : teamData.getGenerators()) {
                this.generators.add(new TeamGenerator(
                        this,
                        generatorData.getItem(),
                        this.buildLocationWithWorld(generatorData.getLocation()),
                        team,
                        generatorData.getUpgradeSteps()
                ));
            }
        }

        for (LobbyGeneratorData generatorData : generators) {
            this.generators.add(new PublicGenerator(
                    this,
                    generatorData.getItem(),
                    this.buildLocationWithWorld(generatorData.getLocation()),
                    generatorData.getUpgradeSteps()
            ));
        }

        for (LobbyGeneratorUpgradeTimeActionData generatorUpgradeData : generatorUpgradeTimeActions) {

            if (generatorUpgradeData.getGeneratorType() == 1) {
                this.timeActions.add(new DiamondGeneratorUpgradeAction(this, generatorUpgradeData.getTime(), "§aDiamond Generators §ehave beed upgraded to §aLevel " + (generatorUpgradeData.getLevel() + 1), "Diamond " + (generatorUpgradeData.getLevel() + 1), generatorUpgradeData.getLevel()));
            } else if (generatorUpgradeData.getGeneratorType() == 2) {
                this.timeActions.add(new EmeraldGeneratorUpgradeAction(this, generatorUpgradeData.getTime(), "§aEmerald Generators §ehave beed upgraded to §aLevel " + (generatorUpgradeData.getLevel() + 1), "Emerald " + (generatorUpgradeData.getLevel() + 1), generatorUpgradeData.getLevel()));
            }

        }

        for (LobbyDestroyBedsTimeActionData destroyBedsData : bedDestroyTimeActions) {
            this.timeActions.add(new DestroyBedsAction(this, destroyBedsData.getTime(), destroyBedsData.isDisableBeds()));
        }

        Collections.sort(this.timeActions);

        for (BedwarsTeam team : this.getTeams()) {

            for (Location location : team.getShopVillagerLocations()) {
                this.spawnItemShopVillager(team, this.buildLocationWithWorld(location));
            }

            for (Location location : team.getUpgradesVillagerLocations()) {
                this.spawnUpgradesVillager(team, this.buildLocationWithWorld(location));
            }

        }

        this.itemShop.initEntries(shopConfig);
    }

    @Override
    public GameStatus tick() {

        // PREPARE GAME

        if (!this.prepared) {
            this.prepareGame();
        }

        // STOP IF WORLD NOT LOADED

        if (this.world == null || !this.plugin.getServer().getWorlds().contains(this.world)) {
            this.plugin.getLogger().warning("Bedwars game end because world is not loaded");
            return GameStatus.ABORT;
        }

        // PLAYER MANAGEMENT

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {

            // Is Ingame

            boolean isIngame = this.players.containsKey(player.getUniqueId());

            // Scoreboard

            if (!this.playerScoreboards.containsKey(player.getUniqueId())) {
                this.playerScoreboards.put(player.getUniqueId(), this.plugin.getServer().getScoreboardManager().getNewScoreboard());
            }

            if (this.timeStep >= 1) {
                this.scoreboardTick(
                        player,
                        this.getSidebar(this.players.get(player.getUniqueId()))
                );
            }

            // Game mode

            if (!isIngame && !this.plugin.isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            }

            // Set player visibility

            for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {

                if (this.plugin.isPlayerBypassing(player.getUniqueId()) && !player.canSee(otherPlayer)) {

                    player.showPlayer(otherPlayer);

                } else if (this.players.containsKey(otherPlayer) && !player.canSee(otherPlayer)) {

                    player.showPlayer(otherPlayer);

                } else if (!this.players.containsKey(otherPlayer) && player.canSee(otherPlayer)) {

                    player.hidePlayer(otherPlayer);

                }

            }

            // Continue if player is not ingame

            if (!isIngame) {
                continue;
            }

            PlayerData playerData = this.players.get(player.getUniqueId());

            // Check player for invalid values

            BedwarsTeam team = null;
            try {
                team = this.teams.get(playerData.getTeam());
            } catch (IndexOutOfBoundsException ignored) {
                // Player is getting removed when exception is thrown because teamData will then be null
            }

            if (team == null) {
                this.players.remove(player.getUniqueId());
                continue;
            }

            // Player respawn

            if (playerData.isAlive()) {

                if (playerData.getRespawnCountdown() != this.respawnCountdown) {
                    playerData.setRespawnCountdown(this.respawnCountdown);
                }

                if (!this.plugin.isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

            } else {

                if (!this.plugin.isPlayerBypassing(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (this.timeStep >= 1) {
                    if (playerData.getRespawnCountdown() > 0) {

                        player.sendTitle("§c§lDEAD", "§7§lYou will respawn in " + playerData.getRespawnCountdown() + " seconds", 0, 20, 0);
                        player.sendMessage("§7Respawn in " + playerData.getRespawnCountdown() + " seconds");

                        playerData.setRespawnCountdown(playerData.getRespawnCountdown() - 1);

                    } else {

                        this.respawnPlayer(player);

                    }
                }

            }

            // Inventory

            this.inventoryTick(player, playerData, team);

            // Haste Team Upgrade

            int hasteUpgradeLevel = this.getUpgradeLevel(team.getHasteUpgrade(), this.teamUpgradesConfig.getHasteUpgrade().getUpgradeLevels());

            if (hasteUpgradeLevel > 0) {

                if (player.getPotionEffect(PotionEffectType.FAST_DIGGING) == null || player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier() != hasteUpgradeLevel - 1) {
                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3600 * 20, hasteUpgradeLevel - 1));
                }

            } else {

                if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                }

            }

            // Regeneration

            int healPoolUpgrade = this.getUpgradeLevel(team.getHealPoolUpgrade(), this.teamUpgradesConfig.getHealPoolUpgrade().getUpgradeLevels());

            if (healPoolUpgrade > 0 && Bedwars.getBlockDistance(team.getSpawnpoints().get(0), player.getLocation()) <= team.getBaseRadius() && !player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, healPoolUpgrade - 1));
            }

            // Saturation

            if (!player.hasPotionEffect(PotionEffectType.SATURATION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3600 * 20, 255, false, false));
            }

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

        // SCOREBOARDS CLEANUP

        for (UUID playerId : this.getPlayerScoreboards().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.playerScoreboards.remove(playerId);
            }
        }

        // GENERATORS

        for (Generator generator : this.getGenerators()) {
            generator.tick();
        }

        // TIME ACTIONS (PUBLIC GENERATOR UPGRADES)

        if (this.timeStep >= 1) {

            for (TimeAction timeAction : this.getTimeActions()) {

                if (this.time <= timeAction.getTime() && !timeAction.isCompleted()) {
                    timeAction.execute();
                }

            }

        }

        // TEAM VILLAGER MANAGEMENT

        for (BedwarsTeam team : this.getTeams()) {

            boolean shopVillagerExists = false;
            boolean upgradesVillagerExists = false;

            for (Villager villager : List.copyOf(this.world.getEntitiesByClass(Villager.class))) {

                if (villager.getScoreboardTags().contains("shop.team." + team.getId())) {
                    shopVillagerExists = true;
                }

                if (villager.getScoreboardTags().contains("upgrades.team." + team.getId())) {
                    upgradesVillagerExists = true;
                }

                if (shopVillagerExists && upgradesVillagerExists) {
                    break;
                }

            }

            if (!shopVillagerExists && !team.getShopVillagerLocations().isEmpty()) {
                this.spawnItemShopVillager(team, team.getShopVillagerLocations().get(0));
            }

            if (!upgradesVillagerExists && !team.getUpgradesVillagerLocations().isEmpty()) {
                this.spawnUpgradesVillager(team, team.getUpgradesVillagerLocations().get(0));
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

        // TIME STEP

        if (this.timeStep >= 1) {
            this.timeStep = 0;
        } else {
            this.timeStep = 1;
        }

        return GameStatus.NORMAL;
    }

    private void inventoryTick(Player player, PlayerData playerData, BedwarsTeam team) {

        // Item management

        boolean inventoryPickaxeUpgradeMissing = true;
        boolean inventoryShearsUpgradeMissing = true;

        for (int slot = 0; slot < player.getInventory().getSize() + 1; slot++) {
            ItemStack item;

            if (slot < player.getInventory().getSize()) {
                item = player.getInventory().getItem(slot);
            } else {
                item = player.getItemOnCursor();
            }

            int itemId = this.plugin.getItemStorage().getItemId(item);

            if (itemId < 0) {
                continue;
            }

            // Sharpness Team Upgrade

            if (item != null && (item.getType().toString().endsWith("SWORD") || item.getType().toString().endsWith("AXE"))) {

                if (item.getItemMeta() == null) {
                    item.setItemMeta(this.plugin.getServer().getItemFactory().getItemMeta(item.getType()));
                }

                int enchantmentLevel = 0;

                if (team.getAttackDamageUpgrade() > 0 && team.getAttackDamageUpgrade() - 1 < this.teamUpgradesConfig.getSharpnessUpgrade().getUpgradeLevels().size()) {
                    enchantmentLevel = this.teamUpgradesConfig.getSharpnessUpgrade().getUpgradeLevels().get(team.getAttackDamageUpgrade() - 1);
                }

                if (enchantmentLevel > 0) {

                    Integer level = item.getItemMeta().getEnchants().get(Enchantment.DAMAGE_ALL);

                    if (level == null || level != enchantmentLevel) {
                        ItemMeta meta = item.getItemMeta();
                        meta.addEnchant(Enchantment.DAMAGE_ALL, enchantmentLevel, true);
                        item.setItemMeta(meta);
                    }

                } else {

                    if (item.getItemMeta().getEnchants().containsKey(Enchantment.DAMAGE_ALL)) {
                        ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchantment.DAMAGE_ALL);
                        item.setItemMeta(meta);
                    }

                }

            }

            // Protection Team Upgrade

            if (item != null && this.plugin.getItemStorage().isArmorItem(item)) {

                if (item.getItemMeta() == null) {
                    item.setItemMeta(this.plugin.getServer().getItemFactory().getItemMeta(item.getType()));
                }

                int enchantmentLevel = 0;

                if (team.getProtectionUpgrade() > 0 && team.getProtectionUpgrade() - 1 < this.teamUpgradesConfig.getProtectionUpgrade().getUpgradeLevels().size()) {
                    enchantmentLevel = this.teamUpgradesConfig.getProtectionUpgrade().getUpgradeLevels().get(team.getProtectionUpgrade() - 1);
                }

                if (enchantmentLevel > 0) {

                    Integer level = item.getItemMeta().getEnchants().get(Enchantment.PROTECTION_ENVIRONMENTAL);

                    if (level == null || level != enchantmentLevel) {
                        ItemMeta meta = item.getItemMeta();
                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel, true);
                        item.setItemMeta(meta);
                    }

                } else {

                    if (item.getItemMeta().getEnchants().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                        ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
                        item.setItemMeta(meta);
                    }

                }

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

            // Boots

            int bootsItemId;

            if (playerData.getArmorUpgrade() > 0) {
                bootsItemId = this.itemShop.getArmorUpgrade().getItemId(playerData.getArmorUpgrade());
            } else {
                bootsItemId = this.armorConfig.getDefaultBoots();
            }

            if (this.plugin.getItemStorage().getItemId(player.getInventory().getBoots()) != bootsItemId) {

                ItemStack item = this.plugin.getItemStorage().getItem(bootsItemId);

                if (item != null) {

                    if (item.getItemMeta() instanceof LeatherArmorMeta) {
                        item = this.plugin.getItemStorage().colorArmor(item, team.getColor());
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

            if (this.plugin.getItemStorage().getItemId(player.getInventory().getLeggings()) != leggingsItemId) {

                ItemStack item = this.plugin.getItemStorage().getItem(leggingsItemId);

                if (item != null) {

                    if (leggingsItemId != this.armorConfig.getDefaultLeggings()) {
                        item = this.plugin.getItemStorage().copyItemMeta(item, this.plugin.getItemStorage().getArmorPiece(item.getType(), 2));
                    }

                    if (item.getItemMeta() instanceof LeatherArmorMeta) {
                        item = this.plugin.getItemStorage().colorArmor(item, team.getColor());
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

            if (this.plugin.getItemStorage().getItemId(player.getInventory().getChestplate()) != chestplateItemId) {

                ItemStack item = this.plugin.getItemStorage().getItem(chestplateItemId);

                if (item != null) {

                    if (chestplateItemId != this.armorConfig.getDefaultChestplate()) {
                        item = this.plugin.getItemStorage().copyItemMeta(item, this.plugin.getItemStorage().getArmorPiece(item.getType(), 1));
                    }

                    if (item.getItemMeta() instanceof LeatherArmorMeta) {
                        item = this.plugin.getItemStorage().colorArmor(item, team.getColor());
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

            if (this.plugin.getItemStorage().getItemId(player.getInventory().getHelmet()) != helmetItemId) {

                ItemStack item = this.plugin.getItemStorage().getItem(helmetItemId);

                if (item != null) {

                    if (helmetItemId != this.armorConfig.getDefaultHelmet()) {
                        item = this.plugin.getItemStorage().copyItemMeta(item, this.plugin.getItemStorage().getArmorPiece(item.getType(), 0));
                    }

                    if (item.getItemMeta() instanceof LeatherArmorMeta) {
                        item = this.plugin.getItemStorage().colorArmor(item, team.getColor());
                    }

                    player.getInventory().setHelmet(item);
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
                team.setDisplayName(bedwarsTeam.getName());
                team.setColor(bedwarsTeam.getChatColor());
                team.setAllowFriendlyFire(false);
                team.setCanSeeFriendlyInvisibles(true);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

            }

            for (UUID teamPlayerId : bedwarsTeam.getPlayers()) {

                Player teamPlayer = this.plugin.getServer().getPlayer(teamPlayerId);

                if (teamPlayer == player && this.plugin.isPlayerBypassing(player.getUniqueId())) {

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

        for (Player teamPlayer : this.plugin.getServer().getOnlinePlayers()) {

            if (teamPlayer == player && this.plugin.isPlayerBypassing(player.getUniqueId())) {

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

            int inTime = this.time - timeAction.getTime();

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

            sidebarDisplayStrings.add(iTeam.getChatColor() + iTeam.getName() + "§r: " + teamStatusIndicator);

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
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {

            if (!this.players.containsKey(player.getUniqueId())) {
                continue;
            }

            PlayerData playerData = this.players.get(player.getUniqueId());
            BedwarsTeam team = this.getTeams().get(playerData.getTeam());

            if (team == null) {
                continue;
            }

            player.sendMessage("§7You are in " + team.getChatColor() + "Team " + team.getChatColor().name() + "§7.");

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);

            player.getInventory().clear();

            for (PotionEffect potionEffect : List.copyOf(player.getActivePotionEffects())) {
                player.removePotionEffect(potionEffect.getType());
            }

        }

        this.prepared = true;
    }

    @Override
    public GamePart getNextStatus() {
        return null;
    }

    public boolean respawnPlayer(Player player) {

        if (player == null || !this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        PlayerData playerData = this.players.get(player.getUniqueId());

        playerData.setAlive(true);
        player.teleport(this.teams.get(playerData.getTeam()).getRandomSpawnpoint());
        player.resetTitle();

        player.sendMessage("§bRespawning...");

        return true;
    }

    public void spawnItemShopVillager(BedwarsTeam team, Location location) {
        Villager villager = (Villager) this.world.spawnEntity(location, EntityType.VILLAGER);
        villager.teleport(location);
        villager.setAI(false);
        villager.setCustomName("§6§lITEM SHOP");
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.addScoreboardTag("shop.team." + team.getId());
    }

    public void spawnUpgradesVillager(BedwarsTeam team, Location location) {
        Villager villager = (Villager) this.world.spawnEntity(location, EntityType.VILLAGER);
        villager.teleport(location);
        villager.setAI(false);
        villager.setCustomName("§b§lTEAM UPGRADES");
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.addScoreboardTag("upgrades.team." + team.getId());
    }

    public boolean addPlayer(UUID playerId, int team) {
        return this.players.put(playerId, new PlayerData(team)) != null;
    }

    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public Map<UUID, PlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }

    public World getWorld() {
        return this.world;
    }

    public List<BedwarsTeam> getTeams() {
        return List.copyOf(this.teams);
    }

    public List<Generator> getGenerators() {
        return List.copyOf(this.generators);
    }

    public int getTime() {
        return this.time;
    }

    public int getMaxTime() {
        return this.maxTime;
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

    public List<Location> getPlayerPlacedBlocks() {
        return this.playerPlacedBlocks;
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

    public int getSpawnBlockPlaceProtection() {
        return this.spawnBlockPlaceProtection;
    }

    public int getVillagerBlockPlaceProtection() {
        return this.villagerBlockPlaceProtection;
    }

    public int getRespawnCountdown() {
        return this.respawnCountdown;
    }

    public ArmorConfig getArmorConfig() {
        return this.armorConfig;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
