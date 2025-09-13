package net.jandie1505.bedwars.game.game.shop;

import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.ShopGUIPosition;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemShop {
    @NotNull private final Game game;
    @NotNull private final Map<String, ShopEntry> items;
    @NotNull private final Map<String, UpgradeEntry> upgrades;

    public ItemShop(@NotNull Game game) {
        this.game = game;
        this.items = new HashMap<>();
        this.upgrades = new HashMap<>();

        this.items.putAll(DefaultConfigValues.getDefaultShopEntries(game.getPlugin()));

        // Demo upgrade. TODO: Remove when no longer needed
        this.upgrades.put("pickaxe", new UpgradeEntry("pickaxe", Map.of(1, new UpgradeEntry.PriceEntry(Material.IRON_INGOT, 10)), Set.of(new ShopGUIPosition(3, 20)), Map.of(1, new ItemStack(Material.WOODEN_PICKAXE))));
    }

    public @Nullable ShopEntry getItem(@NotNull String itemId) {
        return this.items.get(itemId);
    }

    public @NotNull Map<String, ShopEntry> getItems() {
        return this.items;
    }

}
