package net.jandie1505.bedwars.game.game.world;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockProtectionSystem implements ManagedListener {
    @NotNull private final Game game;
    @NotNull private final Set<Vector> playerPlacedBlocks;

    public BlockProtectionSystem(@NotNull Game game) {
        this.game = game;
        this.playerPlacedBlocks = new HashSet<>();

        this.game.registerListener(this);
    }

    // ----- OBSERVERS -----

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlaceForObserving(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.getPlugin().isPaused()) return;
        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        // Log block place
        this.playerPlacedBlocks.add(event.getBlockPlaced().getLocation().toVector());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakForObserving(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        // Remove block place log entry
        this.playerPlacedBlocks.remove(event.getBlock().getLocation().toVector());
    }

    // ----- PREVENTION -----

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForProtection(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (!this.game.isPlayerIngame(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (this.canBreak(event.getBlock().getLocation())) return;
        if (event.getBlock().getBlockData() instanceof Bed) return;
        if (event.getBlock().getType() == Material.FIRE) return;
        if (event.getBlock().getType() == Material.SNOW) return;

        event.setCancelled(true);
    }

    // ----- MODIFY -----

    public final void addBlock(@NotNull Vector location) {
        this.getPlayerPlacedBlocks().add(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public final void removeBlock(@NotNull Vector location) {
        this.getPlayerPlacedBlocks().remove(location);
        this.getPlayerPlacedBlocks().remove(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void addBlock(@NotNull Location location) {
        this.getPlayerPlacedBlocks().add(location.toVector());
    }

    public void addBlock(@NotNull Block block) {
        this.getPlayerPlacedBlocks().add(block.getLocation().toVector());
    }

    public void removeBlock(@NotNull Location location) {
        this.getPlayerPlacedBlocks().remove(location.toVector());
    }

    public void removeBlock(@NotNull Block block) {
        this.getPlayerPlacedBlocks().remove(block.getLocation().toVector());
    }

    // ----- INFO -----

    public boolean canBreak(@Nullable Vector vector) {
        if (vector == null) return false;
        return this.playerPlacedBlocks.contains(vector.clone());
    }

    public boolean canBreak(Location location) {
        return this.canBreak(location.toVector());
    }

    public @NotNull Set<Vector> getPlayerPlacedBlocks() {
        return this.playerPlacedBlocks;
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
