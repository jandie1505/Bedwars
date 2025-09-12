package net.jandie1505.bedwars.game.game.shop.shop;

import net.chaossquad.mclib.json.JSONConfigUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public record ShopEntry(
        @NotNull ItemStack item,
        @NotNull Material currency,
        int price,
        @NotNull List<ShopGUIPosition> positions
) {

    public ShopEntry(@NotNull ItemStack item, @NotNull Material currency, int price, @NotNull List<ShopGUIPosition> positions) {
        this.item = item.clone();
        this.currency = currency;
        this.price = price;
        this.positions = List.copyOf(positions);
    }

    @Override
    public ItemStack item() {
        return this.item.clone();
    }

    public static ShopEntry createFromJSON(JSONObject data) {

        JSONObject itemData = data.optJSONObject("item");
        //if (itemData == null) return null;
        ItemStack item;
        try {
            item = JSONConfigUtils.deserializeItem(itemData);
            //if (item == null) return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Material currency = Material.getMaterial(data.getString("currency"));
        //if (currency == null) return null;

        List<ShopGUIPosition> positions = new ArrayList<>();
        JSONArray guiPositions = data.optJSONArray("positions");
        //if (guiPositions == null) return null;
        for (int i = 0; i < guiPositions.length(); i++) {
            JSONObject guiPosition = guiPositions.getJSONObject(i);
            //if (guiPosition == null) continue;
            ShopGUIPosition position = ShopGUIPosition.createFromJSON(guiPosition);
            //if (position == null) continue;
            positions.add(position);
        }

        return new ShopEntry(
                item,
                currency,
                data.optInt("price", 1),
                positions
        );
    }

    public static JSONObject convertToJSON(ShopEntry shopEntry) {
        JSONObject data = new JSONObject();

        data.put("item", JSONConfigUtils.serializeItem(shopEntry.item()));
        data.put("currency", shopEntry.currency().name());
        data.put("price", shopEntry.price());

        JSONArray positions = new JSONArray();
        for (ShopGUIPosition guiPosition : shopEntry.positions) {
            positions.put(ShopGUIPosition.convertToJSON(guiPosition));
        }
        data.put("positions", positions);

        return data;
    }

}
