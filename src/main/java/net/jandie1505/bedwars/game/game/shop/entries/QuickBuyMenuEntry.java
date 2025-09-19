package net.jandie1505.bedwars.game.game.shop.entries;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Quick Buy Menu Entry.
 * @param type type
 * @param id
 */
public record QuickBuyMenuEntry(@NotNull Type type, @NotNull String id) {

    public enum Type {
        ITEM,
        UPGRADE
    }

    public static @NotNull QuickBuyMenuEntry fromJSON(@NotNull JSONObject json) {
        Type type = Type.valueOf(json.getString("type"));
        String id = json.getString("id");
        return new QuickBuyMenuEntry(type, id);
    }

    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", this.type.name());
        json.put("id", this.id);
        return json;
    }

}
