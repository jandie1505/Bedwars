package net.jandie1505.bedwars.config;

import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

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

        // WOOL

        JSONObject wool = new JSONObject();

        wool.put("type", "WHITE_WOOL");
        wool.put("name", "§rWool");
        wool.put("amount", 16);

        config.put("100", wool);

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

        JSONObject woolBlock = new JSONObject();
        woolBlock.put("itemId", 100);
        woolBlock.put("price", 10);
        woolBlock.put("currency", "IRON_INGOT");
        woolBlock.put("page", 0);
        woolBlock.put("slot", 19);
        shopItems.put(woolBlock);

        itemShopConfig.put("shopItems", shopItems);

        config.put("itemShop", itemShopConfig);

        return config;
    }

}