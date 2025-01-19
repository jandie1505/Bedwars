package net.jandie1505.bedwars.old.game.timeactions.base;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

/**
 * Contains data about time actions.
 * @param type time action type
 * @param time time action time
 * @param dataFields data fields
 */
public record TimeActionData(String type, int time, Map<String, Object> dataFields) {

    public record DataAccessor<T>(String key) {}

    public TimeActionData(String type, int time, Map<String, Object> dataFields) {
        this.type = type;
        this.time = time;
        this.dataFields = Map.copyOf(dataFields);
    }

    /**
     * Returns a specific data value with the specific type
     * @param accessor data accessor
     * @return value
     * @param <T> type
     */
    @SuppressWarnings("unchecked")
    public <T> T getDataField(DataAccessor<T> accessor) {
        try {
            return (T) dataFields.get(accessor.key());
        } catch (ClassCastException e) {
            return null;
        }
    }

    public JSONObject serializeToJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("time", time);
        jsonObject.put("data", new JSONObject(dataFields));
        return jsonObject;
    }

    public static TimeActionData deserializeFromJSON(JSONObject json) {

        try {
            String type = json.getString("type");
            int time = json.getInt("time");

            Map<String, Object> dataFields = Map.of();
            JSONObject dataObject = json.optJSONObject("data");
            if (dataObject != null) {
                dataFields = Objects.requireNonNull(Objects.requireNonNull(dataObject).toMap());
            }

            return new TimeActionData(type, time, dataFields);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
