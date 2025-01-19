package net.jandie1505.bedwars.old.game;

import org.bukkit.Location;

/**
 * Configuration for the game.
 * @param respawnCountdown respawn countdown
 * @param maxTime max time
 * @param spawnBlockPlaceProtection radius around team spawns where players cannot place blocks
 * @param villagerBlockPlaceProtection radius around shop villagers players cannot place blocks
 * @param centerLocation center location of the map
 * @param mapRadius radius of the map
 */
public record GameConfig(int respawnCountdown, int maxTime, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection, Location centerLocation, int mapRadius) {

    public GameConfig(int respawnCountdown, int maxTime, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection, Location centerLocation, int mapRadius) {
        this.respawnCountdown = respawnCountdown;
        this.maxTime = maxTime;
        this.spawnBlockPlaceProtection = spawnBlockPlaceProtection;
        this.villagerBlockPlaceProtection = villagerBlockPlaceProtection;
        this.centerLocation = centerLocation.clone();
        this.mapRadius = mapRadius;
    }

    @Override
    public Location centerLocation() {
        return centerLocation.clone();
    }

}
