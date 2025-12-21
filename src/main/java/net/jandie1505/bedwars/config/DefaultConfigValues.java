package net.jandie1505.bedwars.config;

import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.MiscUtils;
import net.jandie1505.bedwars.constants.ConfigKeys;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.constants.GameConfigKeys;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.ArmorUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.UpgradableItemUpgrade;
import net.jandie1505.bedwars.game.game.shop.entries.QuickBuyMenuEntry;
import net.jandie1505.bedwars.game.game.shop.entries.ShopGUIPosition;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.team.gui.TeamGUI;
import net.jandie1505.bedwars.game.game.team.traps.constants.TeamTraps;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.jandie1505.bedwars.game.game.team.upgrades.types.EnchantmentTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.HealPoolTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.PermanentPotionEffectTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.TeamChestUpgrade;
import net.jandie1505.bedwars.game.game.timeactions.actions.DestroyBedsAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.EndgameWitherTimeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.GeneratorUpgradeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.WorldborderChangeTimeAction;
import net.jandie1505.bedwars.game.lobby.constants.LobbyConfigKeys;
import net.jandie1505.datastorage.DataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public final class DefaultConfigValues {
    private DefaultConfigValues() {}

    public static final Component LORE = Component.empty()
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

    public static DataStorage getConfig() {
        DataStorage config = new DataStorage();

        config.set(ConfigKeys.TESTING_MODE, false);

        config.set(ConfigKeys.INTEGRATION_CLOUDNET, false);
        config.set(ConfigKeys.INTEGRATION_SUPERVANISH, true);
        config.set(ConfigKeys.INTEGRATION_PARTY_AND_FRIENDS, true);
        config.set(ConfigKeys.INTEGRATION_PLAYERPOINTS, false);
        config.set(ConfigKeys.INTEGRATION_PLAYERLEVELS, false);

        config.set(ConfigKeys.CLOUDSYSTEM_ENABLE, false);
        config.set(ConfigKeys.CLOUDSYSTEM_INGAME_COMMAND, "");

        config.set(LobbyConfigKeys.section(LobbyConfigKeys.SLOT_SYSTEM_TEAM_COUNT), -1);
        config.set(LobbyConfigKeys.section(LobbyConfigKeys.SLOT_SYSTEM_PLAYERS_PER_TEAM), -1);

        config.set(LobbyConfigKeys.section(LobbyConfigKeys.CHAT_ADD_PREFIX_SUFFIX), false);

        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_VICTORY), 500);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_BED_DESTROYED), 100);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_PLAYER_KILL), 20);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_TEAM_UPGRADE_PURCHASE), 10);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_TEAM_TRAP_PURCHASE), 5);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_PLAYER_UPGRADE_PURCHASE), 2);
        config.set(GameConfigKeys.section(GameConfigKeys.REWARD_LIMIT), 5000);

        config.set(GameConfigKeys.section(GameConfigKeys.TNT_PARTICLES), true);
        config.set(GameConfigKeys.section(GameConfigKeys.INVENTORY_SORT), true);

        return config;
    }

    public static @NotNull JSONObject getLobbyConfig() {
        JSONObject lobbyConfig = new JSONObject();

        lobbyConfig.put("mapVoting", true);
        lobbyConfig.put("requiredPlayers", 2);
        lobbyConfig.put("mapVoteButton", 1);
        lobbyConfig.put("teamSelectionButton", 2);
        lobbyConfig.put("mapButton", 171);

        JSONObject lobbyBorder = new JSONObject();
        lobbyBorder.put("enable", false);
        lobbyBorder.put("x1", -9);
        lobbyBorder.put("y1", -60);
        lobbyBorder.put("z1", -9);
        lobbyBorder.put("x2", 9);
        lobbyBorder.put("y2", 255);
        lobbyBorder.put("z2", 9);
        lobbyConfig.put("border", lobbyBorder);

        JSONObject lobbySpawnpoint = new JSONObject();
        lobbySpawnpoint.put("x", 0.5);
        lobbySpawnpoint.put("y", 0.0);
        lobbySpawnpoint.put("z", 0.5);
        lobbySpawnpoint.put("yaw", -90);
        lobbySpawnpoint.put("pitch", 0);
        lobbyConfig.put("spawnpoint", lobbySpawnpoint);

        return lobbyConfig;
    }

    public static @NotNull JSONObject getExampleMap() {
        JSONObject minimalistMap = new JSONObject();

        minimalistMap.put("name", "Minimalist");
        minimalistMap.put("world", "minimalist");
        minimalistMap.put("respawnCooldown", 5);
        minimalistMap.put("maxTime", 3600);
        minimalistMap.put("spawnBlockPlaceProtectionRadius", 1);
        minimalistMap.put("villagerBlockPlaceProtectionRadius", 1);
        minimalistMap.put("mapRadius", 82);

        // Center location

        JSONObject centerLocation = new JSONObject();

        centerLocation.put("x", 0.5);
        centerLocation.put("y", 0);
        centerLocation.put("z", 0.5);

        minimalistMap.put("center", centerLocation);

        // TEAMS

        JSONArray minimalistMapTeams = new JSONArray();

        // Team Generator Speed

        JSONArray ironGeneratorSpeed = new JSONArray();
        ironGeneratorSpeed.put(15);
        ironGeneratorSpeed.put(10);
        ironGeneratorSpeed.put(5);
        ironGeneratorSpeed.put(2);
        ironGeneratorSpeed.put(1);

        JSONArray goldGeneratorSpeed = new JSONArray();
        goldGeneratorSpeed.put(10*20);
        goldGeneratorSpeed.put(5*20);
        goldGeneratorSpeed.put(2.5*20);
        goldGeneratorSpeed.put(20);
        goldGeneratorSpeed.put(10);

        JSONArray teamEmeraldGeneratorSpeed = new JSONArray();
        teamEmeraldGeneratorSpeed.put(0);
        teamEmeraldGeneratorSpeed.put(0);
        teamEmeraldGeneratorSpeed.put(0);
        teamEmeraldGeneratorSpeed.put(45*20);
        teamEmeraldGeneratorSpeed.put(30*20);

        // Green Team Create

        JSONObject greenTeamData = new JSONObject();

        greenTeamData.put("name", "Green");
        greenTeamData.put("color", Color.LIME.asRGB());
        greenTeamData.put("chatColor", "GREEN");
        greenTeamData.put("baseRadius", 15);

        // Green Base Center

        JSONObject greenTeamBaseCenter = new JSONObject();
        greenTeamBaseCenter.put("x", 54.5);
        greenTeamBaseCenter.put("y", 1.0);
        greenTeamBaseCenter.put("z", 0.5);
        greenTeamData.put("baseCenter", greenTeamBaseCenter);

        // Green Spawnpoints

        JSONArray greenTeamSpawnpoints = new JSONArray();

        JSONObject greenTeamSpawnpoint = new JSONObject();
        greenTeamSpawnpoint.put("x", 54.5);
        greenTeamSpawnpoint.put("y", 1.0);
        greenTeamSpawnpoint.put("z", 0.5);
        greenTeamSpawnpoint.put("yaw", 90.0);
        greenTeamSpawnpoint.put("pitch", 0.0);
        greenTeamSpawnpoints.put(greenTeamSpawnpoint);

        greenTeamData.put("spawnpoints", greenTeamSpawnpoints);

        // Green Bed Locations

        JSONArray greenTeamBedLocations = new JSONArray();

        JSONObject greenTeamBedLocation = new JSONObject();
        greenTeamBedLocation.put("x", 44);
        greenTeamBedLocation.put("y", 1);
        greenTeamBedLocation.put("z", 0);
        greenTeamBedLocations.put(greenTeamBedLocation);

        greenTeamData.put("bedLocations", greenTeamBedLocations);

        // Green Generators

        JSONArray greenTeamGenerators = new JSONArray();

        JSONObject greenTeamIronGenerator = new JSONObject();

        JSONObject greenTeamIronGeneratorLocation = new JSONObject();
        greenTeamIronGeneratorLocation.put("x", 54.5);
        greenTeamIronGeneratorLocation.put("y", 1);
        greenTeamIronGeneratorLocation.put("z", -6.5);
        greenTeamIronGenerator.put("location", greenTeamIronGeneratorLocation);

        greenTeamIronGenerator.put("material", Material.IRON_INGOT.toString());
        greenTeamIronGenerator.put("speed", ironGeneratorSpeed);

        greenTeamGenerators.put(greenTeamIronGenerator);

        JSONObject greenTeamGoldGenerator = new JSONObject();

        JSONObject greenTeamGoldGeneratorLocation = new JSONObject();
        greenTeamGoldGeneratorLocation.put("x", 54.5);
        greenTeamGoldGeneratorLocation.put("y", 1);
        greenTeamGoldGeneratorLocation.put("z", 7.5);
        greenTeamGoldGenerator.put("location", greenTeamGoldGeneratorLocation);

        greenTeamGoldGenerator.put("material", Material.GOLD_INGOT.toString());
        greenTeamGoldGenerator.put("speed", goldGeneratorSpeed);

        greenTeamGenerators.put(greenTeamGoldGenerator);

        JSONObject greemTeamEmeraldGenerator = new JSONObject();

        greemTeamEmeraldGenerator.put("location", greenTeamGoldGeneratorLocation);
        greemTeamEmeraldGenerator.put("material", Material.EMERALD.toString());
        greemTeamEmeraldGenerator.put("speed", teamEmeraldGeneratorSpeed);

        greenTeamGenerators.put(greemTeamEmeraldGenerator);

        greenTeamData.put("generators", greenTeamGenerators);

        // Green Shop Villager Locations

        JSONArray greenTeamShopVillagerLocations = new JSONArray();

        JSONObject greenTeamShopVillagerLocation = new JSONObject();
        greenTeamShopVillagerLocation.put("x", 60.5);
        greenTeamShopVillagerLocation.put("y", 1);
        greenTeamShopVillagerLocation.put("z", -2.5);
        greenTeamShopVillagerLocation.put("yaw", 90.0);
        greenTeamShopVillagerLocation.put("pitch", 0);
        greenTeamShopVillagerLocations.put(greenTeamShopVillagerLocation);

        greenTeamData.put("shopVillagers", greenTeamShopVillagerLocations);

        // Green Upgrade Villager Locations

        JSONArray greenTeamUpgradeVillagerLocations = new JSONArray();

        JSONObject greenTeamUpgradeVillagerLocation = new JSONObject();
        greenTeamUpgradeVillagerLocation.put("x", 60.5);
        greenTeamUpgradeVillagerLocation.put("y", 1);
        greenTeamUpgradeVillagerLocation.put("z", 3.5);
        greenTeamUpgradeVillagerLocation.put("yaw", 90.0);
        greenTeamUpgradeVillagerLocation.put("pitch", 0);

        greenTeamUpgradeVillagerLocations.put(greenTeamUpgradeVillagerLocation);

        greenTeamData.put("upgradeVillagers", greenTeamUpgradeVillagerLocations);

        // Add Green Team

        minimalistMapTeams.put(greenTeamData);

        // Red Team Create

        JSONObject redTeamData = new JSONObject();

        redTeamData.put("name", "Red");
        redTeamData.put("color", Color.RED.asRGB());
        redTeamData.put("chatColor", "RED");
        redTeamData.put("baseRadius", 15);

        // Red BaseCenter

        JSONObject redTeamBaseCenter = new JSONObject();
        redTeamBaseCenter.put("x", -53.5);
        redTeamBaseCenter.put("y", 1.0);
        redTeamBaseCenter.put("z", 0.5);
        redTeamData.put("baseCenter", redTeamBaseCenter);

        // Red Spawnpoints

        JSONArray redTeamSpawnpoints = new JSONArray();

        JSONObject redTeamSpawnpoint = new JSONObject();
        redTeamSpawnpoint.put("x", -53.5);
        redTeamSpawnpoint.put("y", 1.0);
        redTeamSpawnpoint.put("z", 0.5);
        redTeamSpawnpoint.put("yaw", -90.0);
        redTeamSpawnpoint.put("pitch", 0.0);
        redTeamSpawnpoints.put(redTeamSpawnpoint);

        redTeamData.put("spawnpoints", redTeamSpawnpoints);

        // Red Bed Locations

        JSONArray redTeamBedLocations = new JSONArray();

        JSONObject redTeamBedLocation = new JSONObject();
        redTeamBedLocation.put("x", -44);
        redTeamBedLocation.put("y", 1);
        redTeamBedLocation.put("z", 0);
        redTeamBedLocations.put(redTeamBedLocation);

        redTeamData.put("bedLocations", redTeamBedLocations);

        // Red Generators

        JSONArray redTeamGenerators = new JSONArray();

        JSONObject redTeamIronGenerator = new JSONObject();

        JSONObject redTeamIronGeneratorLocation = new JSONObject();
        redTeamIronGeneratorLocation.put("x", -53.5);
        redTeamIronGeneratorLocation.put("y", 1);
        redTeamIronGeneratorLocation.put("z", 7.5);
        redTeamIronGenerator.put("location", redTeamIronGeneratorLocation);

        redTeamIronGenerator.put("material", Material.IRON_INGOT.toString());
        redTeamIronGenerator.put("speed", ironGeneratorSpeed);

        redTeamGenerators.put(redTeamIronGenerator);

        JSONObject redTeamGoldGenerator = new JSONObject();

        JSONObject redTeamGoldGeneratorLocation = new JSONObject();
        redTeamGoldGeneratorLocation.put("x", -53.5);
        redTeamGoldGeneratorLocation.put("y", 1);
        redTeamGoldGeneratorLocation.put("z", -6.5);
        redTeamGoldGenerator.put("location", redTeamGoldGeneratorLocation);

        redTeamGoldGenerator.put("material", Material.GOLD_INGOT.toString());
        redTeamGoldGenerator.put("speed", goldGeneratorSpeed);

        redTeamGenerators.put(redTeamGoldGenerator);

        JSONObject redTeamEmeraldGenerator = new JSONObject();

        redTeamEmeraldGenerator.put("location", redTeamGoldGeneratorLocation);
        redTeamEmeraldGenerator.put("material", Material.EMERALD.toString());
        redTeamEmeraldGenerator.put("speed", teamEmeraldGeneratorSpeed);

        redTeamGenerators.put(redTeamEmeraldGenerator);

        redTeamData.put("generators", redTeamGenerators);

        // Red Shop Villager Locations

        JSONArray redTeamShopVillagerLocations = new JSONArray();

        JSONObject redTeamShopVillagerLocation = new JSONObject();
        redTeamShopVillagerLocation.put("x", -59.5);
        redTeamShopVillagerLocation.put("y", 1);
        redTeamShopVillagerLocation.put("z", 3.5);
        redTeamShopVillagerLocation.put("yaw", -90.0);
        redTeamShopVillagerLocation.put("pitch", 0);
        redTeamShopVillagerLocations.put(redTeamShopVillagerLocation);

        redTeamData.put("shopVillagers", redTeamShopVillagerLocations);

        // Red Upgrade Villager Locations

        JSONArray redTeamUpgradeVillagerLocations = new JSONArray();

        JSONObject redTeamUpgradeVillagerLocation = new JSONObject();
        redTeamUpgradeVillagerLocation.put("x", -59.5);
        redTeamUpgradeVillagerLocation.put("y", 1);
        redTeamUpgradeVillagerLocation.put("z", -2.5);
        redTeamUpgradeVillagerLocation.put("yaw", -90.0);
        redTeamUpgradeVillagerLocation.put("pitch", 0);

        redTeamUpgradeVillagerLocations.put(redTeamUpgradeVillagerLocation);

        redTeamData.put("upgradeVillagers", redTeamUpgradeVillagerLocations);

        // Add Red Team

        minimalistMapTeams.put(redTeamData);

        // Add teams to map

        minimalistMap.put("teams", minimalistMapTeams);

        // Global Generator Speed

        JSONArray emeraldGeneratorSpeed = new JSONArray();
        emeraldGeneratorSpeed.put(40*20);
        emeraldGeneratorSpeed.put(35*20);
        emeraldGeneratorSpeed.put(30*20);
        emeraldGeneratorSpeed.put(20*20);
        emeraldGeneratorSpeed.put(10*20);
        emeraldGeneratorSpeed.put(5*20);

        JSONArray diamondGeneratorSpeed = new JSONArray();
        diamondGeneratorSpeed.put(30*20);
        diamondGeneratorSpeed.put(25*20);
        diamondGeneratorSpeed.put(20*20);
        diamondGeneratorSpeed.put(10*20);
        diamondGeneratorSpeed.put(5*20);
        diamondGeneratorSpeed.put(3*20);

        // Global Generators

        JSONArray globalGenerators = new JSONArray();

        JSONObject firstGlobalEmeraldGenerator = new JSONObject();

        JSONObject firstGlobalEmeraldGeneratorLocations = new JSONObject();
        firstGlobalEmeraldGeneratorLocations.put("x", 8.5);
        firstGlobalEmeraldGeneratorLocations.put("y", 1);
        firstGlobalEmeraldGeneratorLocations.put("z", 8.5);
        firstGlobalEmeraldGenerator.put("location", firstGlobalEmeraldGeneratorLocations);

        firstGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());
        firstGlobalEmeraldGenerator.put("speed", emeraldGeneratorSpeed);

        globalGenerators.put(firstGlobalEmeraldGenerator);

        JSONObject secondGlobalEmeraldGenerator = new JSONObject();

        JSONObject secondGlobalEmeraldGeneratorLocations = new JSONObject();
        secondGlobalEmeraldGeneratorLocations.put("x", 8.5);
        secondGlobalEmeraldGeneratorLocations.put("y", 1);
        secondGlobalEmeraldGeneratorLocations.put("z", -7.5);
        secondGlobalEmeraldGenerator.put("location", secondGlobalEmeraldGeneratorLocations);

        secondGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());
        secondGlobalEmeraldGenerator.put("speed", emeraldGeneratorSpeed);

        globalGenerators.put(secondGlobalEmeraldGenerator);

        JSONObject thirdGlobalEmeraldGenerator = new JSONObject();

        JSONObject thirdGlobalEmeraldGeneratorLocations = new JSONObject();
        thirdGlobalEmeraldGeneratorLocations.put("x", -7.5);
        thirdGlobalEmeraldGeneratorLocations.put("y", 1);
        thirdGlobalEmeraldGeneratorLocations.put("z", 8.5);
        thirdGlobalEmeraldGenerator.put("location", thirdGlobalEmeraldGeneratorLocations);

        thirdGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());
        thirdGlobalEmeraldGenerator.put("speed", emeraldGeneratorSpeed);

        globalGenerators.put(thirdGlobalEmeraldGenerator);

        JSONObject fourthGlobalEmeraldGenerator = new JSONObject();

        JSONObject fourthGlobalEmeraldGeneratorLocations = new JSONObject();
        fourthGlobalEmeraldGeneratorLocations.put("x", -7.5);
        fourthGlobalEmeraldGeneratorLocations.put("y", 1);
        fourthGlobalEmeraldGeneratorLocations.put("z", -7.5);
        fourthGlobalEmeraldGenerator.put("location", fourthGlobalEmeraldGeneratorLocations);

        fourthGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());
        fourthGlobalEmeraldGenerator.put("speed", emeraldGeneratorSpeed);

        globalGenerators.put(fourthGlobalEmeraldGenerator);

        JSONObject firstGlobalDiamondGenerator = new JSONObject();

        JSONObject firstGlobalDiamondGeneratorLocations = new JSONObject();
        firstGlobalDiamondGeneratorLocations.put("x", 0.5);
        firstGlobalDiamondGeneratorLocations.put("y", 1);
        firstGlobalDiamondGeneratorLocations.put("z", -43.5);
        firstGlobalDiamondGenerator.put("location", firstGlobalDiamondGeneratorLocations);

        firstGlobalDiamondGenerator.put("material", Material.DIAMOND.toString());
        firstGlobalDiamondGenerator.put("speed", diamondGeneratorSpeed);

        globalGenerators.put(firstGlobalDiamondGenerator);

        JSONObject secondGlobalDiamondGenerator = new JSONObject();

        JSONObject secondGlobalDiamondGeneratorLocations = new JSONObject();
        secondGlobalDiamondGeneratorLocations.put("x", 0.5);
        secondGlobalDiamondGeneratorLocations.put("y", 1);
        secondGlobalDiamondGeneratorLocations.put("z", 44.5);
        secondGlobalDiamondGenerator.put("location", secondGlobalDiamondGeneratorLocations);

        secondGlobalDiamondGenerator.put("material", Material.DIAMOND.toString());
        secondGlobalDiamondGenerator.put("speed", diamondGeneratorSpeed);

        globalGenerators.put(secondGlobalDiamondGenerator);

        minimalistMap.put("globalGenerators", globalGenerators);

        // Time Actions

        JSONArray timeActions = new JSONArray();

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_diamond_1",
                3300,
                1,
                1
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_diamond_2",
                3000,
                1,
                2
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_diamond_3",
                2700,
                1,
                3
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_diamond_4",
                2400,
                1,
                4
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_diamond_5",
                2100,
                1,
                5
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_emerald_1",
                3300,
                2,
                1
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_emerald_2",
                3000,
                2,
                2
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_emerald_3",
                2700,
                2,
                3
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_emerald_4",
                2400,
                2,
                4
        ).toJSON());

        timeActions.put(new GeneratorUpgradeAction.Data(
                "generators_emerald_5",
                1800,
                2,
                5
        ).toJSON());

        timeActions.put(new DestroyBedsAction.Data(
                "beds_gone",
                1500,
                true
        ).toJSON());

        timeActions.put(new WorldborderChangeTimeAction.Data(
                "first_border",
                1200,
                23
        ).toJSON());

        timeActions.put(new EndgameWitherTimeAction.Data(
                "withers",
                600
        ).toJSON());

        minimalistMap.put("timeActions", timeActions);

        return minimalistMap;
    }

    private static JSONObject buildDefaultItem(Material material, int amount) {
        JSONObject item = new JSONObject();
        item.put("type", material.toString());

        if (amount > 1) {
            item.put("amount", amount);
        }

        return item;
    }

    public static ItemStack buildButton(@NotNull String name, @Nullable List<String> description, @NotNull Material material, boolean enchanted) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§r" + name);

        if (description != null) {
            meta.setLore(description.stream().map(string -> "§r" + string).toList());
        }

        if (enchanted) {
            meta.addEnchant(Enchantment.FORTUNE, 0, true);
        }

        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack rocketBuilder(String name, int fireworkStars) {

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();

        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.values());

        meta.setPower(fireworkStars * 2);

        for (int i = 0; i < fireworkStars; i++) {
            meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        }

        meta.setLore(List.of("§r§7Firework Stars: " + meta.getEffects().size(), "§r§7Power: " + meta.getPower()));

        item.setItemMeta(meta);

        return item;

    }

    /*

        // Special Items Button

        JSONObject specialItemsButton = new JSONObject();

        specialItemsButton.put("type", "TNT");
        specialItemsButton.put("name", "§bSpecial Items");

        config.put("10", specialItemsButton);
     */

    public static ItemStack[] getShopMenuBar() {
        ItemStack[] menuBar = new ItemStack[8];

        menuBar[0] = buildButton("§7§lBlocks", null, Material.BRICKS, false);
        menuBar[1] = buildButton("§7§lMelee Weapons", null, Material.DIAMOND_SWORD, false);
        menuBar[2] = buildButton("§7§lArmor / Player Upgrades", null, Material.IRON_BOOTS, true);
        menuBar[3] = buildButton("§7§lTools", null, Material.STONE_PICKAXE, false);
        menuBar[4] = buildButton("§7§lRanged Weapons", null, Material.BOW, true);
        menuBar[5] = buildButton("§7§lPotions", null, Material.POTION, false);
        menuBar[6] = buildButton("§7§lSpecial Items", null, Material.TNT, true);
        menuBar[7] = buildButton("§7§lRedstone", null, Material.REDSTONE, false);

        return menuBar;
    }

    public static Map<String, ShopEntry> getDefaultShopEntries(Plugin plugin) {
        Map<String, ShopEntry> entries = new HashMap<>();

        // ----- BLOCKS (1) -----

        // Wool

        entries.put(
                "wool",
                new ShopEntry(
                        new ItemStack(Material.WHITE_WOOL, 16),
                        Material.IRON_INGOT,
                        4,
                        List.of(new ShopGUIPosition(1, 19))
                )
        );

        // Terracotta

        entries.put(
                "terracotta",
                new ShopEntry(
                        new ItemStack(Material.TERRACOTTA, 24),
                        Material.IRON_INGOT,
                        16,
                        List.of(new ShopGUIPosition(1, 20))
                )
        );

        // Glass

        entries.put(
                "glass",
                new ShopEntry(
                        new ItemStack(Material.GLASS, 4),
                        Material.IRON_INGOT,
                        12,
                        List.of(new ShopGUIPosition(1, 21))
                )
        );

        // End stone

        entries.put(
                "endstone",
                new ShopEntry(
                        new ItemStack(Material.END_STONE, 12),
                        Material.IRON_INGOT,
                        24,
                        List.of(new ShopGUIPosition(1, 22))
                )
        );

        // Ladder

        entries.put(
                "ladder",
                new ShopEntry(
                        new ItemStack(Material.LADDER, 8),
                        Material.IRON_INGOT,
                        4,
                        List.of(new ShopGUIPosition(1, 23))
                )
        );

        // Oak planks

        entries.put(
                "wood",
                new ShopEntry(
                        new ItemStack(Material.OAK_PLANKS, 16),
                        Material.GOLD_INGOT,
                        4,
                        List.of(new ShopGUIPosition(1, 24))
                )
        );

        // Ancient Debris

        ItemStack ancientDebrisItem = new ItemStack(Material.ANCIENT_DEBRIS, 4);
        ItemMeta ancientDebrisMeta = ancientDebrisItem.getItemMeta();
        ancientDebrisMeta.setLore(List.of("§r§7The first serious bed defence."));
        ancientDebrisItem.setItemMeta(ancientDebrisMeta);

        entries.put(
                "ancient_debris",
                new ShopEntry(
                        ancientDebrisItem,
                        Material.EMERALD,
                        4,
                        List.of(new ShopGUIPosition(1, 25))
                )
        );

        // Obsidian

        ItemStack obsidianItem = new ItemStack(Material.OBSIDIAN, 4);
        ItemMeta obsidianMeta = obsidianItem.getItemMeta();
        obsidianMeta.setLore(List.of("§r§7The best affordable defence", "§r§7for your bed!"));
        obsidianItem.setItemMeta(obsidianMeta);

        entries.put(
                "obsidian",
                new ShopEntry(
                        obsidianItem,
                        Material.EMERALD,
                        6,
                        List.of(new ShopGUIPosition(1, 28))
                )
        );

        // Bedrock

        ItemStack bedrockItem = new ItemStack(Material.BEDROCK, 4);
        ItemMeta bedrockMeta = bedrockItem.getItemMeta();
        bedrockMeta.setLore(List.of(
                "§r§7If you want to take it serious.",
                "§r§7Indestructible protection for your bed!",
                "§r§7Will be replaced with glass after beds gone.",
                "§r§7Cannot be placed anymore after beds gone."
        ));
        bedrockItem.setItemMeta(bedrockMeta);

        entries.put(
                "bedrock",
                new ShopEntry(
                        bedrockItem,
                        Material.EMERALD,
                        64,
                        List.of(new ShopGUIPosition(1, 29))
                )
        );

        // ----- WEAPONS (2) -----

        // Stone Sword

        ItemStack stoneSwordItem = new ItemStack(Material.STONE_SWORD);
        ItemMeta stoneSwordMeta = stoneSwordItem.getItemMeta();
        stoneSwordMeta.setLore(List.of("§r§7At least better than fighting with your fists."));
        stoneSwordMeta.setUnbreakable(true);
        stoneSwordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stoneSwordMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        stoneSwordItem.setItemMeta(stoneSwordMeta);

        entries.put(
                "stone_sword",
                new ShopEntry(
                        stoneSwordItem,
                        Material.IRON_INGOT,
                        10,
                        List.of(new ShopGUIPosition(2, 19))
                )
        );

        // Iron Sword

        ItemStack ironSwordItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta ironSwordMeta = ironSwordItem.getItemMeta();
        ironSwordMeta.setLore(List.of("§r§7A mediocre fighting experience."));
        ironSwordMeta.setUnbreakable(true);
        ironSwordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ironSwordMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        ironSwordItem.setItemMeta(ironSwordMeta);

        entries.put(
                "iron_sword",
                new ShopEntry(
                        ironSwordItem,
                        Material.GOLD_INGOT,
                        7,
                        List.of(new ShopGUIPosition(2, 20))
                )
        );

        // Diamond Sword

        ItemStack diamondSwordItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta diamondSwordMeta = diamondSwordItem.getItemMeta();
        diamondSwordMeta.setLore(List.of("§r§7Full fighting power."));
        diamondSwordMeta.setUnbreakable(true);
        diamondSwordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        diamondSwordMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        diamondSwordItem.setItemMeta(diamondSwordMeta);

        entries.put(
                "diamond_sword",
                new ShopEntry(
                        diamondSwordItem,
                        Material.EMERALD,
                        3,
                        List.of(new ShopGUIPosition(2, 21))
                )
        );

        // Netherite Sword

        ItemStack netheriteSwordItem = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta netheriteSwordMeta = netheriteSwordItem.getItemMeta();
        netheriteSwordMeta.setLore(List.of("§r§7To destroy everything."));
        netheriteSwordMeta.setUnbreakable(true);
        netheriteSwordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        netheriteSwordMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        netheriteSwordItem.setItemMeta(netheriteSwordMeta);

        entries.put(
                "netherite_sword",
                new ShopEntry(
                        netheriteSwordItem,
                        Material.EMERALD,
                        9,
                        List.of(new ShopGUIPosition(2, 22))
                )
        );

        // Stone Axe

        ItemStack stoneAxeItem = new ItemStack(Material.STONE_AXE);
        ItemMeta stoneAxeMeta = stoneAxeItem.getItemMeta();
        stoneAxeMeta.setLore(List.of("§r§7At least better than fighting with your fists."));
        stoneAxeMeta.setUnbreakable(true);
        stoneAxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stoneAxeMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        stoneAxeItem.setItemMeta(stoneAxeMeta);

        entries.put(
                "stone_axe",
                new ShopEntry(
                        stoneAxeItem,
                        Material.IRON_INGOT,
                        12,
                        List.of(new ShopGUIPosition(2, 28))
                )
        );

        // Iron Axe

        ItemStack ironAxeItem = new ItemStack(Material.IRON_AXE);
        ItemMeta ironAxeMeta = ironAxeItem.getItemMeta();
        ironAxeMeta.setLore(List.of("§r§7A mediocre fighting experience."));
        ironAxeMeta.setUnbreakable(true);
        ironAxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ironAxeMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        ironAxeItem.setItemMeta(ironAxeMeta);

        entries.put(
                "iron_axe",
                new ShopEntry(
                        ironAxeItem,
                        Material.GOLD_INGOT,
                        9,
                        List.of(new ShopGUIPosition(2, 29))
                )
        );

        // Diamond Axe

        ItemStack diamondAxeItem = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta diamondAxeMeta = diamondAxeItem.getItemMeta();
        diamondAxeMeta.setLore(List.of("§r§7Full fighting power."));
        diamondAxeMeta.setUnbreakable(true);
        diamondAxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        diamondAxeMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        diamondAxeItem.setItemMeta(diamondAxeMeta);

        entries.put(
                "diamond_axe",
                new ShopEntry(
                        diamondAxeItem,
                        Material.EMERALD,
                        4,
                        List.of(new ShopGUIPosition(2, 30))
                )
        );

        // Netherite Axe

        ItemStack netheriteAxeItem = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta netheriteAxeMeta = netheriteAxeItem.getItemMeta();
        netheriteAxeMeta.setLore(List.of("§r§7To destroy everything."));
        netheriteAxeMeta.setUnbreakable(true);
        netheriteAxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        netheriteAxeMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, PersistentDataType.BOOLEAN, true);
        netheriteAxeItem.setItemMeta(netheriteAxeMeta);

        entries.put(
                "netherite_axe",
                new ShopEntry(
                        netheriteAxeItem,
                        Material.EMERALD,
                        11,
                        List.of(new ShopGUIPosition(2, 31))
                )
        );

        // Knockback Stick

        ItemStack knockbackStickItem = new ItemStack(Material.STICK);
        ItemMeta knockbackStickMeta = knockbackStickItem.getItemMeta();
        knockbackStickMeta.setDisplayName("§rKnockback Stick");
        knockbackStickMeta.setLore(List.of("§r§7Knock your enemies into the void."));
        knockbackStickMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        knockbackStickItem.setItemMeta(knockbackStickMeta);

        entries.put(
                "knockback_stick",
                new ShopEntry(
                        knockbackStickItem,
                        Material.GOLD_INGOT,
                        5,
                        List.of(new ShopGUIPosition(2, 25))
                )
        );

        // Knockback Stick MkII

        ItemStack knockbackStickMk2Item = new ItemStack(Material.STICK);
        ItemMeta knockbackStickMk2Meta = knockbackStickMk2Item.getItemMeta();
        knockbackStickMk2Meta.setDisplayName("§rKnockback Stick MkII");
        knockbackStickMk2Meta.setLore(List.of("§r§7Even more knockback..."));
        knockbackStickMk2Meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        knockbackStickMk2Item.setItemMeta(knockbackStickMk2Meta);

        entries.put(
                "knockback_stick_mk2",
                new ShopEntry(
                        knockbackStickMk2Item,
                        Material.EMERALD,
                        5,
                        List.of(new ShopGUIPosition(2, 34))
                )
        );

        // Knockback Stick MkII

        ItemStack knockbackStickMk3Item = new ItemStack(Material.STICK);
        ItemMeta knockbackStickMk3Meta = knockbackStickMk3Item.getItemMeta();
        knockbackStickMk3Meta.setDisplayName("§rKnockback Stick MkIII");
        knockbackStickMk3Meta.setLore(List.of(
                "§r§7Ensures that your opponent is no longer",
                "§r§7on the platform after the hit."
        ));
        knockbackStickMk3Meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        knockbackStickMk3Item.setItemMeta(knockbackStickMk3Meta);

        entries.put(
                "knockback_stick_mk3",
                new ShopEntry(
                        knockbackStickMk3Item,
                        Material.EMERALD,
                        15,
                        List.of(new ShopGUIPosition(2, 43))
                )
        );

        // ----- RANGED WEAPONS (5) -----

        // Standard Bow

        ItemStack standardBowItem = new ItemStack(Material.BOW);
        ItemMeta standardBowMeta = standardBowItem.getItemMeta();
        standardBowMeta.setLore(List.of("§r§7Just a normal bow.", "§r§7Without any special features."));
        standardBowMeta.setUnbreakable(true);
        standardBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        standardBowItem.setItemMeta(standardBowMeta);

        entries.put(
                "standard_bow",
                new ShopEntry(
                        standardBowItem,
                        Material.GOLD_INGOT,
                        12,
                        List.of(new ShopGUIPosition(5, 19))
                )
        );

        // Powerful Bow

        ItemStack powerfulBowItem = new ItemStack(Material.BOW);
        ItemMeta powerfulBowMeta = powerfulBowItem.getItemMeta();
        powerfulBowMeta.setDisplayName("§rEnhanced Bow");
        powerfulBowMeta.setLore(List.of("§r§7A bow with the power enchantment."));
        powerfulBowMeta.addEnchant(Enchantment.POWER, 1, true);
        powerfulBowMeta.setUnbreakable(true);
        powerfulBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        powerfulBowItem.setItemMeta(powerfulBowMeta);

        entries.put(
                "powerful_bow",
                new ShopEntry(
                        powerfulBowItem,
                        Material.GOLD_INGOT,
                        24,
                        List.of(new ShopGUIPosition(5, 20))
                )
        );

        // Power and Punch Bow

        ItemStack powerPunchBowItem = new ItemStack(Material.BOW);
        ItemMeta powerPunchBowMeta = powerPunchBowItem.getItemMeta();
        powerPunchBowMeta.setDisplayName("§rPower & Punch Bow");
        powerPunchBowMeta.setLore(List.of("§r§7Power + Punch."));
        powerPunchBowMeta.addEnchant(Enchantment.POWER, 1, true);
        powerPunchBowMeta.addEnchant(Enchantment.PUNCH, 1, true);
        powerPunchBowMeta.setUnbreakable(true);
        powerPunchBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        powerPunchBowItem.setItemMeta(powerPunchBowMeta);

        entries.put(
                "power_punch_bow",
                new ShopEntry(
                        powerPunchBowItem,
                        Material.EMERALD,
                        6,
                        List.of(new ShopGUIPosition(5, 21))
                )
        );

        // Most powerful bow

        ItemStack mostPowerfulBowItem = new ItemStack(Material.BOW);
        ItemMeta mostPowerfulBowMeta = mostPowerfulBowItem.getItemMeta();
        mostPowerfulBowMeta.setDisplayName("§rMost Powerful Bow");
        mostPowerfulBowMeta.setLore(List.of("§r§7More power, but no punch."));
        mostPowerfulBowMeta.addEnchant(Enchantment.POWER, 2, true);
        mostPowerfulBowMeta.setUnbreakable(true);
        mostPowerfulBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        mostPowerfulBowItem.setItemMeta(mostPowerfulBowMeta);

        entries.put(
                "most_powerful_bow",
                new ShopEntry(
                        mostPowerfulBowItem,
                        Material.EMERALD,
                        8,
                        List.of(new ShopGUIPosition(5, 28))
                )
        );

        // Get on their nerves bow

        ItemStack getOnTheirNervesBowItem = new ItemStack(Material.BOW);
        ItemMeta getOnTheirNervesBowMeta = getOnTheirNervesBowItem.getItemMeta();
        getOnTheirNervesBowMeta.setDisplayName("§rGet-on-their-nerves-Bow");
        getOnTheirNervesBowMeta.setLore(List.of("§r§7The enemies will like you for using this!"));
        getOnTheirNervesBowMeta.addEnchant(Enchantment.PUNCH, 2, true);
        getOnTheirNervesBowMeta.setUnbreakable(true);
        getOnTheirNervesBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        getOnTheirNervesBowItem.setItemMeta(getOnTheirNervesBowMeta);

        entries.put(
                "get_on_their_nerves_bow",
                new ShopEntry(
                        getOnTheirNervesBowItem,
                        Material.EMERALD,
                        8,
                        List.of(new ShopGUIPosition(5, 29))
                )
        );

        // Get on their nerves bow: Extended Edition

        ItemStack getOnTheirNervesBowExtendedItem = new ItemStack(Material.BOW);
        ItemMeta getOnTheirNervesBowExtendedMeta = getOnTheirNervesBowExtendedItem.getItemMeta();
        getOnTheirNervesBowExtendedMeta.setDisplayName("§rGet-on-their-nerves-Bow: Extended Edition");
        getOnTheirNervesBowExtendedMeta.setLore(List.of("§r§7The other teams will definitely", "§r§7remember you for using this!"));
        getOnTheirNervesBowExtendedMeta.addEnchant(Enchantment.PUNCH, 3, true);
        getOnTheirNervesBowExtendedMeta.setUnbreakable(true);
        getOnTheirNervesBowExtendedMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        getOnTheirNervesBowExtendedItem.setItemMeta(getOnTheirNervesBowExtendedMeta);

        entries.put(
                "get_on_their_nerves_bow_extended",
                new ShopEntry(
                        getOnTheirNervesBowExtendedItem,
                        Material.EMERALD,
                        12,
                        List.of(new ShopGUIPosition(5, 30))
                )
        );

        // Crossbow

        ItemStack crossbowItem = new ItemStack(Material.CROSSBOW);
        ItemMeta crossbowMeta = crossbowItem.getItemMeta();
        crossbowMeta.setLore(List.of("§r§7Just a normal Crossbow."));
        crossbowMeta.setUnbreakable(true);
        crossbowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        crossbowItem.setItemMeta(crossbowMeta);

        entries.put(
                "crossbow",
                new ShopEntry(
                        crossbowItem,
                        Material.GOLD_INGOT,
                        15,
                        List.of(new ShopGUIPosition(5, 37))
                )
        );

        // Enhanced Crossbow

        ItemStack enhancedCrossbowItem = new ItemStack(Material.CROSSBOW);
        ItemMeta enhancedCrossbowMeta = enhancedCrossbowItem.getItemMeta();
        enhancedCrossbowMeta.setDisplayName("§rEnhanced Crossbow");
        enhancedCrossbowMeta.setLore(List.of("§r§7If you want to be quicker."));
        enhancedCrossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);
        enhancedCrossbowMeta.setUnbreakable(true);
        enhancedCrossbowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        enhancedCrossbowItem.setItemMeta(enhancedCrossbowMeta);

        entries.put(
                "enhanced_crossbow",
                new ShopEntry(
                        enhancedCrossbowItem,
                        Material.GOLD_INGOT,
                        30,
                        List.of(new ShopGUIPosition(5, 38))
                )
        );

        // Very enhanced Crossbow

        ItemStack veryEnhancedCrossbowItem = new ItemStack(Material.CROSSBOW);
        ItemMeta veryEnhancedCrossbowMeta = veryEnhancedCrossbowItem.getItemMeta();
        veryEnhancedCrossbowMeta.setDisplayName("§rVery Enhanced Crossbow");
        veryEnhancedCrossbowMeta.setLore(List.of(
                "§r§7The enhanced version of",
                "§r§7the enhanced crossbow!"
        ));
        veryEnhancedCrossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 2, true);
        veryEnhancedCrossbowMeta.setUnbreakable(true);
        veryEnhancedCrossbowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        veryEnhancedCrossbowItem.setItemMeta(veryEnhancedCrossbowMeta);

        entries.put(
                "very_enhanced_crossbow",
                new ShopEntry(
                        veryEnhancedCrossbowItem,
                        Material.EMERALD,
                        8,
                        List.of(new ShopGUIPosition(5, 39))
                )
        );

        // Arrow

        entries.put(
                "arrow",
                new ShopEntry(
                        new ItemStack(Material.ARROW),
                        Material.GOLD_INGOT,
                        2,
                        List.of(new ShopGUIPosition(5, 24))
                )
        );
        
        // Spectral Arrow

        entries.put(
                "spectral_arrow",
                new ShopEntry(
                        new ItemStack(Material.SPECTRAL_ARROW),
                        Material.GOLD_INGOT,
                        3,
                        List.of(new ShopGUIPosition(5, 25))
                )
        );

        // Creeper Arrow

        ItemStack creeperArrowItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta creeperArrowMeta = (PotionMeta) creeperArrowItem.getItemMeta();
        creeperArrowMeta.setDisplayName("§rCreeper Arrow");
        creeperArrowMeta.setLore(List.of("§r§7Spawns a Creeper at the point of impact."));
        creeperArrowMeta.setColor(Color.GREEN);
        creeperArrowMeta.addItemFlags(ItemFlag.values());
        creeperArrowMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.CREEPER_ARROW);
        creeperArrowItem.setItemMeta(creeperArrowMeta);

        entries.put(
                "creeper_arrow",
                new ShopEntry(
                        creeperArrowItem,
                        Material.EMERALD,
                        3,
                        List.of(new ShopGUIPosition(5, 33))
                )
        );

        // Vodka Arrow

        ItemStack vodkaArrowItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta vodkaArrowMeta = (PotionMeta) vodkaArrowItem.getItemMeta();
        vodkaArrowMeta.setDisplayName("§rVodka Arrow");
        vodkaArrowMeta.setLore(List.of(
                "§r§7We will not show you the effects here,",
                "§r§7you need to experience them yourself."
        ));
        vodkaArrowMeta.setColor(Color.GRAY);
        vodkaArrowMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30*20, 0, true, true, true), true);
        vodkaArrowMeta.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 30*20, 1, true, true, true), true);
        vodkaArrowMeta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30*20, 0, true, true, true), true);
        vodkaArrowMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 1, true, true, true), true);
        vodkaArrowMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 0, true, true, true), true);
        vodkaArrowMeta.addItemFlags(ItemFlag.values());
        vodkaArrowItem.setItemMeta(vodkaArrowMeta);

        entries.put(
                "vodka_arrow",
                new ShopEntry(
                        vodkaArrowItem,
                        Material.EMERALD,
                        64,
                        List.of(new ShopGUIPosition(5, 34))
                )
        );

        // Small Crossbow Ammunition

        entries.put(
                "small_rocket",
                new ShopEntry(
                        rocketBuilder("§rSmall Rocket", 1),
                        Material.GOLD_INGOT,
                        5,
                        List.of(new ShopGUIPosition(5, 42))
                )
        );

        // Big Crossbow Ammunition

        entries.put(
                "big_rocket",
                new ShopEntry(
                        rocketBuilder("§rBig Rocket", 2),
                        Material.GOLD_INGOT,
                        10,
                        List.of(new ShopGUIPosition(5, 43))
                )
        );

        // ----- POTIONS (6) -----

        // Enhanced movement potion

        ItemStack enhancedMovementPotionItem = new ItemStack(Material.POTION);
        PotionMeta enhancedMovementPotionMeta = (PotionMeta) enhancedMovementPotionItem.getItemMeta();
        enhancedMovementPotionMeta.setDisplayName("§rEnhanced Movement Potion");
        enhancedMovementPotionMeta.setColor(Color.AQUA);
        enhancedMovementPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 0, true, true, true), true);
        enhancedMovementPotionItem.setItemMeta(enhancedMovementPotionMeta);

        entries.put(
                "enhanced_movement_potion",
                new ShopEntry(
                        enhancedMovementPotionItem,
                        Material.EMERALD,
                        1,
                        List.of(new ShopGUIPosition(6, 19))
                )
        );

        // Jump-boost potion

        ItemStack jumpBoostPotionItem = new ItemStack(Material.POTION);
        PotionMeta jumpBoostPotionMeta = (PotionMeta) jumpBoostPotionItem.getItemMeta();
        jumpBoostPotionMeta.setDisplayName("§rJust **** you if you use this shit!");
        jumpBoostPotionMeta.setLore(List.of(
                "§r§7Makes you jump around like a",
                "§r§7stupid a****** for 30 seconds."
        ));
        jumpBoostPotionMeta.setColor(Color.YELLOW);
        jumpBoostPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 30*20, 0, true, true, true), true);
        jumpBoostPotionMeta.addItemFlags(ItemFlag.values());
        jumpBoostPotionItem.setItemMeta(jumpBoostPotionMeta);

        entries.put(
                "jump_boost_potion",
                new ShopEntry(
                        jumpBoostPotionItem,
                        Material.EMERALD,
                        1,
                        List.of(new ShopGUIPosition(6, 20))
                )
        );

        // invisibility Potion

        ItemStack invisibilityPotionItem = new ItemStack(Material.POTION);
        PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotionItem.getItemMeta();
        invisibilityPotionMeta.setDisplayName("§rInvisibility Potion");
        invisibilityPotionMeta.setLore(List.of(
                "§r§7Makes you invisible for 30 seconds.",
                "§r§7This will remove your armor while you're invisible!"
        ));
        invisibilityPotionMeta.setColor(Color.GRAY);
        invisibilityPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 0, true, true, true), true);
        invisibilityPotionMeta.addItemFlags(ItemFlag.values());
        invisibilityPotionItem.setItemMeta(invisibilityPotionMeta);

        entries.put(
                "invisibility_potion",
                new ShopEntry(
                        invisibilityPotionItem,
                        Material.EMERALD,
                        2,
                        List.of(new ShopGUIPosition(6, 21))
                )
        );

        // Green Bull

        ItemStack greenBullItem = new ItemStack(Material.POTION);
        PotionMeta greenBullMeta = (PotionMeta) greenBullItem.getItemMeta();
        greenBullMeta.setDisplayName("§rGreenBull");
        greenBullMeta.setLore(List.of(
                "§r§7Gives you Wings.",
                "§r§7You shouldn't use this unprepared ;)"
        ));
        greenBullMeta.setColor(Color.GREEN);
        greenBullMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, -1, 0, true, true, true), true);
        greenBullMeta.addItemFlags(ItemFlag.values());
        greenBullItem.setItemMeta(greenBullMeta);

        entries.put(
                "green_bull",
                new ShopEntry(
                        greenBullItem,
                        Material.EMERALD,
                        5,
                        List.of(new ShopGUIPosition(6, 22))
                )
        );

        // Suicide Potion

        ItemStack suicidePotionItem = new ItemStack(Material.POTION);
        PotionMeta suicidePotionMeta = (PotionMeta) suicidePotionItem.getItemMeta();
        suicidePotionMeta.setDisplayName("§rI-don't-want-to-live-anymore-Potion");
        suicidePotionMeta.setLore(List.of("§r§7Read the name and guess what it does."));
        suicidePotionMeta.setColor(Color.BLACK);
        suicidePotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 10, true, true, true), true);
        suicidePotionMeta.addItemFlags(ItemFlag.values());
        suicidePotionItem.setItemMeta(suicidePotionMeta);

        entries.put(
                "suicide_potion",
                new ShopEntry(
                        suicidePotionItem,
                        Material.IRON_INGOT,
                        16,
                        List.of(new ShopGUIPosition(6, 23))
                )
        );

        // Slowness Splash Potion

        ItemStack slownessSplashPotionItem = new ItemStack(Material.SPLASH_POTION);
        PotionMeta slownessSplashPotionMeta = (PotionMeta) slownessSplashPotionItem.getItemMeta();
        slownessSplashPotionMeta.setDisplayName("§rSplash Potion of Slowness");
        slownessSplashPotionMeta.setColor(Color.NAVY);
        slownessSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 15*20, 2, true, true, true), true);
        slownessSplashPotionItem.setItemMeta(slownessSplashPotionMeta);

        entries.put(
                "slowness_splash_potion",
                new ShopEntry(
                        slownessSplashPotionItem,
                        Material.GOLD_INGOT,
                        16,
                        List.of(new ShopGUIPosition(6, 28))
                )
        );

        // Extreme Speed Splash Potion

        ItemStack extremeSpeedSplashPotionItem = new ItemStack(Material.SPLASH_POTION);
        PotionMeta extremeSpeedSplashPotionMeta = (PotionMeta) extremeSpeedSplashPotionItem.getItemMeta();
        extremeSpeedSplashPotionMeta.setDisplayName("§rSplash Potion of Extreme Speed");
        extremeSpeedSplashPotionMeta.setLore(List.of(
                "§r§7Makes your enemies so fast that you",
                "§r§7probably will never see them again."
        ));
        extremeSpeedSplashPotionMeta.setColor(Color.AQUA);
        extremeSpeedSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 255, false, false, false), true);
        extremeSpeedSplashPotionMeta.addItemFlags(ItemFlag.values());
        extremeSpeedSplashPotionItem.setItemMeta(extremeSpeedSplashPotionMeta);

        entries.put(
                "extreme_speed_splash_potion",
                new ShopEntry(
                        extremeSpeedSplashPotionItem,
                        Material.EMERALD,
                        9,
                        List.of(new ShopGUIPosition(6, 29))
                )
        );

        // Damage Splash Potion

        ItemStack damageSplashPotionItem = new ItemStack(Material.SPLASH_POTION);
        PotionMeta damageSplashPotionMeta = (PotionMeta) damageSplashPotionItem.getItemMeta();
        damageSplashPotionMeta.setDisplayName("§rSplash Potion of Damage");
        damageSplashPotionMeta.setLore(List.of("§r§7Damage your enemies!"));
        damageSplashPotionMeta.setColor(Color.RED);
        damageSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1, true, true, true), true);
        damageSplashPotionItem.setItemMeta(damageSplashPotionMeta);

        entries.put(
                "damage_splash_potion",
                new ShopEntry(
                        damageSplashPotionItem,
                        Material.EMERALD,
                        10,
                        List.of(new ShopGUIPosition(6, 30))
                )
        );

        // Throwable Vodka

        ItemStack throwableVodkaItem = new ItemStack(Material.SPLASH_POTION);
        PotionMeta throwableVodkaMeta = (PotionMeta) throwableVodkaItem.getItemMeta();
        throwableVodkaMeta.setDisplayName("§rThrowable Vodka");
        throwableVodkaMeta.setLore(List.of(
                "§r§7For lots of fun for your enemies.",
                "§r§7We will not show you the effects here,",
                "§r§7you need to experience them yourself."
        ));
        throwableVodkaMeta.setColor(Color.GRAY);
        throwableVodkaMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30*20, 0, true, true, true), true);
        throwableVodkaMeta.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 30*20, 1, true, true, true), true);
        throwableVodkaMeta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30*20, 0, true, true, true), true);
        throwableVodkaMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15*20, 1, true, true, true), true);
        throwableVodkaMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 15*20, 0, true, true, true), true);
        throwableVodkaMeta.addItemFlags(ItemFlag.values());
        throwableVodkaItem.setItemMeta(throwableVodkaMeta);

        entries.put(
                "throwable_vodka",
                new ShopEntry(
                        throwableVodkaItem,
                        Material.EMERALD,
                        32,
                        List.of(new ShopGUIPosition(6, 31))
                )
        );

        // Milk

        entries.put(
                "milk",
                new ShopEntry(
                        new ItemStack(Material.MILK_BUCKET),
                        Material.GOLD_INGOT,
                        25,
                        List.of(new ShopGUIPosition(6, 25))
                )
        );

        // Effect Immunity Potion

        ItemStack effectImmunityDrinkItem = new ItemStack(Material.POTION);
        PotionMeta effectImmunityDringMeta = (PotionMeta) effectImmunityDrinkItem.getItemMeta();
        effectImmunityDringMeta.setDisplayName("§rPotion of Effect Immunity");
        effectImmunityDringMeta.setLore(List.of(
                "§r§7This will make you immune to all",
                "§r§7potion effects for 30 seconds.",
                "§r§7Please note that positive effects",
                "§r§7are also affected!"
        ));
        effectImmunityDringMeta.addItemFlags(ItemFlag.values());
        effectImmunityDringMeta.setColor(Color.RED);
        if (plugin != null) effectImmunityDringMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.EFFECT_IMMUNITY_POTION);
        effectImmunityDrinkItem.setItemMeta(effectImmunityDringMeta);

        entries.put(
                "effect_immunity_potion",
                new ShopEntry(
                        effectImmunityDrinkItem,
                        Material.EMERALD,
                        10,
                        List.of(new ShopGUIPosition(6, 34))
                )
        );

        // Stealth Potion

        ItemStack stealthPotionItem = new ItemStack(Material.POTION);
        PotionMeta stealthPotionMeta = (PotionMeta) stealthPotionItem.getItemMeta();
        stealthPotionMeta.setDisplayName("§rStealth potion");
        stealthPotionMeta.setLore(List.of(
                "§r§7Makes you undetectable from",
                "§r§7enemy traps for 30 seconds."
        ));
        stealthPotionMeta.addItemFlags(ItemFlag.values());
        stealthPotionMeta.setColor(Color.GRAY);
        if (plugin != null) stealthPotionMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.STEALTH_POTION);
        stealthPotionItem.setItemMeta(stealthPotionMeta);

        entries.put(
                "stealth_potion",
                new ShopEntry(
                        stealthPotionItem,
                        Material.EMERALD,
                        6,
                        List.of(new ShopGUIPosition(6, 43))
                )
        );

        // ----- SPECIAL ITEMS (7) -----

        // Golden Apple

        entries.put(
                "golden_apple",
                new ShopEntry(
                        new ItemStack(Material.GOLDEN_APPLE),
                        Material.GOLD_INGOT,
                        3,
                        List.of(new ShopGUIPosition(7, 19))
                )
        );

        // Enchanted Golden Apple

        ItemStack enchantedGoldenAppleItem = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta enchantedGoldenAppleMeta = enchantedGoldenAppleItem.getItemMeta();
        enchantedGoldenAppleMeta.setLore(List.of("§r§7Your enemies will have fun :)"));
        enchantedGoldenAppleItem.setItemMeta(enchantedGoldenAppleMeta);

        entries.put(
                "enchanted_golden_apple",
                new ShopEntry(
                        enchantedGoldenAppleItem,
                        Material.EMERALD,
                        32,
                        List.of(new ShopGUIPosition(7, 20))
                )
        );

        // Snowball

        ItemStack snowballItem = new ItemStack(Material.SNOWBALL);
        ItemMeta snowballMeta = snowballItem.getItemMeta();
        snowballMeta.setLore(List.of(
                "§r§7For your next snowball fight!",
                "§r§7Causes knockback to entities."
        ));
        snowballItem.setItemMeta(snowballMeta);

        entries.put(
                "snowball",
                new ShopEntry(
                        snowballItem,
                        Material.IRON_INGOT,
                        15,
                        List.of(new ShopGUIPosition(7, 21))
                )
        );

        // Base Defender Spawn Egg

        ItemStack ironGolemSpawnEggItem = new ItemStack(Material.IRON_GOLEM_SPAWN_EGG);
        ItemMeta ironGolemSpawnEggMeta = ironGolemSpawnEggItem.getItemMeta();
        ironGolemSpawnEggMeta.setDisplayName("§rBase Defender Spawn Egg");
        ironGolemSpawnEggMeta.setLore(List.of("§r§7Spawns a base defender."));
        ironGolemSpawnEggMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.BASE_DEFENDER_SPAWN_EGG);
        ironGolemSpawnEggItem.setItemMeta(ironGolemSpawnEggMeta);

        entries.put(
                "base_defender_spawn_egg",
                new ShopEntry(
                        ironGolemSpawnEggItem,
                        Material.IRON_INGOT,
                        256,
                        List.of(new ShopGUIPosition(7, 22))
                )
        );

        // Fireball

        ItemStack fireballItem = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta fireballMeta = fireballItem.getItemMeta();
        fireballMeta.setDisplayName("§rFireball");
        fireballMeta.setLore(List.of("§r§7For a small explosion!"));
        if (plugin != null) fireballMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.FIREBALL);
        fireballItem.setItemMeta(fireballMeta);

        entries.put(
                "fireball",
                new ShopEntry(
                        fireballItem,
                        Material.IRON_INGOT,
                        25,
                        List.of(new ShopGUIPosition(7, 23))
                )
        );

        // Enhanced Fireball

        ItemStack enhancedFireballItem = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta enhancedFireballMeta = enhancedFireballItem.getItemMeta();
        enhancedFireballMeta.setDisplayName("§rEnhanced Fireball");
        enhancedFireballMeta.setLore(List.of("§r§7For a big explosion!"));
        enhancedFireballMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        enhancedFireballMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) enhancedFireballMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.ENHANCED_FIREBALL);
        enhancedFireballItem.setItemMeta(enhancedFireballMeta);

        entries.put(
                "enhanced_fireball",
                new ShopEntry(
                        enhancedFireballItem,
                        Material.EMERALD,
                        1,
                        List.of(new ShopGUIPosition(7, 24))
                )
        );

        // Automatically igniting TNT

        ItemStack automaticallyIgnitingTNTItem = new ItemStack(Material.TNT);
        ItemMeta automaticallyIgnitingTNTMeta = automaticallyIgnitingTNTItem.getItemMeta();
        automaticallyIgnitingTNTMeta.setDisplayName("§rAuto-igniting TNT");
        automaticallyIgnitingTNTMeta.setLore(List.of("§r§7TNT that ignites when placed."));
        automaticallyIgnitingTNTMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        automaticallyIgnitingTNTMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) automaticallyIgnitingTNTMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.AUTOMATICALLY_IGNITING_TNT);
        automaticallyIgnitingTNTItem.setItemMeta(automaticallyIgnitingTNTMeta);

        entries.put(
                "automatically_igniting_tnt",
                new ShopEntry(
                        automaticallyIgnitingTNTItem,
                        Material.GOLD_INGOT,
                        8,
                        List.of(new ShopGUIPosition(7, 25))
                )
        );

        // Ender Pearl

        entries.put(
                "ender_pearl",
                new ShopEntry(
                        new ItemStack(Material.ENDER_PEARL),
                        Material.EMERALD,
                        2,
                        List.of(new ShopGUIPosition(7, 28))
                )
        );

        // Water Bucket

        entries.put(
                "water_bucket",
                new ShopEntry(
                        new ItemStack(Material.WATER_BUCKET),
                        Material.GOLD_INGOT,
                        3,
                        List.of(new ShopGUIPosition(7, 29))
                )
        );

        // Lava Bucket

        entries.put(
                "lava_bucket",
                new ShopEntry(
                        new ItemStack(Material.LAVA_BUCKET),
                        Material.GOLD_INGOT,
                        6,
                        List.of(new ShopGUIPosition(7, 30))
                )
        );

        // Replacement Bed

        ItemStack replacementBedItem = new ItemStack(Material.BLACK_BED);
        ItemMeta replacementBedMeta = replacementBedItem.getItemMeta();
        replacementBedMeta.setDisplayName("§rReplacement Bed");
        replacementBedMeta.setLore(List.of(
                "§r§7In case you lost your bed.",
                "§r§7Has to be placed on the original",
                "§r§7location of your team's bed.",
                "§r§7Has no effect after irreplaceable",
                "§r§7beds gone time event."
        ));
        replacementBedItem.setItemMeta(replacementBedMeta);

        entries.put(
                "replacement_bed",
                new ShopEntry(
                        replacementBedItem,
                        Material.EMERALD,
                        128,
                        List.of(new ShopGUIPosition(7, 31))
                )
        );

        // Sponge

        entries.put(
                "sponge",
                new ShopEntry(
                        new ItemStack(Material.SPONGE),
                        Material.GOLD_INGOT,
                        3,
                        List.of(new ShopGUIPosition(7, 32))
                )
        );

        // Cobweb

        entries.put(
                "cobweb",
                new ShopEntry(
                        new ItemStack(Material.COBWEB),
                        Material.IRON_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 33))
                )
        );

        // Chest

        entries.put(
                "chest",
                new ShopEntry(
                        new ItemStack(Material.CHEST),
                        Material.IRON_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 34))
                )
        );

        // Safety Plattform

        ItemStack safetyPlattformItem = new ItemStack(Material.BLAZE_ROD);
        ItemMeta safetyPlattformMeta = safetyPlattformItem.getItemMeta();
        safetyPlattformMeta.setDisplayName("§rSafety Plattform");
        safetyPlattformMeta.setLore(List.of(
                "§r§7- Spawns a 3x3 glass plattform under your feet",
                "§r§7- Place in main hand and right-click to deploy",
                "§r§7- Place in off-hand and press swapping key (F) to deploy",
                "§r§7- Is not placeable when an enemy is in the spawning radius"
        ));
        safetyPlattformMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        safetyPlattformMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) safetyPlattformMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.SAFETY_PLATTFORM);
        safetyPlattformItem.setItemMeta(safetyPlattformMeta);

        entries.put(
                "safety_plattform",
                new ShopEntry(
                        safetyPlattformItem,
                        Material.GOLD_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 37))
                )
        );

        // Battle Plattform

        ItemStack battlePlattformItem = new ItemStack(Material.END_ROD);
        ItemMeta battlePlattformMeta = battlePlattformItem.getItemMeta();
        battlePlattformMeta.setDisplayName("§rBattleground Plattform");
        battlePlattformMeta.setLore(List.of(
                "§r§7- Spawns a 20x20 stone brick plattform under your feet",
                "§r§7- Right-click to deploy",
                "§r§7- Blocks where an enemy player is will not be placed",
                "§r§7- Cover options are placed on the platform",
                "§r§7- Has a cooldown of 60 seconds to prevent spamming"
        ));
        battlePlattformMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        battlePlattformMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) battlePlattformMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.BATTLEGROUND_PLATTFORM);
        battlePlattformItem.setItemMeta(battlePlattformMeta);

        entries.put(
                "battleground_plattform",
                new ShopEntry(
                        battlePlattformItem,
                        Material.GOLD_INGOT,
                        512,
                        List.of(new ShopGUIPosition(7, 39))
                )
        );

        // Auto Bridge

        ItemStack autoBridgeItem = new ItemStack(Material.EGG);
        ItemMeta autoBridgeMeta = autoBridgeItem.getItemMeta();
        autoBridgeMeta.setDisplayName("§rAuto-Bridge");
        autoBridgeMeta.setLore(List.of("§r§7Spawns a bridge in front of you."));
        autoBridgeMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        autoBridgeMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) autoBridgeMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.AUTO_BRIDGE);
        autoBridgeItem.setItemMeta(autoBridgeMeta);

        entries.put(
                "auto_bridge",
                new ShopEntry(
                        autoBridgeItem,
                        Material.GOLD_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 40))
                )
        );

        // Mobile Castle

        ItemStack mobileCastleItem = new ItemStack(Material.STONE_BRICKS);
        ItemMeta mobileCastleMeta = mobileCastleItem.getItemMeta();
        mobileCastleMeta.setDisplayName("§rMobile Castle");
        mobileCastleMeta.setLore(List.of(
                "§r§7- Spawns a small castle",
                "§r§7- Right-click to use",
                "§r§7- Is not placeable when an enemy is in the spawning radius",
                "§r§7- Has a cooldown of 30 seconds to prevent spamming"
        ));
        mobileCastleMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        mobileCastleMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) mobileCastleMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.MOBILE_CASTLE);
        mobileCastleItem.setItemMeta(mobileCastleMeta);

        entries.put(
                "mobile_castle",
                new ShopEntry(
                        mobileCastleItem,
                        Material.IRON_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 41))
                )
        );

        // Single-use Jetpack

        ItemStack singleUseJetpackItem = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemMeta singleUseJetpackMeta = singleUseJetpackItem.getItemMeta();
        singleUseJetpackMeta.setDisplayName("§rSingle-use Jetpack");
        singleUseJetpackMeta.setLore(List.of("§r§7Will boost you up a few blocks."));
        singleUseJetpackMeta.addEnchant(Enchantment.FORTUNE, 0, true);
        singleUseJetpackMeta.addItemFlags(ItemFlag.values());
        if (plugin != null) singleUseJetpackMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.SINGLE_USE_JETPACK);
        singleUseJetpackItem.setItemMeta(singleUseJetpackMeta);

        entries.put(
                "single_use_jetpack",
                new ShopEntry(
                        singleUseJetpackItem,
                        Material.EMERALD,
                        4,
                        List.of(new ShopGUIPosition(7, 42))
                )
        );

        // Snow Defender Spawn Egg

        ItemStack snowDefenderSpawnEggItem = new ItemStack(Material.SNOW_GOLEM_SPAWN_EGG);
        ItemMeta snowDefenderSpawnEggMeta = snowDefenderSpawnEggItem.getItemMeta();
        snowDefenderSpawnEggMeta.setDisplayName("§rSnow Defender Spawn Egg");
        snowDefenderSpawnEggMeta.setLore(List.of("§r§7Spawns a Snow Defender."));
        snowDefenderSpawnEggMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.SNOW_DEFENDER_SPAWN_EGG);
        snowDefenderSpawnEggItem.setItemMeta(snowDefenderSpawnEggMeta);

        entries.put(
                "snow_defender_spawn_egg",
                new ShopEntry(
                        snowDefenderSpawnEggItem,
                        Material.GOLD_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 43))
                )
        );

        // Dog Spawn Egg

        ItemStack dogSpawnEggItem = new ItemStack(Material.WOLF_SPAWN_EGG);
        ItemMeta dogSpawnEggMeta = dogSpawnEggItem.getItemMeta();
        dogSpawnEggMeta.setDisplayName("§rDog Spawn Egg");
        dogSpawnEggMeta.setLore(List.of("§r§7Spawns your new best friend."));
        dogSpawnEggMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.DOG_SPAWN_EGG);
        dogSpawnEggItem.setItemMeta(dogSpawnEggMeta);

        entries.put(
                "dog_spawn_egg",
                new ShopEntry(
                        dogSpawnEggItem,
                        Material.EMERALD,
                        16,
                        List.of(new ShopGUIPosition(7, 46))
                )
        );

        // Grappling Hook

        ItemStack grapplingHookItem = new ItemStack(Material.CROSSBOW);
        ItemMeta grapplingHookMeta = grapplingHookItem.getItemMeta();
        grapplingHookMeta.displayName(LORE.append(Component.text("Grappling Hook")));
        grapplingHookMeta.lore(List.of(
                LORE.append(Component.text("A grappling hook.", NamedTextColor.GRAY)),
                LORE.append(Component.text("- Right click to hook", NamedTextColor.GRAY)),
                LORE.append(Component.text("- Left click to release hook", NamedTextColor.GRAY)),
                LORE.append(Component.text("- Other items can be used while being hooked", NamedTextColor.GRAY)),
                LORE.append(Component.text("- Current status is displayed in the actionbar", NamedTextColor.GRAY))
        ));
        grapplingHookMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        grapplingHookMeta.addItemFlags(ItemFlag.values());
        grapplingHookMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.GRAPPLING_HOOK);
        grapplingHookItem.setItemMeta(grapplingHookMeta);

        // Currently disabled because grappling hook is not implemented yet
        //entries.put(
        //        "grappling_hook",
        //        new ShopEntry(
        //                grapplingHookItem,
        //                Material.EMERALD,
        //                20,
        //                List.of(new ShopGUIPosition(7, 47))
        //        )
        //);

        // Team Chest Access Point

        ItemStack teamChestAccessPointItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta teamChestAccessPointMeta = teamChestAccessPointItem.getItemMeta();
        teamChestAccessPointMeta.setDisplayName("§rTeam Chest Access Point");
        teamChestAccessPointMeta.setLore(List.of(
                "§r§7Place where you want to access",
                "§r§7your team chest.",
                "§r§7Please note that other teams can",
                "§r§7also use it for their team chest."
        ));
        teamChestAccessPointItem.setItemMeta(teamChestAccessPointMeta);

        entries.put(
                "team_chest",
                new ShopEntry(
                        teamChestAccessPointItem,
                        Material.GOLD_INGOT,
                        32,
                        List.of(new ShopGUIPosition(7, 49))
                )
        );

        // Aspect of the World Bait

        ItemStack aspectOfTheWorldBaitItem = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta aspectOfTheWorldBaitMeta = aspectOfTheWorldBaitItem.getItemMeta();
        aspectOfTheWorldBaitMeta.displayName(Component.text("Aspect of the World").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED));
        aspectOfTheWorldBaitMeta.lore(List.of(Component.text("A Sword which came from the world of \"Minceraft\"")));
        aspectOfTheWorldBaitMeta.addItemFlags(ItemFlag.values());
        aspectOfTheWorldBaitItem.setItemMeta(aspectOfTheWorldBaitMeta);

        entries.put(
                "aspect_of_the_world_bait",
                new ShopEntry(
                        aspectOfTheWorldBaitItem,
                        Material.EMERALD,
                        2369,
                        List.of(new ShopGUIPosition(7, 51))
                )
        );

        // Environment Scanner

        ItemStack environmentScannerItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta environmentScannerMeta = environmentScannerItem.getItemMeta();
        environmentScannerMeta.setDisplayName("§rEnvironment Scanner");
        environmentScannerMeta.setLore(List.of(
                "§r§7- Give the nearest enemy glow.",
                "§r§7- The glowing is shown to all players.",
                "§r§7- 30 seconds cooldown to prevent spamming."
        ));
        if (plugin != null) environmentScannerMeta.getPersistentDataContainer().set(NamespacedKeys.GAME_SPECIAL_ITEM, PersistentDataType.STRING, CustomItemValues.ENVIRONMENT_SCANNER);
        environmentScannerItem.setItemMeta(environmentScannerMeta);

        entries.put(
                "environment_scanner",
                new ShopEntry(
                        environmentScannerItem,
                        Material.EMERALD,
                        3,
                        List.of(new ShopGUIPosition(7, 52))
                )
        );

        // Real Aspect of the World (just for fun here, it can not be bought since no ShopGUIPosition is set)

        entries.put(
                "aspect_of_the_world",
                new ShopEntry(
                        MiscUtils.getAspectOfTheWorld(),
                        Material.BARRIER,
                        Integer.MAX_VALUE,
                        List.of()
                )
        );

        // ----- RETURN -----

        return entries;
    }

    public static Map<String, UpgradeEntry> getDefaultUpgradeEntries(Plugin plugin) {
        Map<String, UpgradeEntry> entries = new HashMap<>();

        // Pickaxe
        entries.put("pickaxe", new UpgradeEntry(
                "pickaxe",
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 10),
                        2, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 20),
                        3, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 30),
                        4, new UpgradeEntry.PriceEntry(Material.GOLD_INGOT, 3),
                        5, new UpgradeEntry.PriceEntry(Material.GOLD_INGOT, 6)
                ),
                Set.of(new ShopGUIPosition(4, 20)),
                Map.of(
                        1, getUpgradePickaxe(1),
                        2, getUpgradePickaxe(2),
                        3, getUpgradePickaxe(3),
                        4, getUpgradePickaxe(4),
                        5, getUpgradePickaxe(5)
                )
        ));

        // Armor
        entries.put("armor", new UpgradeEntry(
                "armor",
                Map.of(
                        2, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 40),
                        3, new UpgradeEntry.PriceEntry(Material.GOLD_INGOT, 12),
                        4, new UpgradeEntry.PriceEntry(Material.EMERALD, 6),
                        5, new UpgradeEntry.PriceEntry(Material.EMERALD, 15)
                ),
                Set.of(new ShopGUIPosition(3, 31)),
                Map.of(
                        2, new ItemStack(Material.CHAINMAIL_BOOTS),
                        3, new ItemStack(Material.IRON_BOOTS),
                        4, new ItemStack(Material.DIAMOND_BOOTS),
                        5, new ItemStack(Material.NETHERITE_BOOTS)
                )
        ));

        // Shears
        entries.put("shears", new UpgradeEntry(
                "shears",
                Map.of(1, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 10)),
                Set.of(new ShopGUIPosition(4, 21)),
                Map.of(1, new ItemStack(Material.SHEARS))
        ));

        return entries;
    }

    public static ItemStack getUpgradePickaxe(int level) {

        switch (level) {
            case 1 -> {
                ItemStack item = new ItemStack(Material.WOODEN_PICKAXE);
                ItemMeta meta = item.getItemMeta();

                meta.displayName(Component.text("Pickaxe Lvl. 1"));

                item.setItemMeta(meta);
                return item;
            }
            case 2 -> {
                ItemStack item = new ItemStack(Material.STONE_PICKAXE);
                ItemMeta meta = item.getItemMeta();

                meta.displayName(Component.text("Pickaxe Lvl. 2"));
                meta.addEnchant(Enchantment.EFFICIENCY, 1, true);

                item.setItemMeta(meta);
                return item;
            }
            case 3 -> {
                ItemStack item = new ItemStack(Material.IRON_PICKAXE);
                ItemMeta meta = item.getItemMeta();

                meta.displayName(Component.text("Pickaxe Lvl. 3"));
                meta.addEnchant(Enchantment.EFFICIENCY, 2, true);

                item.setItemMeta(meta);
                return item;
            }
            case 4 -> {
                ItemStack item = new ItemStack(Material.GOLDEN_PICKAXE);
                ItemMeta meta = item.getItemMeta();

                meta.displayName(Component.text("Pickaxe Lvl. 4"));
                meta.addEnchant(Enchantment.EFFICIENCY, 3, true);

                item.setItemMeta(meta);
                return item;
            }
            case 5 -> {
                ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
                ItemMeta meta = item.getItemMeta();

                meta.displayName(Component.text("Pickaxe Lvl. 5"));
                meta.addEnchant(Enchantment.EFFICIENCY, 3, true);

                item.setItemMeta(meta);
                return item;
            }
            default -> {
                return new ItemStack(Material.AIR);
            }
        }

    }

    // PLAYER UPGRADES

    public static @NotNull List<PlayerUpgrade.Data> getPlayerUpgrades() {
        List<PlayerUpgrade.Data> upgrades = new ArrayList<>();

        upgrades.add(new UpgradableItemUpgrade.Data(
                "pickaxe",
                List.of(
                        DefaultConfigValues.getUpgradePickaxe(1),
                        DefaultConfigValues.getUpgradePickaxe(2),
                        DefaultConfigValues.getUpgradePickaxe(3),
                        DefaultConfigValues.getUpgradePickaxe(4),
                        DefaultConfigValues.getUpgradePickaxe(5)
                ),
                true,
                true
        ));

        upgrades.add(new UpgradableItemUpgrade.Data(
                "shears",
                List.of(new ItemStack(Material.SHEARS)),
                true,
                true
        ));

        upgrades.add(new ArmorUpgrade.Data(
                "armor",
                List.of(
                        new ArmorUpgrade.ArmorSet(prepareArmor(Material.LEATHER_HELMET), prepareArmor(Material.LEATHER_CHESTPLATE), prepareArmor(Material.LEATHER_LEGGINGS), prepareArmor(Material.LEATHER_BOOTS)),
                        new ArmorUpgrade.ArmorSet(prepareArmor(Material.LEATHER_HELMET), prepareArmor(Material.LEATHER_CHESTPLATE), prepareArmor(Material.CHAINMAIL_LEGGINGS), prepareArmor(Material.CHAINMAIL_BOOTS)),
                        new ArmorUpgrade.ArmorSet(prepareArmor(Material.LEATHER_HELMET), prepareArmor(Material.LEATHER_CHESTPLATE), prepareArmor(Material.IRON_LEGGINGS),  prepareArmor(Material.IRON_BOOTS)),
                        new ArmorUpgrade.ArmorSet(prepareArmor(Material.LEATHER_HELMET), prepareArmor(Material.LEATHER_CHESTPLATE), prepareArmor(Material.DIAMOND_LEGGINGS), prepareArmor(Material.DIAMOND_BOOTS)),
                        new ArmorUpgrade.ArmorSet(prepareArmor(Material.LEATHER_HELMET), prepareArmor(Material.LEATHER_CHESTPLATE), prepareArmor(Material.NETHERITE_LEGGINGS), prepareArmor(Material.NETHERITE_BOOTS))
                )
        ));

        return upgrades;
    }

    // TEAM UPGRADES

    public static @NotNull List<TeamUpgrade.Data> getTeamUpgrades() {
        List<TeamUpgrade.Data> upgrades = new ArrayList<>();

        upgrades.add(new EnchantmentTeamUpgrade.Data(TeamUpgrades.SHARPNESS, NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, Enchantment.SHARPNESS));
        upgrades.add(new EnchantmentTeamUpgrade.Data(TeamUpgrades.PROTECTION, NamespacedKeys.GAME_ITEM_PROTECTION_AFFECTED, Enchantment.PROTECTION));
        upgrades.add(new PermanentPotionEffectTeamUpgrade.Data(TeamUpgrades.HASTE, PotionEffectType.HASTE, false, false, false));
        upgrades.add(new HealPoolTeamUpgrade.Data(TeamUpgrades.HEAL_POOL));
        upgrades.add(new TeamChestUpgrade.Data(TeamUpgrades.TEAM_CHEST));

        return upgrades;
    }

    public static @NotNull JSONObject getDefaultTeamUpgradesFile() {
        JSONObject teamUpgradesFile = new JSONObject();

        // Team Upgrades
        JSONObject teamUpgradesSection = new JSONObject();
        for (TeamUpgrade.Data data : DefaultConfigValues.getTeamUpgrades()) {
            teamUpgradesSection.put(data.id(), data.toJSON());
        }
        teamUpgradesFile.put("team_upgrades", teamUpgradesSection);

        // Traps
        // ...

        // Return
        return teamUpgradesFile;
    }

    // ARMOR

    private static @NotNull ItemStack prepareArmor(@NotNull Material material) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_PROTECTION_AFFECTED, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull Map<Integer, QuickBuyMenuEntry> getDefaultQuickBuyMenu() {
        Map<Integer, QuickBuyMenuEntry> menu = new HashMap<>();

        menu.put(19, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "wool"));
        menu.put(20, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "stone_sword"));
        menu.put(21, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.UPGRADE, "armor"));
        menu.put(23, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "standard_bow"));
        menu.put(25, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "automatically_igniting_tnt"));
        menu.put(28, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "wood"));
        menu.put(29, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "iron_sword"));
        menu.put(31, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.UPGRADE, "shears"));
        menu.put(32, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "spectral_arrow"));
        menu.put(34, new QuickBuyMenuEntry(QuickBuyMenuEntry.Type.ITEM, "water_bucket"));

        return menu;
    }

    public static @NotNull Map<String, UpgradeEntry> getDefaultTeamUpgradeEntries() {
        Map<String, UpgradeEntry> upgrades = new HashMap<>();

        upgrades.put("sharpness", new UpgradeEntry(
                TeamUpgrades.SHARPNESS,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 8),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 16),
                        3, new UpgradeEntry.PriceEntry(Material.DIAMOND, 32)
                ),
                Set.of(new ShopGUIPosition(0, 19)),
                Map.of(
                        1, getSharpnessTeamUpgradeItem(0),
                        2, getSharpnessTeamUpgradeItem(1),
                        3, getSharpnessTeamUpgradeItem(2),
                        4, getSharpnessTeamUpgradeItem(3)
                )
        ));

        upgrades.put("protection", new UpgradeEntry(
                TeamUpgrades.PROTECTION,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 5),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 10),
                        3, new UpgradeEntry.PriceEntry(Material.DIAMOND, 20),
                        4, new UpgradeEntry.PriceEntry(Material.DIAMOND, 40),
                        5, new UpgradeEntry.PriceEntry(Material.DIAMOND, 80)
                ),
                Set.of(new ShopGUIPosition(0, 21)),
                Map.of(
                        1, getProtectionTeamUpgradeItem(0),
                        2, getProtectionTeamUpgradeItem(1),
                        3, getProtectionTeamUpgradeItem(2),
                        4, getProtectionTeamUpgradeItem(3),
                        5, getProtectionTeamUpgradeItem(4),
                        6, getProtectionTeamUpgradeItem(5)
                )
        ));

        upgrades.put("haste", new UpgradeEntry(
                TeamUpgrades.HASTE,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 4),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 8)
                ),
                Set.of(new ShopGUIPosition(0, 23)),
                Map.of(
                        1, getHasteTeamUpgradeItem(0),
                        2, getHasteTeamUpgradeItem(1),
                        3, getHasteTeamUpgradeItem(2)
                )
        ));

        upgrades.put("generators", new UpgradeEntry(
                TeamUpgrades.GENERATORS,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 4),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 8),
                        3, new UpgradeEntry.PriceEntry(Material.DIAMOND, 12),
                        4, new UpgradeEntry.PriceEntry(Material.DIAMOND, 16),
                        5, new UpgradeEntry.PriceEntry(Material.DIAMOND, 20)
                ),
                Set.of(new ShopGUIPosition(0, 25)),
                Map.of(
                        1, getGeneratorTeamUpgradeItem(0),
                        2, getGeneratorTeamUpgradeItem(1),
                        3, getGeneratorTeamUpgradeItem(2),
                        4, getGeneratorTeamUpgradeItem(3),
                        5, getGeneratorTeamUpgradeItem(4),
                        6, getGeneratorTeamUpgradeItem(5)
                )
        ));

        upgrades.put("heal_pool", new UpgradeEntry(
                TeamUpgrades.HEAL_POOL,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 3),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 9)
                ),
                Set.of(new ShopGUIPosition(0, 38)),
                Map.of(
                        1, getHealPoolTeamUpgradeItem(0),
                        2, getHealPoolTeamUpgradeItem(1),
                        3, getHealPoolTeamUpgradeItem(2)
                )
        ));

        upgrades.put("end_game_buff", new UpgradeEntry(
                TeamUpgrades.ENDGAME_BUFF,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 20),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 40),
                        3, new UpgradeEntry.PriceEntry(Material.DIAMOND, 60)
                ),
                Set.of(new ShopGUIPosition(0, 40)),
                Map.of(
                        1, getEndgameBuffTeamUpgradeItem(0),
                        2, getEndgameBuffTeamUpgradeItem(1),
                        3, getEndgameBuffTeamUpgradeItem(2),
                        4, getEndgameBuffTeamUpgradeItem(3)
                )
        ));

        upgrades.put("team_chest", new UpgradeEntry(
                TeamUpgrades.TEAM_CHEST,
                Map.of(
                        1, new UpgradeEntry.PriceEntry(Material.DIAMOND, 1),
                        2, new UpgradeEntry.PriceEntry(Material.DIAMOND, 2),
                        3, new UpgradeEntry.PriceEntry(Material.DIAMOND, 3),
                        4, new UpgradeEntry.PriceEntry(Material.DIAMOND, 4),
                        5, new UpgradeEntry.PriceEntry(Material.DIAMOND, 5),
                        6, new UpgradeEntry.PriceEntry(Material.DIAMOND, 6)
                ),
                Set.of(new ShopGUIPosition(0, 42)),
                Map.of(
                        1, getTeamChestUpgradeItem(0),
                        2, getTeamChestUpgradeItem(1),
                        3, getTeamChestUpgradeItem(2),
                        4, getTeamChestUpgradeItem(3),
                        5, getTeamChestUpgradeItem(4),
                        6, getTeamChestUpgradeItem(5),
                        7, getTeamChestUpgradeItem(6)
                )
        ));

        return upgrades;
    }
    
    public static @NotNull Map<String, TeamGUI.TrapEntry> getDefaultTeamTrapEntries() {
        Map<String, TeamGUI.TrapEntry> entries = new HashMap<>();
        
        entries.put(TeamTraps.ALARM_TRAP, new TeamGUI.TrapEntry(
                TeamTraps.ALARM_TRAP,
                new UpgradeEntry.PriceEntry(Material.DIAMOND, 2),
                Set.of(new ShopGUIPosition(0, 29)),
                getAlarmTrapItem()
        ));

        entries.put(TeamTraps.COUNTERMEASURES_TRAP, new TeamGUI.TrapEntry(
                TeamTraps.COUNTERMEASURES_TRAP,
                new UpgradeEntry.PriceEntry(Material.DIAMOND, 3),
                Set.of(new ShopGUIPosition(0, 33)),
                getCountermeasuresTrapItem()
        ));

        entries.put(TeamTraps.ITS_A_TRAP, new TeamGUI.TrapEntry(
                TeamTraps.ITS_A_TRAP,
                new UpgradeEntry.PriceEntry(Material.DIAMOND, 1),
                Set.of(new ShopGUIPosition(0, 30)),
                getItsATrapTrapItem()
        ));

        entries.put(TeamTraps.MINING_FATIGUE_TRAP, new TeamGUI.TrapEntry(
                TeamTraps.MINING_FATIGUE_TRAP,
                new UpgradeEntry.PriceEntry(Material.DIAMOND, 1),
                Set.of(new ShopGUIPosition(0, 32)),
                getMiningFatigueTrapItem()
        ));

        return entries;
    }

    private static @NotNull ItemStack getSharpnessTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.IRON_SWORD,
                Component.text("Sharpness Upgrade", NamedTextColor.WHITE),
                List.of(
                        Component.text("Applies a sharpness enchantment to:", NamedTextColor.GRAY),
                        Component.text(" - Swords", NamedTextColor.GRAY),
                        Component.text(" - Axes", NamedTextColor.GRAY)
                ),
                List.of(
                        new TierListEntry(Component.text("Sharpness I"), Component.text("8 Diamonds")),
                        new TierListEntry(Component.text("Sharpness II"), Component.text("16 Diamonds")),
                        new TierListEntry(Component.text("Sharpness III"), Component.text("32 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getProtectionTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.IRON_CHESTPLATE,
                Component.text("Protection Upgrade", NamedTextColor.WHITE),
                List.of(
                        Component.text("Applies a protection enchantment to:", NamedTextColor.GRAY),
                        Component.text(" - Helmets", NamedTextColor.GRAY),
                        Component.text(" - Chestplates", NamedTextColor.GRAY),
                        Component.text(" - Leggings", NamedTextColor.GRAY),
                        Component.text(" - Boots", NamedTextColor.GRAY)
                ),
                List.of(
                        new TierListEntry(Component.text("Protection I"), Component.text("5 Diamonds")),
                        new TierListEntry(Component.text("Protection II"), Component.text("10 Diamonds")),
                        new TierListEntry(Component.text("Protection III"), Component.text("20 Diamonds")),
                        new TierListEntry(Component.text("Protection IV"), Component.text("40 Diamonds")),
                        new TierListEntry(Component.text("Protection V"), Component.text("80 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getHasteTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.GOLDEN_PICKAXE,
                Component.text("Haste Upgrade", NamedTextColor.WHITE),
                List.of(Component.text("Gives all team members the haste effect.", NamedTextColor.GRAY)),
                List.of(
                        new TierListEntry(Component.text("Haste I"), Component.text("4 Diamonds")),
                        new TierListEntry(Component.text("Haste II"), Component.text("8 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getGeneratorTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.FURNACE,
                Component.text("Generator Upgrade", NamedTextColor.WHITE),
                List.of(Component.text("Speeds up your generators.", NamedTextColor.GRAY)),
                List.of(
                        new TierListEntry(Component.text("Generators"), Component.text("4 Diamonds")),
                        new TierListEntry(Component.text("Generators"), Component.text("8 Diamonds")),
                        new TierListEntry(Component.text("Generators"), Component.text("12 Diamonds")),
                        new TierListEntry(Component.text("Generators"), Component.text("16 Diamonds")),
                        new TierListEntry(Component.text("Generators"), Component.text("20 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getHealPoolTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.BEACON,
                Component.text("Heal Pool", NamedTextColor.WHITE),
                List.of(Component.text("Gives regeneration to all team members in the base.", NamedTextColor.GRAY)),
                List.of(
                        new TierListEntry(Component.text("Regeneration I"), Component.text("3 Diamonds")),
                        new TierListEntry(Component.text("Regeneration II"), Component.text("9 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getEndgameBuffTeamUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.WITHER_SKELETON_SKULL,
                Component.text("Endgame Buff", NamedTextColor.WHITE),
                List.of(Component.text("More Endgame Withers for your team.", NamedTextColor.GRAY)),
                List.of(
                        new TierListEntry(Component.text("2 Withers"), Component.text("20 Diamonds")),
                        new TierListEntry(Component.text("3 Withers"), Component.text("40 Diamonds")),
                        new TierListEntry(Component.text("4 Withers"), Component.text("60 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack getTeamChestUpgradeItem(int level) {
        return generateUpgradeItem(
                Material.CHEST,
                Component.text("Team Chest", NamedTextColor.WHITE),
                List.of(Component.text("Increases the size of the team chest.", NamedTextColor.GRAY)),
                List.of(
                        new TierListEntry(Component.text("9 Slots"), Component.text("1 Diamond")),
                        new TierListEntry(Component.text("18 Slots"), Component.text("2 Diamonds")),
                        new TierListEntry(Component.text("27 Slots"), Component.text("3 Diamonds")),
                        new TierListEntry(Component.text("36 Slots"), Component.text("4 Diamonds")),
                        new TierListEntry(Component.text("45 Slots"), Component.text("5 Diamonds")),
                        new TierListEntry(Component.text("54 Slots"), Component.text("6 Diamonds"))
                ),
                level
        );
    }

    private static @NotNull ItemStack generateUpgradeItem(@NotNull Material material, @NotNull Component name, @NotNull List<Component> description, @NotNull List<TierListEntry> tiers, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(name));

        List<Component> lore = new ArrayList<>();
        lore.addAll(description.stream().map(ItemUtils.CLEARED_LORE_COMPONENT::append).toList());
        lore.add(Component.empty());
        lore.addAll(generateTierList(tiers, level).stream().map(ItemUtils.CLEARED_LORE_COMPONENT::append).toList());

        meta.lore(lore);

        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        item.setAmount(Math.max(Math.min(level, item.getType().getMaxStackSize()), 1));

        return item;
    }

    private static @NotNull List<Component> generateTierList(@NotNull List<TierListEntry> tiers, int currentLevel) {
        if (tiers.isEmpty()) return List.of();

        List<Component> out = new ArrayList<>();

        for (int i = 0; i < tiers.size(); i++) {
            int level = i + 1;

            TierListEntry e = tiers.get(i);

            if (currentLevel >= level) {
                out.add(Component.empty().append(Component.text("Tier " + level + ": ", NamedTextColor.GREEN).append(e.name().color(NamedTextColor.GREEN)).appendSpace().append(Component.text("✔", NamedTextColor.GREEN))));
            } else {
                out.add(Component.empty().append(Component.text("Tier " + level + ": ", NamedTextColor.GRAY).append(e.name().color(NamedTextColor.GRAY)).appendSpace().append(e.price().color(NamedTextColor.GRAY))));
            }

        }

        return out;
    }

    private record TierListEntry(@NotNull Component name, @NotNull Component price) {}
    
    public static @NotNull ItemStack getAlarmTrapItem() {
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta meta = item.getItemMeta();
        
        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Alarm Trap")));
        meta.lore(List.of(
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Features:", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Gives the enemy glowing", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Reveals invisible enemies", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Notifies you and your team", NamedTextColor.GRAY))
        ));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack getCountermeasuresTrapItem() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Countermeasures Trap")));
        meta.lore(List.of(
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Gives effects to your team:", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Jump Boost", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Speed", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Regeneration", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Absorption", NamedTextColor.GRAY))
        ));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack getItsATrapTrapItem() {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("It's a Trap")));
        meta.lore(List.of(
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Gives effects to your enemies:", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Blindness", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text(" - Slowness", NamedTextColor.GRAY))
        ));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack getMiningFatigueTrapItem() {
        ItemStack item = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Mining Fatigue Trap")));
        meta.lore(List.of(
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Gives Mining Fatigue", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("to your enemies", NamedTextColor.GRAY))
        ));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

}