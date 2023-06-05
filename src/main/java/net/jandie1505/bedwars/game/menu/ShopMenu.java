package net.jandie1505.bedwars.game.menu;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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

        for (ShopEntry shopEntry : this.game.getItemShop().getShopEntryPage(page)) {

            if (shopEntry.getItem() == null || shopEntry.getSlot() < 9 || shopEntry.getSlot() > 53) {
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

                if (slotData[0] != page || slotData[1] < 9 || slotData[1] > 53) {
                    continue;
                }

                inventory.setItem(slotData[1], itemStack);

            }

        }

        return inventory;
    }
}
