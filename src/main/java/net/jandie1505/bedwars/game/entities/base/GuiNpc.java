package net.jandie1505.bedwars.game.entities.base;

import net.chaossquad.mclib.entity.RespawningManagedEntity;
import net.jandie1505.bedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiNpc extends RespawningManagedEntity<Villager> {
    @NotNull private final Game game;
    @NotNull private final InventoryProvider provider;

    /**
     * Creates a new GUI NPC.
     *
     * @param game              game
     * @param location          location
     * @param provider          inventory provider
     */
    public GuiNpc(@NotNull Game game, @NotNull Location location, @NotNull EntityCreator<Villager> creator, @NotNull InventoryProvider provider) {
        super(game.getWorld(), game.getTaskScheduler(), game, location, creator, true);
        this.game = game;
        this.provider = provider;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (this.getEntity() == null) return;
        if (event.getRightClicked() != this.getEntity()) return;
        event.setCancelled(true);

        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        event.getPlayer().openInventory(this.provider.getInventory(event.getPlayer()));
    }

    // ----- OTHER -----

    public final @NotNull Game getGame() {
        return this.game;
    }

    public final @NotNull InventoryProvider getProvider() {
        return this.provider;
    }

    public interface InventoryProvider {
        Inventory getInventory(@NotNull Player player);
    }

}
