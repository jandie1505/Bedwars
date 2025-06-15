package net.jandie1505.bedwars.game.game.entities.entities;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.base.ExpiringManagedEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BridgeEgg extends ExpiringManagedEntity<Egg> {
    @NotNull private final Material material;
    @NotNull private final Set<BlockVector> locationCache;


    public BridgeEgg(@NotNull Game game, @NotNull Egg egg, @NotNull Material material, int maxTime) {
        super(game, egg, maxTime);

        /*
        if (material.isBlock()) {
            throw new IllegalArgumentException("Bridge Egg requires a placeable block");
        }

         */

        this.material = material;
        this.locationCache = new HashSet<>();

        Vector velocity = egg.getVelocity().clone();
        velocity.multiply(0.75);
        egg.setVelocity(velocity);
        egg.setGravity(false);

        this.scheduleRepeatingTask(this::task, 1, 1, "bridge_egg");
    }

    private void task() {
        Egg egg = this.getEntity();
        if (egg == null) return;
        if(egg.isDead()) return;

        BlockVector blockLocation = egg.getLocation().toVector().toBlockVector();

        this.locationCache.add(blockLocation.clone());

        Iterator<BlockVector> i = this.locationCache.iterator();
        while (i.hasNext()) {
            BlockVector location = i.next();
            if (blockLocation.equals(location)) continue;

            i.remove();

            Block block = egg.getWorld().getBlockAt(location.toLocation(egg.getWorld()));
            if (block.getType() != Material.AIR) return;

            block.setType(this.material);
            this.getGame().getBlockProtectionSystem().addBlock(block);
        }

    }

    // ----- OTHER -----

    @NotNull
    public Set<BlockVector> getLocationCache() {
        return this.locationCache;
    }

}
