package net.jandie1505.bedwars.lobby.setup;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class LobbyGeneratorData {
    private final Location location;
    private final ItemStack item;
    private final double baseSpeed;
    private final double speedMultiplier;

    public LobbyGeneratorData(Location location, ItemStack item, double baseSpeed, double speedMultiplier) {
        this.location = location;
        this.item = item;
        this.baseSpeed = baseSpeed;
        this.speedMultiplier = speedMultiplier;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
