package net.jandie1505.bedwars.game.menu.shop;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ShopMenu implements InventoryHolder {
    private final Game game;
    private final UUID playerId;

    public ShopMenu(Game game, UUID playerId) {
        this.game = game;
        this.playerId = playerId;
    }

    @Override
    public Inventory getInventory() {
        return this.game.getPlugin().getServer().createInventory(this, 9, "§c§mShopMenu");
    }

    private Inventory getInventoryBase(int page) {
        Inventory inventory = this.game.getPlugin().getServer().createInventory(this, 54, "Item Shop (" + page + ")");

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

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS);
        ItemMeta meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(item.getType());
        meta.setDisplayName(String.valueOf(page));
        item.setItemMeta(meta);

        inventory.setItem(53, item);

        return inventory;
    }

    public Inventory getPage(int page) {

        if (page < 0) {
            return this.getInventory();
        }

        Inventory inventory = this.getInventoryBase(page);

        for (ShopEntryOld shopEntry : this.game.getItemShop().getShopEntryPage(page)) {

            if (shopEntry.getItem() == null || shopEntry.getSlot() < 9 || shopEntry.getSlot() > 52) {
                continue;
            }

            inventory.setItem(shopEntry.getSlot(), shopEntry.getItem());

        }

        for (UpgradeEntry upgradeEntry : this.game.getItemShop().getUpgradeEntryPage(page)) {

            PlayerData playerData = this.game.getPlayers().get(playerId);

            if (playerData == null) {
                continue;
            }

            ItemStack itemStack = upgradeEntry.getItem(upgradeEntry.getUpgradeLevel(playerData) + 1);

            if (itemStack == null) {
                continue;
            }

            for (int[] slotData : upgradeEntry.getSlots()) {

                if (slotData[0] != page || slotData[1] < 9 || slotData[1] > 52) {
                    continue;
                }

                inventory.setItem(slotData[1], itemStack);

            }

        }

        return inventory;
    }

    public static int getMenuPage(Inventory inventory) {

        if (inventory == null) {
            return -1;
        }

        ItemStack item = inventory.getItem(53);

        if (item == null || item.getItemMeta() == null) {
            return -1;
        }

        try {
            return Integer.parseInt(item.getItemMeta().getDisplayName());
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }
}
