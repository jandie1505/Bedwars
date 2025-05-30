package net.jandie1505.bedwars.game.game.timeactions.base;

import org.json.JSONObject;

import java.util.Map;

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
            String type = (String) json.remove("type");
            int time = (Integer) json.remove("time");

            Map<String, Object> dataFields = json.toMap();

            return new TimeActionData(type, time, dataFields);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
