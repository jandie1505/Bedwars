package net.jandie1505.bedwars.game.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ShopEntry {
    private final ItemShop itemShop;
    private final int itemId;
    private final int price;
    private final Material currency;
    private final int page;
    private final int slot;

    public ShopEntry(ItemShop itemShop, int itemId, int price, Material currency, int page, int slot) {
        this.itemShop = itemShop;
        this.itemId = itemId;
        this.price = price;
        this.currency = currency;
        this.page = page;
        this.slot = slot;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public Material getCurrency() {
        return currency;
    }

    public int getPage() {
        return page;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        ItemStack item = this.itemShop.getGame().getPlugin().getItemStorage().getItem(this.itemId);

        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        lore.add(1, "§rPrice: §a" + this.price + " " + this.itemShop.getGame().getPlugin().getServer().getItemFactory().getItemMeta(this.currency).getDisplayName());
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}
