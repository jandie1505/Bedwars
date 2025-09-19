package net.jandie1505.bedwars.game.game.entities.base;

import net.chaossquad.mclib.entity.RespawningManagedEntity;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiNpc extends RespawningManagedEntity<Villager> {
    @NotNull private final Game game;
    @Nullable private InventoryProvider provider;

    /**
     * Creates a new GUI NPC.<br/>
     * When the inventory provider is null, nothing happens when a player clicks on it.
     *
     * @param game              game
     * @param location          location
     * @param provider          inventory provider
     */
    public GuiNpc(@NotNull Game game, @NotNull Location location, @NotNull EntityCreator<Villager> creator, @Nullable InventoryProvider provider) {
        super(game.getWorld(), game.getTaskScheduler(), game, location, creator, true);
        this.game = game;
        this.provider = provider;
    }

    /**
     * Creates a new GUI NPC.<br/>
     * When the inventory provider is null, nothing happens when a player clicks on it.
     *
     * @param game              game
     * @param location          location
     */
    public GuiNpc(@NotNull Game game, @NotNull Location location, @NotNull EntityCreator<Villager> creator) {
        this(game, location, creator, null);
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (this.getEntity() == null) return;
        if (event.getRightClicked() != this.getEntity()) return;
        event.setCancelled(true);

        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        if (this.provider == null) return;
        event.getPlayer().openInventory(this.provider.getInventory(event.getPlayer()));
    }

    // ----- OTHER -----

    /**
     * Returns the game.
     * @return game
     */
    public final @NotNull Game getGame() {
        return this.game;
    }

    /**
     * Returns the inventory provider
     * @return inventory provider
     */
    public final @Nullable InventoryProvider getProvider() {
        return this.provider;
    }

    /**
     * Sets the inventory provider.
     * @param provider inventory provider
     */
    public final void setProvider(@Nullable InventoryProvider provider) {
        this.provider = provider;
    }

    /**
     * Provider for the inventory the GUI NPC will open when a player clicks on it.
     */
    public interface InventoryProvider {
        Inventory getInventory(@NotNull Player player);
    }

}
