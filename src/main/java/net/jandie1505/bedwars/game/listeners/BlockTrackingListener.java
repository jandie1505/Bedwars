package net.jandie1505.bedwars.game.listeners;

import net.jandie1505.bedwars.ManagedListener;
import net.jandie1505.bedwars.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockTrackingListener implements ManagedListener {
    private Game game;

    public BlockTrackingListener(Game game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer().getUniqueId())) return;
        if (this.game.getPlugin().isPaused()) return;
        if (this.game.getPlayer(event.getPlayer().getUniqueId()) == null) return;

        // Log block place
        this.game.getPlayerPlacedBlocks().add(event.getBlockPlaced().getLocation().clone());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        // Remove block place log entry
        this.game.getPlayerPlacedBlocks().remove(event.getBlock().getLocation().clone());

    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    @Override
    public Game getGame() {
        return this.game;
    }

}
