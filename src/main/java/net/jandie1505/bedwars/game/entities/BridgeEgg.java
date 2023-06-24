package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.util.Vector;

public class BridgeEgg {
    private final Game game;
    private final Egg egg;
    private final Material material;
    private int lifetime;
    private Location lastLocation;

    public BridgeEgg(Game game, Egg egg, Material material) {
        this.game = game;
        this.egg = egg;
        this.material = material;
        this.lifetime = 5*20;
        this.lastLocation = null;
    }

    public void tick() {

        if (this.egg == null) {
            return;
        }

        if (this.lifetime <= 0) {
            this.egg.remove();
            return;
        }

        this.lifetime--;

        Vector vector = this.egg.getVelocity();

        if (vector.getY() < 0.0) {
            vector.setY(vector.getY() / 2);
        }

        this.egg.setVelocity(vector);

        if (this.game.getWorld() != this.egg.getWorld()) {
            return;
        }

        if (this.lastLocation == null) {
            this.updateLastLocation();
            return;
        }

        if (this.getBlockLocation(this.egg.getLocation()).equals(this.lastLocation)) {
            return;
        }

        Block block = this.egg.getWorld().getBlockAt(this.lastLocation);

        if (block.getType() != Material.AIR) {
            this.updateLastLocation();
            return;
        }

        if (this.material == null) {
            return;
        }

        this.game.getPlayerPlacedBlocks().add(block.getLocation());
        block.setType(this.material);
        this.updateLastLocation();

    }

    private Location getBlockLocation(Location location) {
        return new Location(this.egg.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private void updateLastLocation() {
        this.lastLocation = this.getBlockLocation(this.egg.getLocation());
    }

    public boolean canBeRemoved() {
        return this.game == null || this.egg == null || egg.isDead() || this.game.getWorld() != this.egg.getWorld();
    }
}
