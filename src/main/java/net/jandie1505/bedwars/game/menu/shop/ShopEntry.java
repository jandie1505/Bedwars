package net.jandie1505.bedwars.game.menu.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an item in the shop.
 * @param item
 * @param currency
 * @param price
 */
public record ShopEntry(
        @NotNull ItemStack item,
        @NotNull Material currency,
        int price
) {

    public ShopEntry(@NotNull ItemStack item, @NotNull Material currency, int price) {
        this.item = item.clone();
        this.currency = currency;
        this.price = price;
    }

    @Override
    public ItemStack item() {
        return this.item.clone();
    }

}
