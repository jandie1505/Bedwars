package net.jandie1505.bedwars.lobby.setup;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LobbyGeneratorData {
    private final Location location;
    private final ItemStack item;
    private final List<Double> upgradeSteps;

    public LobbyGeneratorData(Location location, ItemStack item, List<Double> upgradeSteps) {
        this.location = location;
        this.item = item;
        this.upgradeSteps = upgradeSteps;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack getItem() {
        return item;
    }

    public List<Double> getUpgradeSteps() {
        return List.copyOf(this.upgradeSteps);
    }
}
