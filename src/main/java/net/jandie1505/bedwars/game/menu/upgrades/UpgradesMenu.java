package net.jandie1505.bedwars.game.menu.upgrades;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class UpgradesMenu implements InventoryHolder {
    private final Game game;
    private final UUID playerId;

    public UpgradesMenu(Game game, UUID playerId) {
        this.game = game;
        this.playerId = playerId;
    }

    @Override
    public Inventory getInventory() {
        return this.game.getPlugin().getServer().createInventory(this, 9, "§c§mUpgradesMenu");
    }

}
