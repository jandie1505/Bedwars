package net.jandie1505.bedwars.game.menu.shop;

import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UpgradeEntry {
    private final ItemShop itemShop;
    private final List<Integer> upgradeItemIds;
    private final List<Integer> upgradePrices;
    private final List<Material> upgradeCurrencies;
    private final List<int[]> slots;

    public UpgradeEntry(ItemShop itemShop, List<Integer> upgradeItemIds, List<Integer> upgradePrices, List<Material> upgradeCurrencies, List<int[]> slots) {
        this.itemShop = itemShop;
        this.upgradeItemIds = List.copyOf(upgradeItemIds);
        this.upgradePrices = List.copyOf(upgradePrices);
        this.upgradeCurrencies = List.copyOf(upgradeCurrencies);
        this.slots = List.copyOf(slots);
    }

    public List<Integer> getUpgradeItemIds() {
        return List.copyOf(this.upgradeItemIds);
    }

    public int getItemId(int upgradeLevel) {

        upgradeLevel--;

        if (upgradeLevel < 0) {
            return -3;
        }

        if (this.upgradeItemIds.isEmpty()) {
            return -1;
        }

        if (upgradeLevel >= this.upgradeItemIds.size()) {
            return -2;
        }

        return this.upgradeItemIds.get(upgradeLevel);
    }

    public int getUpgradePrice(int upgradeLevel) {

        upgradeLevel--;

        if (upgradeLevel < 0) {
            return -3;
        }

        if (this.upgradePrices.isEmpty()) {
            return -1;
        }

        if (upgradeLevel >= this.upgradePrices.size()) {
            return -2;
        }

        return this.upgradePrices.get(upgradeLevel);
    }

    public Material getUpgradeCurrency(int upgradeLevel) {

        upgradeLevel--;

        if (upgradeLevel < 0) {
            return null;
        }

        if (this.upgradeCurrencies.isEmpty()) {
            return null;
        }

        if (upgradeLevel >= this.upgradeCurrencies.size()) {
            return null;
        }

        return this.upgradeCurrencies.get(upgradeLevel);
    }

    public int getUpgradeLevel(PlayerData playerData) {

        if (this.itemShop.getArmorUpgrade() == this) {
            return playerData.getArmorUpgrade();
        } else if (this.itemShop.getPickaxeUpgrade() == this) {
            return playerData.getPickaxeUpgrade();
        } else if (this.itemShop.getShearsUpgrade() == this) {
            return playerData.getShearsUpgrade();
        } else {
            return 0;
        }

    }

    public void upgradePlayer(PlayerData playerData) {

        if (this.itemShop.getArmorUpgrade() == this) {
            playerData.setArmorUpgrade(playerData.getArmorUpgrade() + 1);
        } else if (this.itemShop.getPickaxeUpgrade() == this) {
            playerData.setPickaxeUpgrade(playerData.getPickaxeUpgrade() + 1);
        } else if (this.itemShop.getShearsUpgrade() == this) {
            playerData.setShearsUpgrade(playerData.getShearsUpgrade() + 1);
        }

    }

    public ItemStack getItem(int upgradeLevel) {
        int itemId = this.getItemId(upgradeLevel);
        boolean toHighValue = false;

        if (itemId == -2) {
            itemId = this.getItemId(this.upgradeItemIds.size());
            toHighValue = true;
        }

        ItemStack item = this.itemShop.getGame().getPlugin().getItemStorage().getItem(itemId);

        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        if (toHighValue) {
            lore.add(1, "§r§aAlready unlocked");
        } else {
            lore.add(1, "§r§fPrice: §a" + this.getUpgradePrice(upgradeLevel) + " " + this.getUpgradeCurrency(upgradeLevel).name() + "s");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public ItemShop getItemShop() {
        return this.itemShop;
    }

    public List<int[]> getSlots() {
        return List.copyOf(slots);
    }
}
