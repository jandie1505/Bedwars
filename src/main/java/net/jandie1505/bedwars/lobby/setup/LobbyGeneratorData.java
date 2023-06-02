package net.jandie1505.bedwars.lobby.setup;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class LobbyGeneratorData {
    private final Location location;
    private final ItemStack item;
    private final int startLevel;
    private final double baseSpeed;
    private final double speedDivisor;

    public LobbyGeneratorData(Location location, ItemStack item, int startLevel, double baseSpeed, double speedDivisor) {
        this.location = location;
        this.item = item;
        this.startLevel = startLevel;
        this.baseSpeed = baseSpeed;
        this.speedDivisor = speedDivisor;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getSpeedDivisor() {
        return speedDivisor;
    }
}
