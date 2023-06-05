package net.jandie1505.bedwars.game.menu;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Material;
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
    private UpgradeEntry armorUpgrade;
    private UpgradeEntry pickaxeUpgrade;
    private UpgradeEntry shearsUpgrade;

    public ItemShop(Game game) {
        this.game = game;
        this.menuItems = new Integer[9];
        this.shopEntries = Collections.synchronizedList(new ArrayList<>());
        this.armorUpgrade = null;
        this.pickaxeUpgrade = null;
        this.shearsUpgrade = null;
    }

    public Integer[] getMenuItems() {
        return Arrays.copyOf(this.menuItems, this.menuItems.length);
    }

    public List<ShopEntry> getShopEntries() {
        return List.copyOf(this.shopEntries);
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
