package net.jandie1505.bedwars.old.game.menu.shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public record ShopGUIPosition(int page, int slot) {

    public static ShopGUIPosition createFromJSON(JSONObject data) {
        int page = data.optInt("page", -1);
        int slot = data.optInt("slot", -1);
        //if (page < 0 || slot < 0) return null;
        return new ShopGUIPosition(page, slot);
    }

    public static JSONObject convertToJSON(ShopGUIPosition guiPosition) {
        JSONObject data = new JSONObject();
        data.put("page", guiPosition.page());
        data.put("slot", guiPosition.slot());
        return data;
    }

    public static List<ShopGUIPosition> createFromJSON(JSONArray data) {
        List<ShopGUIPosition> positions = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            positions.add(ShopGUIPosition.createFromJSON(data.getJSONObject(i)));
        }

        return positions;
    }

    public static JSONArray convertToJSON(List<ShopGUIPosition> positions) {
        JSONArray positionsJSON = new JSONArray();

        for (ShopGUIPosition position : positions) {
            positionsJSON.put(convertToJSON(position));
        }

        return positionsJSON;
    }

}
