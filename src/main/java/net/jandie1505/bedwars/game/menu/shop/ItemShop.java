package net.jandie1505.bedwars.game.menu.shop;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemShop {
    private final Game game;
    private final Integer[] menuItems;
    private final List<ShopEntry> shopEntries;
    private Integer defaultWeapon;
    private UpgradeEntry armorUpgrade;
    private UpgradeEntry pickaxeUpgrade;
    private UpgradeEntry shearsUpgrade;
    private Integer fireballItem;
    private Integer enhancedFireballItem;
    private Integer safetyPlatform;
    private Integer enhancedSafetyPlatform;
    private Integer bridgeEgg;
    private Integer playerTracker;
    private Integer ironGolemSpawnEgg;
    private Integer snowDefenderSpawnEgg;
    private Integer zapper;
    private Integer spawnDust;

    public ItemShop(Game game) {
        this.game = game;
        this.menuItems = new Integer[9];
        this.shopEntries = Collections.synchronizedList(new ArrayList<>());
        this.defaultWeapon = null;
        this.armorUpgrade = null;
        this.pickaxeUpgrade = null;
        this.shearsUpgrade = null;
        this.fireballItem = null;
        this.enhancedFireballItem = null;
        this.safetyPlatform = null;
        this.enhancedSafetyPlatform = null;
        this.bridgeEgg = null;
        this.playerTracker = null;
        this.ironGolemSpawnEgg = null;
        this.snowDefenderSpawnEgg = null;
        this.zapper = null;
        this.spawnDust = null;
    }

    public Integer[] getMenuItems() {
        return Arrays.copyOf(this.menuItems, this.menuItems.length);
    }

    public List<ShopEntry> getShopEntries() {
        return List.copyOf(this.shopEntries);
    }

    public Integer getDefaultWeapon() {
        return this.defaultWeapon;
    }

    public ItemStack getDefaultWeaponItem() {

        if (this.defaultWeapon == null) {
            return null;
        }

        return this.game.getPlugin().getItemStorage().getItem(this.defaultWeapon);
    }

    public List<UpgradeEntry> getUpgradeEntries() {
        return List.of(
                this.armorUpgrade,
                this.pickaxeUpgrade,
                this.shearsUpgrade
        );
    }

    public List<ShopEntry> getShopEntryPage(int page) {
        List<ShopEntry> returnList = new ArrayList<>();

        for (ShopEntry shopEntry : this.getShopEntries()) {

            if (shopEntry.getPage() == page) {
                returnList.add(shopEntry);
            }

        }

        return List.copyOf(returnList);
    }

    public List<UpgradeEntry> getUpgradeEntryPage(int page) {
        List<UpgradeEntry> returnList = new ArrayList<>();
        List<UpgradeEntry> iList = new ArrayList<>();

        iList.add(this.armorUpgrade);
        iList.add(this.pickaxeUpgrade);
        iList.add(this.shearsUpgrade);

        while (iList.remove(null));

        for (UpgradeEntry upgradeEntry : iList) {

            for (int[] slot : upgradeEntry.getSlots()) {

                if (slot.length != 2) {
                    continue;
                }

                if (slot[0] == page) {
                    returnList.add(upgradeEntry);
                    break;
                }

            }

        }

        return returnList;
    }

    public ShopEntry getShopEntry(int id) {

        for (ShopEntry shopEntry : this.getShopEntries()) {

            if (shopEntry.getItemId() == id) {
                return shopEntry;
            }

        }

        return null;
    }

    public UpgradeEntry getArmorUpgrade() {
        return this.armorUpgrade;
    }

    public UpgradeEntry getPickaxeUpgrade() {
        return this.pickaxeUpgrade;
    }

    public UpgradeEntry getShearsUpgrade() {
        return this.shearsUpgrade;
    }

    public UpgradeEntry getUpgradeEntry(int itemId) {

        for (UpgradeEntry upgradeEntry : this.getUpgradeEntries()) {

            if (upgradeEntry.getUpgradeItemIds().contains(itemId)) {
                return upgradeEntry;
            }

        }

        return null;
    }

    public Integer getFireballItem() {
        return fireballItem;
    }

    public Integer getEnhancedFireballItem() {
        return enhancedFireballItem;
    }

    public Integer getSafetyPlatform() {
        return safetyPlatform;
    }

    public Integer getEnhancedSafetyPlatform() {
        return enhancedSafetyPlatform;
    }

    public Integer getBridgeEgg() {
        return bridgeEgg;
    }

    public Integer getPlayerTracker() {
        return playerTracker;
    }

    public Integer getIronGolemSpawnEgg() {
        return ironGolemSpawnEgg;
    }

    public Integer getSnowDefenderSpawnEgg() {
        return snowDefenderSpawnEgg;
    }

    public Integer getZapper() {
        return zapper;
    }

    public Integer getSpawnDust() {
        return spawnDust;
    }

    public void initEntries(JSONObject shopConfig) {
        JSONArray menuItems = shopConfig.optJSONArray("menuItems");

        if (menuItems != null) {

            for (int i = 0; i < 9; i++) {

                int itemId = menuItems.optInt(i, -1);

                if (itemId < 0) {
                    this.menuItems[i] = null;
                } else {
                    this.menuItems[i] = itemId;
                }

            }

        }

        JSONArray shopItems = shopConfig.optJSONArray("shopItems");

        if (shopItems != null) {

            for (Object object : shopItems) {

                if (!(object instanceof JSONObject)) {
                    continue;
                }

                JSONObject shopEntry = (JSONObject) object;

                int itemId = shopEntry.optInt("itemId", -1);

                if (itemId < 0) {
                    continue;
                }

                int price = shopEntry.optInt("price", -1);

                if (price < 0) {
                    continue;
                }

                Material currency = Material.getMaterial(shopEntry.optString("currency", ""));

                if (currency == null) {
                    continue;
                }

                int page = shopEntry.optInt("page", -1);

                if (page < 0) {
                    continue;
                }

                int slot = shopEntry.optInt("slot", -1);

                if (slot < 0) {
                    continue;
                }

                this.shopEntries.add(new ShopEntry(this, itemId, price, currency, page, slot));

            }

        }

        JSONObject specialItems = shopConfig.optJSONObject("specialItems");

        if (specialItems != null) {

            this.defaultWeapon = specialItems.optInt("defaultWeapon", -1);
            this.fireballItem = specialItems.optInt("fireball", -1);
            this.enhancedFireballItem = specialItems.optInt("enhancedFireball", -1);
            this.safetyPlatform = specialItems.optInt("safetyPlatform", -1);
            this.enhancedSafetyPlatform = specialItems.optInt("enhancedSafetyPlatform", -1);
            this.bridgeEgg = specialItems.optInt("bridgeEgg", -1);
            this.playerTracker = specialItems.optInt("playerTracker", -1);
            this.ironGolemSpawnEgg = specialItems.optInt("ironGolemSpawnEgg", -1);
            this.snowDefenderSpawnEgg = specialItems.optInt("snowDefenderSpawnEgg", -1);
            this.zapper = specialItems.optInt("zapper", -1);
            this.spawnDust = specialItems.optInt("spawnDust", -1);

        }

        JSONObject upgradeItems = shopConfig.optJSONObject("upgradeItems");

        if (upgradeItems != null) {

            this.armorUpgrade = this.buildUpgradeEntry(upgradeItems, "armor");
            this.pickaxeUpgrade = this.buildUpgradeEntry(upgradeItems, "pickaxe");
            this.shearsUpgrade = this.buildUpgradeEntry(upgradeItems, "shears");

        }

    }

    private UpgradeEntry buildUpgradeEntry(JSONObject upgradeItems, String key) {

        JSONObject upgrade = upgradeItems.optJSONObject(key);

        if (upgrade == null) {
            return null;
        }

        // Item Ids

        JSONArray itemIds = upgrade.optJSONArray("ids");

        if (itemIds == null) {
            return null;
        }

        List<Integer> itemIdList = new ArrayList<>();

        for (Object object : itemIds) {

            if (!(object instanceof Integer)) {
                continue;
            }

            itemIdList.add((Integer) object);

        }

        // Prices

        JSONArray prices = upgrade.optJSONArray("prices");

        if (prices == null) {
            return null;
        }

        List<Integer> priceList = new ArrayList<>();

        for (Object object : prices) {

            if (!(object instanceof Integer)) {
                continue;
            }

            priceList.add((Integer) object);

        }

        JSONArray currencies = upgrade.optJSONArray("currencies");

        if (currencies == null) {
            return null;
        }

        List<Material> currencyList = new ArrayList<>();

        for (Object object : currencies) {

            if (!(object instanceof String)) {
                continue;
            }

            Material material = Material.getMaterial((String) object);

            if (material == null) {
                continue;
            }

            currencyList.add(material);

        }

        JSONArray slots = upgrade.optJSONArray("slots");

        if (slots == null) {
            return null;
        }

        List<int[]> slotList = new ArrayList<>();

        for (Object object : slots) {

            if (!(object instanceof JSONObject)) {
                continue;
            }

            JSONObject slotData = (JSONObject) object;

            int page = slotData.optInt("page", -1);
            int slot = slotData.optInt("slot", -1);

            if (page < 0 || slot < 0) {
                continue;
            }

            slotList.add(new int[]{page, slot});

        }

        return new UpgradeEntry(this, itemIdList, priceList, currencyList, slotList);
    }

    public Game getGame() {
        return this.game;
    }
}
