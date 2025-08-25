package net.jandie1505.bedwars.game.game.shop;

import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.menu.shop.ShopEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemShop {
    @NotNull private final Game game;
    @NotNull private final Map<String, ShopEntry> items;

    public ItemShop(@NotNull Game game) {
        this.game = game;
        this.items = new HashMap<>();

        this.items.putAll(DefaultConfigValues.getDefaultShopEntries(game.getPlugin()));
    }

    public @Nullable ShopEntry getItem(@NotNull String itemId) {
        return this.items.get(itemId);
    }

    public @NotNull Map<String, ShopEntry> getItems() {
        return this.items;
    }

}
