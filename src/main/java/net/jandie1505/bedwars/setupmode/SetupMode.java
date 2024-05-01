package net.jandie1505.bedwars.setupmode;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public class SetupMode extends GamePart {
    private final World world;
    private final JSONObject mapData;

    public SetupMode(Bedwars plugin, String worldName) {
        super(plugin);
        this.world = this.getPlugin().loadWorld(worldName);

        JSONArray maps = this.getPlugin().getMapConfig().getConfig().optJSONArray("maps");

        JSONObject mapDataInit = null;

        if (maps != null) {
            for (Object object : maps) {

                if (!(object instanceof JSONObject)) {
                    continue;
                }

                JSONObject map = (JSONObject) object;

                if (worldName.equals(map.optString("world"))) {
                    mapDataInit = new JSONObject(map.toString());
                    break;
                }
            }
        }

        if (mapDataInit != null) {
            this.mapData = new JSONObject(mapDataInit.toString());
        } else {
            this.mapData = new JSONObject();
        }

        if (this.world == null || this.world == this.getPlugin().getServer().getWorlds().get(0)) {
            this.getPlugin().stopGame();
            this.getPlugin().getLogger().warning("Game stopped because missing/wrong world in setup mode");
            return;
        }

        this.getTaskScheduler().scheduleRepeatingTask(this::task, 1, 1, "setupMode");
    }

    public boolean shouldRun() {
        return true;
    }

    public boolean task() {

        if (this.world == null) {
            return false;
        }

        if (!this.getPlugin().getServer().getWorlds().contains(this.world)) {
            return false;
        }

        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6MAP SETUP MODE §8§l| §7World: " + this.world.getName()));
        }

        return true;
    }

    @Override
    public GamePart getNextStatus() {
        return null;
    }
}
