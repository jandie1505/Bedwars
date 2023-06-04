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

    public ItemShop(Game game) {
        this.game = game;
        this.menuItems = new Integer[9];
        this.shopEntries = Collections.synchronizedList(new ArrayList<>());
    }

    public Integer[] getMenuItems() {
        return Arrays.copyOf(this.menuItems, this.menuItems.length);
    }

    public List<ShopEntry> getShopEntries() {
        return List.copyOf(this.shopEntries);
    }

    public List<ShopEntry> getPage(int page) {
        List<ShopEntry> returnList = new ArrayList<>();

        for (ShopEntry shopEntry : this.getShopEntries()) {

            if (shopEntry.getPage() == page) {
                returnList.add(shopEntry);
            }

        }

        return List.copyOf(returnList);
    }

    public ShopEntry getShopEntry(int id) {

        for (ShopEntry shopEntry : this.getShopEntries()) {

            if (shopEntry.getItemId() == id) {
                return shopEntry;
            }

        }

        return null;
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

    }

    public Game getGame() {
        return this.game;
    }
}
