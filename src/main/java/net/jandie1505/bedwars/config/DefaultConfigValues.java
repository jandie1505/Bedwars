package net.jandie1505.bedwars.config;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public final class DefaultConfigValues {
    private DefaultConfigValues() {}

    public static JSONObject getGeneralConfig() {
        JSONObject config = new JSONObject();

        return config;
    }

    public static JSONObject getMapConfig() {
        JSONObject config = new JSONObject();

        JSONArray maps = new JSONArray();

        JSONObject minimalistMap = new JSONObject();

        minimalistMap.put("name", "Minimalist");
        minimalistMap.put("world", "minimalist");
        minimalistMap.put("respawnCooldown", 5);
        minimalistMap.put("maxTime", 3600);

        JSONArray minimalistMapTeams = new JSONArray();

        // Green Team Create

        JSONObject greenTeamData = new JSONObject();

        greenTeamData.put("name", "Green");
        greenTeamData.put("color", Color.LIME.asRGB());
        greenTeamData.put("chatColor", "GREEN");

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

        JSONArray greenTeamIronGeneratorSpeed = new JSONArray();
        greenTeamIronGeneratorSpeed.put(1.0);
        greenTeamIronGeneratorSpeed.put(0.5);
        greenTeamIronGeneratorSpeed.put(0.25);
        greenTeamIronGeneratorSpeed.put(0.0625);
        greenTeamIronGeneratorSpeed.put(0.03125);
        greenTeamIronGenerator.put("speed", greenTeamIronGeneratorSpeed);

        greenTeamGenerators.put(greenTeamIronGenerator);

        JSONObject greenTeamGoldGenerator = new JSONObject();

        JSONObject greenTeamGoldGeneratorLocation = new JSONObject();
        greenTeamGoldGeneratorLocation.put("x", 54.5);
        greenTeamGoldGeneratorLocation.put("y", 1);
        greenTeamGoldGeneratorLocation.put("z", 7.5);
        greenTeamGoldGenerator.put("location", greenTeamGoldGeneratorLocation);

        greenTeamGoldGenerator.put("material", Material.GOLD_INGOT.toString());

        JSONArray greenTeamGoldGeneratorSpeed = new JSONArray();
        greenTeamGoldGeneratorSpeed.put(8.0);
        greenTeamGoldGeneratorSpeed.put(7.0);
        greenTeamGoldGeneratorSpeed.put(5.0);
        greenTeamGoldGeneratorSpeed.put(4.0);
        greenTeamGoldGeneratorSpeed.put(2.0);
        greenTeamGoldGenerator.put("speed", greenTeamGoldGeneratorSpeed);

        greenTeamGenerators.put(greenTeamGoldGenerator);

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

        JSONArray redTeamIronGeneratorSpeed = new JSONArray();
        redTeamIronGeneratorSpeed.put(1.0);
        redTeamIronGeneratorSpeed.put(0.5);
        redTeamIronGeneratorSpeed.put(0.25);
        redTeamIronGeneratorSpeed.put(0.0625);
        redTeamIronGeneratorSpeed.put(0.03125);
        redTeamIronGenerator.put("speed", redTeamIronGeneratorSpeed);

        redTeamGenerators.put(redTeamIronGenerator);

        JSONObject redTeamGoldGenerator = new JSONObject();

        JSONObject redTeamGoldGeneratorLocation = new JSONObject();
        redTeamGoldGeneratorLocation.put("x", -53.5);
        redTeamGoldGeneratorLocation.put("y", 1);
        redTeamGoldGeneratorLocation.put("z", -6.5);
        redTeamGoldGenerator.put("location", redTeamGoldGeneratorLocation);

        redTeamGoldGenerator.put("material", Material.GOLD_INGOT.toString());

        JSONArray redTeamGoldGeneratorSpeed = new JSONArray();
        redTeamGoldGeneratorSpeed.put(8.0);
        redTeamGoldGeneratorSpeed.put(7.0);
        redTeamGoldGeneratorSpeed.put(5.0);
        redTeamGoldGeneratorSpeed.put(4.0);
        redTeamGoldGeneratorSpeed.put(2.0);
        redTeamGoldGenerator.put("speed", redTeamGoldGeneratorSpeed);

        redTeamGenerators.put(redTeamGoldGenerator);

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

        // Global Generators

        JSONArray globalGenerators = new JSONArray();

        JSONObject firstGlobalEmeraldGenerator = new JSONObject();

        JSONObject firstGlobalEmeraldGeneratorLocations = new JSONObject();
        firstGlobalEmeraldGeneratorLocations.put("x", 8.5);
        firstGlobalEmeraldGeneratorLocations.put("y", 1);
        firstGlobalEmeraldGeneratorLocations.put("z", 8.5);
        firstGlobalEmeraldGenerator.put("location", firstGlobalEmeraldGeneratorLocations);

        firstGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());

        JSONArray firstGlobalEmeraldGeneratorSpeed = new JSONArray();
        firstGlobalEmeraldGeneratorSpeed.put(40);
        firstGlobalEmeraldGeneratorSpeed.put(30);
        firstGlobalEmeraldGeneratorSpeed.put(20);
        firstGlobalEmeraldGeneratorSpeed.put(10);
        firstGlobalEmeraldGeneratorSpeed.put(5);
        firstGlobalEmeraldGenerator.put("speed", firstGlobalEmeraldGeneratorSpeed);

        globalGenerators.put(firstGlobalEmeraldGenerator);

        JSONObject secondGlobalEmeraldGenerator = new JSONObject();

        JSONObject secondGlobalEmeraldGeneratorLocations = new JSONObject();
        secondGlobalEmeraldGeneratorLocations.put("x", 8.5);
        secondGlobalEmeraldGeneratorLocations.put("y", 1);
        secondGlobalEmeraldGeneratorLocations.put("z", -7.5);
        secondGlobalEmeraldGenerator.put("location", secondGlobalEmeraldGeneratorLocations);

        secondGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());

        JSONArray secondGlobalEmeraldGeneratorSpeed = new JSONArray();
        secondGlobalEmeraldGeneratorSpeed.put(40);
        secondGlobalEmeraldGeneratorSpeed.put(30);
        secondGlobalEmeraldGeneratorSpeed.put(20);
        secondGlobalEmeraldGeneratorSpeed.put(10);
        secondGlobalEmeraldGeneratorSpeed.put(5);
        secondGlobalEmeraldGenerator.put("speed", secondGlobalEmeraldGeneratorSpeed);

        globalGenerators.put(secondGlobalEmeraldGenerator);

        JSONObject thirdGlobalEmeraldGenerator = new JSONObject();

        JSONObject thirdGlobalEmeraldGeneratorLocations = new JSONObject();
        thirdGlobalEmeraldGeneratorLocations.put("x", -7.5);
        thirdGlobalEmeraldGeneratorLocations.put("y", 1);
        thirdGlobalEmeraldGeneratorLocations.put("z", 8.5);
        thirdGlobalEmeraldGenerator.put("location", thirdGlobalEmeraldGeneratorLocations);

        thirdGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());

        JSONArray thirdGlobalEmeraldGeneratorSpeed = new JSONArray();
        thirdGlobalEmeraldGeneratorSpeed.put(40);
        thirdGlobalEmeraldGeneratorSpeed.put(30);
        thirdGlobalEmeraldGeneratorSpeed.put(20);
        thirdGlobalEmeraldGeneratorSpeed.put(10);
        thirdGlobalEmeraldGeneratorSpeed.put(5);
        thirdGlobalEmeraldGenerator.put("speed", thirdGlobalEmeraldGeneratorSpeed);

        globalGenerators.put(thirdGlobalEmeraldGenerator);

        JSONObject fourthGlobalEmeraldGenerator = new JSONObject();

        JSONObject fourthGlobalEmeraldGeneratorLocations = new JSONObject();
        fourthGlobalEmeraldGeneratorLocations.put("x", -7.5);
        fourthGlobalEmeraldGeneratorLocations.put("y", 1);
        fourthGlobalEmeraldGeneratorLocations.put("z", -7.5);
        fourthGlobalEmeraldGenerator.put("location", fourthGlobalEmeraldGeneratorLocations);

        fourthGlobalEmeraldGenerator.put("material", Material.EMERALD.toString());

        JSONArray fourthGlobalEmeraldGeneratorSpeed = new JSONArray();
        fourthGlobalEmeraldGeneratorSpeed.put(40);
        fourthGlobalEmeraldGeneratorSpeed.put(30);
        fourthGlobalEmeraldGeneratorSpeed.put(20);
        fourthGlobalEmeraldGeneratorSpeed.put(10);
        fourthGlobalEmeraldGeneratorSpeed.put(5);
        fourthGlobalEmeraldGenerator.put("speed", fourthGlobalEmeraldGeneratorSpeed);

        globalGenerators.put(fourthGlobalEmeraldGenerator);

        JSONObject firstGlobalDiamondGenerator = new JSONObject();

        JSONObject firstGlobalDiamondGeneratorLocations = new JSONObject();
        firstGlobalDiamondGeneratorLocations.put("x", 0.5);
        firstGlobalDiamondGeneratorLocations.put("y", 1);
        firstGlobalDiamondGeneratorLocations.put("z", -43.5);
        firstGlobalDiamondGenerator.put("location", firstGlobalDiamondGeneratorLocations);

        firstGlobalDiamondGenerator.put("material", Material.DIAMOND.toString());

        JSONArray firstGlobalDiamondGeneratorSpeed = new JSONArray();
        firstGlobalDiamondGeneratorSpeed.put(30);
        firstGlobalDiamondGeneratorSpeed.put(20);
        firstGlobalDiamondGeneratorSpeed.put(10);
        firstGlobalDiamondGeneratorSpeed.put(5);
        firstGlobalDiamondGeneratorSpeed.put(3);
        firstGlobalDiamondGenerator.put("speed", firstGlobalDiamondGeneratorSpeed);

        globalGenerators.put(firstGlobalDiamondGenerator);

        JSONObject secondGlobalDiamondGenerator = new JSONObject();

        JSONObject secondGlobalDiamondGeneratorLocations = new JSONObject();
        secondGlobalDiamondGeneratorLocations.put("x", 0.5);
        secondGlobalDiamondGeneratorLocations.put("y", 1);
        secondGlobalDiamondGeneratorLocations.put("z", 44.5);
        secondGlobalDiamondGenerator.put("location", secondGlobalDiamondGeneratorLocations);

        secondGlobalDiamondGenerator.put("material", Material.DIAMOND.toString());

        JSONArray secondGlobalDiamondGeneratorSpeed = new JSONArray();
        secondGlobalDiamondGeneratorSpeed.put(30);
        secondGlobalDiamondGeneratorSpeed.put(20);
        secondGlobalDiamondGeneratorSpeed.put(10);
        secondGlobalDiamondGeneratorSpeed.put(5);
        secondGlobalDiamondGeneratorSpeed.put(3);
        secondGlobalDiamondGenerator.put("speed", secondGlobalDiamondGeneratorSpeed);

        globalGenerators.put(secondGlobalDiamondGenerator);

        minimalistMap.put("globalGenerators", globalGenerators);

        // Time Actions

        JSONArray timeActions = new JSONArray();

        JSONObject firstDiamondUpgrade = new JSONObject();
        firstDiamondUpgrade.put("type", "GENERATOR_UPGRADE");
        firstDiamondUpgrade.put("generatorType", 1);
        firstDiamondUpgrade.put("generatorLevel", 1);
        firstDiamondUpgrade.put("time", 3300);

        timeActions.put(firstDiamondUpgrade);

        JSONObject secondDiamondUpgrade = new JSONObject();
        secondDiamondUpgrade.put("type", "GENERATOR_UPGRADE");
        secondDiamondUpgrade.put("generatorType", 1);
        secondDiamondUpgrade.put("generatorLevel", 2);
        secondDiamondUpgrade.put("time", 2700);

        timeActions.put(secondDiamondUpgrade);

        JSONObject thirdDiamondUpgrade = new JSONObject();
        thirdDiamondUpgrade.put("type", "GENERATOR_UPGRADE");
        thirdDiamondUpgrade.put("generatorType", 1);
        thirdDiamondUpgrade.put("generatorLevel", 3);
        thirdDiamondUpgrade.put("time", 2100);

        timeActions.put(thirdDiamondUpgrade);

        JSONObject fourthDiamondUpgrade = new JSONObject();
        fourthDiamondUpgrade.put("type", "GENERATOR_UPGRADE");
        fourthDiamondUpgrade.put("generatorType", 1);
        fourthDiamondUpgrade.put("generatorLevel", 4);
        fourthDiamondUpgrade.put("time", 1500);

        timeActions.put(fourthDiamondUpgrade);

        JSONObject fifthDiamondUpgrade = new JSONObject();
        fifthDiamondUpgrade.put("type", "GENERATOR_UPGRADE");
        fifthDiamondUpgrade.put("generatorType", 1);
        fifthDiamondUpgrade.put("generatorLevel", 5);
        fifthDiamondUpgrade.put("time", 900);

        timeActions.put(fifthDiamondUpgrade);

        JSONObject firstEmeraldUpgrade = new JSONObject();
        firstEmeraldUpgrade.put("type", "GENERATOR_UPGRADE");
        firstEmeraldUpgrade.put("generatorType", 2);
        firstEmeraldUpgrade.put("generatorLevel", 1);
        firstEmeraldUpgrade.put("time", 3000);

        timeActions.put(firstEmeraldUpgrade);

        JSONObject secondEmeraldUpgrade = new JSONObject();
        secondEmeraldUpgrade.put("type", "GENERATOR_UPGRADE");
        secondEmeraldUpgrade.put("generatorType", 2);
        secondEmeraldUpgrade.put("generatorLevel", 2);
        secondEmeraldUpgrade.put("time", 2400);

        timeActions.put(secondEmeraldUpgrade);

        JSONObject thirdEmeraldUpgrade = new JSONObject();
        thirdEmeraldUpgrade.put("type", "GENERATOR_UPGRADE");
        thirdEmeraldUpgrade.put("generatorType", 2);
        thirdEmeraldUpgrade.put("generatorLevel", 3);
        thirdEmeraldUpgrade.put("time", 1800);

        timeActions.put(thirdEmeraldUpgrade);

        JSONObject fourthEmeraldUpgrade = new JSONObject();
        fourthEmeraldUpgrade.put("type", "GENERATOR_UPGRADE");
        fourthEmeraldUpgrade.put("generatorType", 2);
        fourthEmeraldUpgrade.put("generatorLevel", 4);
        fourthEmeraldUpgrade.put("time", 1200);

        timeActions.put(fourthEmeraldUpgrade);

        JSONObject fifthEmeraldUpgrade = new JSONObject();
        fifthEmeraldUpgrade.put("type", "GENERATOR_UPGRADE");
        fifthEmeraldUpgrade.put("generatorType", 2);
        fifthEmeraldUpgrade.put("generatorLevel", 5);
        fifthEmeraldUpgrade.put("time", 600);

        timeActions.put(fifthEmeraldUpgrade);

        minimalistMap.put("timeActions", timeActions);

        maps.put(minimalistMap);

        config.put("maps", maps);

        return config;
    }

    public static JSONObject getItemConfig() {
        JSONObject config = new JSONObject();

        // Back Button

        JSONObject backButton = new JSONObject();

        backButton.put("type", "BARRIER");
        backButton.put("name", "§c§lBack");

        JSONArray lore = new JSONArray();
        lore.put("§7Go back");
        backButton.put("lore", lore);

        config.put("0", backButton);

        // Map Vote Button

        JSONObject mapVotingButton = new JSONObject();

        mapVotingButton.put("type", "EMPTY_MAP");
        mapVotingButton.put("name", "§b§lMap Voting §r§b(right click)");

        config.put("1", mapVotingButton);

        // Team Selection Button

        JSONObject teamSelectionButton = new JSONObject();

        teamSelectionButton.put("type", "PLAYER_HEAD");
        teamSelectionButton.put("name", "§b§lTeam Selection §r§b(right click)");

        config.put("2", teamSelectionButton);

        // Quick Buy Button

        JSONObject quickBuyButton = new JSONObject();

        quickBuyButton.put("type", "NETHER_STAR");
        quickBuyButton.put("name", "§bQuick Buy");

        config.put("3", quickBuyButton);

        // Blocks Button

        JSONObject blocksButton = new JSONObject();

        blocksButton.put("type", "BRICKS");
        blocksButton.put("name", "§bBlocks");

        config.put("4", blocksButton);

        // Melee Weapons Button

        JSONObject meleeWeaponsButton = new JSONObject();

        meleeWeaponsButton.put("type", "DIAMOND_SWORD");
        meleeWeaponsButton.put("name", "§bMelee Weapons");

        config.put("5", meleeWeaponsButton);

        // Armor Button

        JSONObject armorButton = new JSONObject();

        armorButton.put("type", "IRON_BOOTS");
        armorButton.put("name", "§bArmor");

        config.put("6", armorButton);

        // Tools Button

        JSONObject toolsButton = new JSONObject();

        toolsButton.put("type", "STONE_PICKAXE");
        toolsButton.put("name", "§bTools");

        config.put("7", toolsButton);

        // Ranged Weapons Button

        JSONObject rangedWeaponsButton = new JSONObject();

        rangedWeaponsButton.put("type", "BOW");
        rangedWeaponsButton.put("name", "§bRanged Weapons");

        config.put("8", rangedWeaponsButton);

        // Potions Button

        JSONObject potionsButton = new JSONObject();

        potionsButton.put("type", "POTION");
        potionsButton.put("name", "§bPotions");

        config.put("9", potionsButton);

        // Special Items Button

        JSONObject specialItemsButton = new JSONObject();

        specialItemsButton.put("type", "TNT");
        specialItemsButton.put("name", "§bSpecial Items");

        config.put("10", specialItemsButton);

        // Wool

        JSONObject wool = new JSONObject();

        wool.put("type", Material.WHITE_WOOL.toString());
        wool.put("name", "§rWool");
        wool.put("amount", 16);

        config.put("100", wool);

        // Terracotta

        config.put("101", buildDefaultItem(Material.TERRACOTTA));

        // Glass

        JSONObject glass = new JSONObject();

        glass.put("type", Material.GLASS.toString());

        config.put("102", glass);

        // Endstone

        JSONObject endstone = new JSONObject();

        endstone.put("type", Material.END_STONE.toString());

        config.put("103", endstone);

        // Ladder

        config.put("104", buildDefaultItem(Material.LADDER));

        // Wood

        config.put("105", buildDefaultItem(Material.OAK_PLANKS));

        // Ancient Debris

        config.put("106", buildDefaultItem(Material.ANCIENT_DEBRIS));

        // Obsidian

        config.put("107", buildDefaultItem(Material.OBSIDIAN));

        // Bedrock

        JSONObject bedrock = new JSONObject();

        endstone.put("type", Material.BEDROCK.toString());
        JSONArray bedrockLore = new JSONArray();
        bedrockLore.put("If you want to take it serious");
        endstone.put("lore", bedrockLore);

        config.put("108", bedrock);

        // Wooden Sword

        config.put("109", buildDefaultItem(Material.WOODEN_SWORD));

        // Stone Sword

        config.put("110", buildDefaultItem(Material.STONE_SWORD));

        // Iron Sword

        config.put("111", buildDefaultItem(Material.IRON_SWORD));

        // Diamond Sword

        config.put("112", buildDefaultItem(Material.DIAMOND_SWORD));

        // Netherite Sword

        config.put("113", buildDefaultItem(Material.NETHERITE_SWORD));

        // Stone Axe

        config.put("114", buildDefaultItem(Material.STONE_AXE));

        // Iron Axe

        config.put("115", buildDefaultItem(Material.IRON_AXE));

        // Diamond Axe

        config.put("116", buildDefaultItem(Material.DIAMOND_AXE));

        // Netherite Axe

        config.put("117", buildDefaultItem(Material.NETHERITE_AXE));

        // Knockback Stick

        JSONObject knockbackStick = new JSONObject();

        knockbackStick.put("type", Material.STICK.toString());
        knockbackStick.put("name", "Knockback Stick");

        JSONArray knockbackStickEnchantments = new JSONArray();

        JSONObject knockbackStickKnockbackEnchantment = new JSONObject();
        knockbackStickKnockbackEnchantment.put("type", Enchantment.KNOCKBACK.toString());
        knockbackStickKnockbackEnchantment.put("level", 1);

        knockbackStickEnchantments.put(knockbackStickKnockbackEnchantment);

        knockbackStick.put("enchantments", knockbackStickEnchantments);

        config.put("118", knockbackStick);

        // Knockback Stick Deluxe

        JSONObject knockbackStickPremium = new JSONObject();

        knockbackStickPremium.put("type", Material.STICK.toString());
        knockbackStickPremium.put("name", "Knockback Stick Premium");

        JSONArray knockbackStickPremiumEnchantments = new JSONArray();

        JSONObject knockbackStickPremiumKnockbackEnchantment = new JSONObject();
        knockbackStickPremiumKnockbackEnchantment.put("type", Enchantment.KNOCKBACK.toString());
        knockbackStickPremiumKnockbackEnchantment.put("level", 2);

        knockbackStickPremiumEnchantments.put(knockbackStickPremiumKnockbackEnchantment);

        knockbackStickPremium.put("enchantments", knockbackStickPremiumEnchantments);

        config.put("119", knockbackStickPremium);

        JSONObject knockbackStickDeluxe = new JSONObject();

        knockbackStickDeluxe.put("type", Material.STICK.toString());
        knockbackStickDeluxe.put("name", "Knockback Stick Premium Deluxe");

        JSONArray knockbackStickDeluxeEnchantments = new JSONArray();

        JSONObject knockbackStickDeluxeKnockbackEnchantment = new JSONObject();
        knockbackStickDeluxeKnockbackEnchantment.put("type", Enchantment.KNOCKBACK.toString());
        knockbackStickDeluxeKnockbackEnchantment.put("level", 3);

        knockbackStickDeluxeEnchantments.put(knockbackStickDeluxeKnockbackEnchantment);

        knockbackStickDeluxe.put("enchantments", knockbackStickDeluxeEnchantments);

        config.put("120", knockbackStickDeluxe);

        // Chainmail Boots

        config.put("121", buildDefaultItem(Material.CHAINMAIL_BOOTS));

        // Iron Boots

        config.put("122", buildDefaultItem(Material.IRON_BOOTS));

        // Diamond Boots

        config.put("123", buildDefaultItem(Material.DIAMOND_BOOTS));

        // Netherite Boots

        config.put("124", buildDefaultItem(Material.NETHERITE_BOOTS));

        // Leather helmet

        config.put("125", buildDefaultItem(Material.LEATHER_HELMET));

        // Leather chestplate

        config.put("126", buildDefaultItem(Material.LEATHER_CHESTPLATE));

        // Leather leggings

        config.put("127", buildDefaultItem(Material.LEATHER_LEGGINGS));

        // Leather boots

        config.put("128", buildDefaultItem(Material.LEATHER_BOOTS));

        // Shears

        config.put("129", buildDefaultItem(Material.SHEARS));

        // Wooden Pickaxe

        config.put("130", buildDefaultItem(Material.WOODEN_PICKAXE));

        // Stone Pickaxe

        JSONObject stonePickaxe = new JSONObject();

        stonePickaxe.put("type", Material.STONE_PICKAXE.toString());

        JSONArray stonePickaxeEnchantments = new JSONArray();

        JSONObject stonePickaxeEfficiencyEnchantment = new JSONObject();
        stonePickaxeEfficiencyEnchantment.put("type", Enchantment.DIG_SPEED.toString());
        stonePickaxeEfficiencyEnchantment.put("level", 1);
        stonePickaxeEnchantments.put(stonePickaxeEfficiencyEnchantment);

        stonePickaxe.put("enchantments", stonePickaxeEnchantments);

        config.put("131", stonePickaxe);

        // Iron Pickaxe

        JSONObject ironPickaxe = new JSONObject();

        ironPickaxe.put("type", Material.STONE_PICKAXE.toString());

        JSONArray ironPickaxeEnchantments = new JSONArray();

        JSONObject ironPickaxeEfficiencyEnchantment = new JSONObject();
        ironPickaxeEfficiencyEnchantment.put("type", Enchantment.DIG_SPEED.toString());
        ironPickaxeEfficiencyEnchantment.put("level", 2);
        ironPickaxeEnchantments.put(ironPickaxeEfficiencyEnchantment);

        ironPickaxe.put("enchantments", ironPickaxeEnchantments);

        config.put("132", ironPickaxe);

        // Golden Pickaxe

        JSONObject goldenPickaxe = new JSONObject();

        goldenPickaxe.put("type", Material.STONE_PICKAXE.toString());

        JSONArray goldenPickaxeEnchantments = new JSONArray();

        JSONObject goldenPickaxeEfficiencyEnchantment = new JSONObject();
        goldenPickaxeEfficiencyEnchantment.put("type", Enchantment.DIG_SPEED.toString());
        goldenPickaxeEfficiencyEnchantment.put("level", 3);
        goldenPickaxeEnchantments.put(goldenPickaxeEfficiencyEnchantment);

        goldenPickaxe.put("enchantments", goldenPickaxeEnchantments);

        config.put("133", goldenPickaxe);

        // Diamond Pickaxe

        JSONObject diamondPickaxe = new JSONObject();

        diamondPickaxe.put("type", Material.STONE_PICKAXE.toString());

        JSONArray diamondPickaxeEnchantments = new JSONArray();

        JSONObject diamondPickaxeEfficiencyEnchantment = new JSONObject();
        diamondPickaxeEfficiencyEnchantment.put("type", Enchantment.DIG_SPEED.toString());
        diamondPickaxeEfficiencyEnchantment.put("level", 3);
        diamondPickaxeEnchantments.put(diamondPickaxeEfficiencyEnchantment);

        diamondPickaxe.put("enchantments", diamondPickaxeEnchantments);

        config.put("134", diamondPickaxe);

        // Bow

        config.put("135", buildDefaultItem(Material.BOW));

        // Enhanced Bow

        JSONObject enhancedBow = new JSONObject();

        enhancedBow.put("type", Material.BOW.toString());
        enhancedBow.put("name", "Enhanced Bow");

        JSONArray enhancedBowEnchantments = new JSONArray();

        JSONObject enhancedBowPowerEnchantment = new JSONObject();
        enhancedBowPowerEnchantment.put("type", Enchantment.ARROW_DAMAGE.toString());
        enhancedBowPowerEnchantment.put("level", 1);
        enhancedBowEnchantments.put(enhancedBowPowerEnchantment);

        enhancedBow.put("enchantments", enhancedBowEnchantments);

        config.put("136", enhancedBow);

        // Most Powerful Bow

        JSONObject mostPowerfulBow = new JSONObject();

        mostPowerfulBow.put("type", Material.BOW.toString());
        mostPowerfulBow.put("name", "Most Powerful Bow");

        JSONArray mostPowerfulBowEnchantments = new JSONArray();

        mostPowerfulBowEnchantments.put(enhancedBowPowerEnchantment);

        JSONObject mostPowerfulBowPunchEnchantment = new JSONObject();
        mostPowerfulBowPunchEnchantment.put("type", Enchantment.ARROW_KNOCKBACK.toString());
        mostPowerfulBowPunchEnchantment.put("level", 1);
        mostPowerfulBowEnchantments.put(mostPowerfulBowPunchEnchantment);

        mostPowerfulBow.put("enchantments", mostPowerfulBowEnchantments);

        config.put("137", mostPowerfulBow);

        // Arrow

        config.put("138", buildDefaultItem(Material.SPECTRAL_ARROW));

        // Golden Apple

        config.put("142", buildDefaultItem(Material.GOLDEN_APPLE));

        // Snowball

        // Iron Golem Spawn Egg

        // Fireball

        JSONObject fireball = new JSONObject();

        fireball.put("type", Material.FIRE_CHARGE.toString());
        fireball.put("name", "Fireball");

        config.put("145", fireball);

        // Enhanced Fireball

        JSONObject enhancedFireball = new JSONObject();

        fireball.put("type", Material.FIRE_CHARGE.toString());
        fireball.put("name", "Enhanced Fireball");

        config.put("146", enhancedFireball);

        // TNT

        config.put("147", buildDefaultItem(Material.TNT));

        // Ender Pearl

        config.put("148", buildDefaultItem(Material.ENDER_PEARL));

        // Water Bucket

        config.put("149", buildDefaultItem(Material.WATER_BUCKET));

        // Bridge Egg

        JSONObject bridgeEgg = new JSONObject();

        bridgeEgg.put("type", Material.EGG.toString());
        bridgeEgg.put("name", "Bridge Egg");

        config.put("150", bridgeEgg);

        // Milk Bucket

        config.put("151", buildDefaultItem(Material.MILK_BUCKET));

        // Sponge

        config.put("152", buildDefaultItem(Material.SPONGE));

        // Sharpness Upgrade

        JSONObject sharpnessUpgrade = new JSONObject();

        sharpnessUpgrade.put("type", Material.IRON_SWORD.toString());
        sharpnessUpgrade.put("name", "Sharpness Upgrade (Swords/Axes)");

        config.put("160", sharpnessUpgrade);

        // Protection Upgrade

        JSONObject protectionUpgrade = new JSONObject();

        protectionUpgrade.put("type", Material.IRON_CHESTPLATE.toString());
        protectionUpgrade.put("name", "Protection Upgrade");

        config.put("161", protectionUpgrade);

        // Haste Upgrade

        JSONObject hasteUpgrade = new JSONObject();

        hasteUpgrade.put("type", Material.GOLDEN_PICKAXE.toString());
        hasteUpgrade.put("name", "Haste Upgrade");

        config.put("162", hasteUpgrade);

        // Forge Upgrade

        JSONObject generatorUpgrade = new JSONObject();

        generatorUpgrade.put("type", Material.FURNACE.toString());
        generatorUpgrade.put("name", "Generator Upgrade");

        config.put("163", generatorUpgrade);

        // Heal Pool Upgrade

        JSONObject healPoolUpgrade = new JSONObject();

        healPoolUpgrade.put("type", Material.BEACON.toString());
        healPoolUpgrade.put("name", "Heal Pool");

        config.put("164", healPoolUpgrade);

        // Dragon Buff Upgrade

        JSONObject dragonBuffUpgrade = new JSONObject();

        dragonBuffUpgrade.put("type", Material.DRAGON_EGG.toString());
        dragonBuffUpgrade.put("name", "Dragon Buff");

        config.put("165", dragonBuffUpgrade);

        return config;
    }

    public static JSONObject getShopConfig() {
        JSONObject config = new JSONObject();

        // ITEM SHOP

        JSONObject itemShopConfig = new JSONObject();

        // Menu Items (category selection)

        JSONArray menuItems = new JSONArray();

        menuItems.put(3);
        menuItems.put(4);
        menuItems.put(5);
        menuItems.put(6);
        menuItems.put(7);
        menuItems.put(8);
        menuItems.put(9);
        menuItems.put(10);

        itemShopConfig.put("menuItems", menuItems);

        // Shop Items (items the players can buy)

        JSONArray shopItems = new JSONArray();

        shopItems.put(buildShopEntry(100, 4, Material.IRON_INGOT, 0, 19));
        shopItems.put(buildShopEntry(110, 10, Material.IRON_INGOT, 0, 20));
        shopItems.put(buildShopEntry(135, 40, Material.IRON_INGOT, 0, 23));

        itemShopConfig.put("shopItems", shopItems);

        // Upgrade Items (armor, pickaxe, shears)

        JSONObject upgradeItems = new JSONObject();

        upgradeItems.put(
                "armor",
                buildUpgradeEntry(
                        List.of(
                                121,
                                122,
                                123,
                                124
                        ),
                        List.of(
                                40,
                                12,
                                6,
                                15
                        ),
                        List.of(
                                Material.IRON_INGOT,
                                Material.GOLD_INGOT,
                                Material.EMERALD,
                                Material.EMERALD
                        ),
                        List.of(
                                new int[]{3, 31}
                        )
                )
        );

        upgradeItems.put(
                "pickaxe",
                buildUpgradeEntry(
                        List.of(
                                130,
                                131,
                                132,
                                133,
                                134
                        ),
                        List.of(
                                10,
                                20,
                                30,
                                3,
                                6
                        ),
                        List.of(
                                Material.IRON_INGOT,
                                Material.IRON_INGOT,
                                Material.IRON_INGOT,
                                Material.GOLD_INGOT,
                                Material.GOLD_INGOT
                        ),
                        List.of(
                                new int[]{4, 20}
                        )
                )
        );

        upgradeItems.put(
                "shears",
                buildUpgradeEntry(
                        List.of(
                                129
                        ),
                        List.of(
                                10
                        ),
                        List.of(
                                Material.IRON_INGOT
                        ),
                        List.of(
                                new int[]{4, 22}
                        )
                )
        );

        itemShopConfig.put("upgradeItems", upgradeItems);

        // Default armor

        JSONObject armorConfig = new JSONObject();

        armorConfig.put("enableArmorSystem", true);

        armorConfig.put("copyHelmet", false);
        armorConfig.put("copyChestplate", false);
        armorConfig.put("copyLeggings", true);

        armorConfig.put("defaultHelmet", 125);
        armorConfig.put("defaultChestplate", 126);
        armorConfig.put("defaultLeggings", 127);
        armorConfig.put("defaultBoots", 128);

        itemShopConfig.put("armorConfig", armorConfig);

        // Save item shop

        config.put("itemShop", itemShopConfig);

        // UPGRADES

        JSONObject upgradeConfig = new JSONObject();

        // Sharpness Upgrade

        JSONObject sharpnessUpgrade = new JSONObject();

        JSONArray sharpnessUpgradeLevels = new JSONArray();

        sharpnessUpgradeLevels.put(1);
        sharpnessUpgradeLevels.put(2);

        sharpnessUpgrade.put("enchantmentLevels", sharpnessUpgradeLevels);

        JSONArray sharpnessUpgradePrices = new JSONArray();

        sharpnessUpgradePrices.put(8);
        sharpnessUpgradePrices.put(16);

        sharpnessUpgrade.put("prices", sharpnessUpgradePrices);

        sharpnessUpgrade.put("item", 160);

        upgradeConfig.put("sharpness", sharpnessUpgrade);

        // Protection Upgrade

        JSONObject protectionUpgrade = new JSONObject();

        JSONArray protectionUpgradeLevels = new JSONArray();

        protectionUpgradeLevels.put(1);
        protectionUpgradeLevels.put(2);
        protectionUpgradeLevels.put(3);
        protectionUpgradeLevels.put(4);

        protectionUpgrade.put("enchantmentLevels", protectionUpgradeLevels);

        JSONArray protectionUpgradePrices = new JSONArray();

        protectionUpgradePrices.put(5);
        protectionUpgradePrices.put(10);
        protectionUpgradePrices.put(20);
        protectionUpgradePrices.put(30);

        protectionUpgrade.put("prices", protectionUpgradePrices);

        protectionUpgrade.put("item", 161);

        upgradeConfig.put("protection", protectionUpgrade);

        // Haste Upgrade

        JSONObject hasteUpgrade = new JSONObject();

        JSONArray hasteUpgradeLevels = new JSONArray();

        hasteUpgradeLevels.put(1);
        hasteUpgradeLevels.put(2);

        hasteUpgrade.put("effectLevels", hasteUpgradeLevels);

        JSONArray hasteUpgradePrices = new JSONArray();

        hasteUpgradePrices.put(4);
        hasteUpgradePrices.put(8);

        hasteUpgrade.put("prices", hasteUpgradePrices);

        hasteUpgrade.put("item", 162);

        upgradeConfig.put("haste", hasteUpgrade);

        // Forge Upgrade

        JSONObject forgeUpgrade = new JSONObject();

        JSONArray forgeUpgradePrices = new JSONArray();

        forgeUpgradePrices.put(4);
        forgeUpgradePrices.put(8);
        forgeUpgradePrices.put(12);
        forgeUpgradePrices.put(16);

        forgeUpgrade.put("prices", forgeUpgradePrices);

        forgeUpgrade.put("item", 163);

        upgradeConfig.put("generators", forgeUpgrade);

        // return

        return config;
    }

    private static JSONObject buildDefaultItem(Material material) {
        JSONObject item = new JSONObject();
        item.put("type", material.toString());
        return item;
    }

    public static JSONObject buildShopEntry(int itemId, int price, Material currency, int page, int slot) {
        JSONObject entry = new JSONObject();
        entry.put("itemId", itemId);
        entry.put("price", price);
        entry.put("currency", currency.toString());
        entry.put("page", page);
        entry.put("slot", slot);
        return entry;
    }

    public static JSONObject buildUpgradeEntry(List<Integer> itemIdList, List<Integer> priceList, List<Material> currencyList, List<int[]> slotList) {
        JSONObject entry = new JSONObject();

        JSONArray itemIds = new JSONArray();

        for (int id : itemIdList) {
            itemIds.put(id);
        }

        JSONArray prices = new JSONArray();

        for (int price : priceList) {
            prices.put(price);
        }

        JSONArray currencies = new JSONArray();

        for (Material material : currencyList) {
            currencies.put(material.toString());
        }

        JSONArray slots = new JSONArray();

        for (int[] slot : slotList) {
            JSONObject slotJSON = new JSONObject();

            slotJSON.put("page", slot[0]);
            slotJSON.put("slot", slot[1]);

            slots.put(slotJSON);
        }

        entry.put("ids", itemIds);
        entry.put("prices", prices);
        entry.put("currencies", currencies);
        entry.put("slots", slots);

        return entry;
    }

}