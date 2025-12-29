package net.jandie1505.bedwars.game.game.team.gui.enderchest_gui;

import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandaloneResourceStorageGUI implements InventoryHolder {
    @NotNull private final Game game;
    @NotNull private final ResourceStorageGUIManager manager;

    public StandaloneResourceStorageGUI(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.manager = new ResourceStorageGUIManager(this.game, this, removeCondition);
    }

    // ----- INVENTORY -----

    public @NotNull Inventory getInventory(@NotNull BedwarsTeam team) {
        Inventory inventory = Bukkit.createInventory(this, 27, Component.text("Resource Storage", NamedTextColor.DARK_AQUA));
        ResourceStorageGUIManager.setListenerEnabled(inventory, true);
        return this.manager.buildInventory(inventory, 0, team);
    }

    // ----- OTHER -----


    public @NotNull ResourceStorageGUIManager getManager() {
        return manager;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Resource Storage: Team not specified", NamedTextColor.RED));
    }

}
