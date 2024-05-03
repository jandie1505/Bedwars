package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.util.Vector;

public class BridgeEgg extends ExpiringManagedEntity<Egg> {
    private final Material material;
    private Location lastLocation;

    public BridgeEgg(Game game, Location location, Material material) {
        super(game, game.getWorld().spawn(location.clone(), Egg.class), 15);
        this.material = material;
        this.lastLocation = null;

        // Vector

        Vector tpVector = this.getEntity().getVelocity().clone();
        tpVector.divide(new Vector(tpVector.length(), tpVector.length(), tpVector.length()));

        location = this.getEntity().getLocation();
        location.add(tpVector);
        location.add(0, -1, 0);

        this.getEntity().teleport(location);

        Vector vector = this.getEntity().getVelocity();

        vector.setX(vector.getX() / 2.0);
        vector.setY(vector.getY() / 2.0);
        vector.setZ(vector.getZ() / 2.0);

        this.getEntity().setVelocity(vector);

        // Tasks

        this.scheduleTask(this::velocityTask, 1, 1, "bridge_egg_velocity");
        this.scheduleTask(this::placeBlocksTask, 1, 1, "bridge_egg_place");
    }

    // TASKS

    private void velocityTask() {
        Vector vector = this.getEntity().getVelocity();

        if (vector.getY() < 0.0) {
            vector.setY(vector.getY() / 2);
        }

        this.getEntity().setVelocity(vector);
    }

    public void placeBlocksTask() {
        if (this.getEntity().getWorld() != this.getGame().getWorld()) return;

        if (this.lastLocation == null) {
            this.updateLastLocation();
            return;
        }

        if (this.getBlockLocation(this.getEntity().getLocation()).equals(this.lastLocation)) return;

        Block block = this.getEntity().getWorld().getBlockAt(this.lastLocation);

        if (block.getType() != Material.AIR) {
            this.updateLastLocation();
            return;
        }

        if (this.material == null) {
            return;
        }

        this.getGame().getPlayerPlacedBlocks().add(block.getLocation());
        block.setType(this.material);
        this.updateLastLocation();

    }

    // UTILITIES

    private Location getBlockLocation(Location location) {
        return new Location(this.getEntity().getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private void updateLastLocation() {
        this.lastLocation = this.getBlockLocation(this.getEntity().getLocation());
    }

}
