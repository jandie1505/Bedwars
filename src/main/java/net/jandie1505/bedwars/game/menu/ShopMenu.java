package net.jandie1505.bedwars.game.menu;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ShopMenu implements InventoryHolder {
    private final Game game;

    public ShopMenu(Game game) {
        this.game = game;
    }

    @Override
    public Inventory getInventory() {
        return this.game.getPlugin().getServer().createInventory(this, 9, "§c§mShopMenu");
    }

    private Inventory getInventoryBase(String name) {
        Inventory inventory = this.game.getPlugin().getServer().createInventory(this, 54, "Item Shop (" + name + ")");

        for (int i = 0; i < 9; i++) {

            Integer[] menuItems = this.game.getItemShop().getMenuItems();

            if (menuItems[i] == null || menuItems[i] < 0) {
                continue;
            }

            ItemStack item = this.game.getPlugin().getItemStorage().getItem(menuItems[i]);

            if (item == null) {
                continue;
            }

            inventory.setItem(i, item);

        }

        return inventory;
    }

    public Inventory getPage(int page) {
        Inventory inventory = this.getInventoryBase("Page " + page);

        for (ShopEntry shopEntry : this.game.getItemShop().getPage(page)) {

            if (shopEntry.getItem() == null || shopEntry.getSlot() < 9 || shopEntry.getSlot() > 53) {
                continue;
            }

            inventory.setItem(shopEntry.getSlot(), shopEntry.getItem());

        }

        return inventory;
    }
}
