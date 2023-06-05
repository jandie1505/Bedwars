package net.jandie1505.bedwars.config;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public final class DefaultConfigValues {
    private DefaultConfigValues() {}

    public static JSONObject getGeneralConfig() {
        JSONObject config = new JSONObject();

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

        // Chainmail Leggings

        config.put("125", buildDefaultItem(Material.CHAINMAIL_LEGGINGS));

        // Iron Leggings

        config.put("126", buildDefaultItem(Material.IRON_LEGGINGS));

        // Diamond Leggings

        config.put("127", buildDefaultItem(Material.DIAMOND_LEGGINGS));

        // Netherite Leggings

        config.put("128", buildDefaultItem(Material.NETHERITE_LEGGINGS));

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
                                new int[]{3, 21}
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

        // Save item shop

        config.put("itemShop", itemShopConfig);

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