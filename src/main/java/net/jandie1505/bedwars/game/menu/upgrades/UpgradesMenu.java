package net.jandie1505.bedwars.game.menu.upgrades;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
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

    public Inventory getUpgradesMenu() {
        Inventory inventory = this.game.getPlugin().getServer().createInventory(this, 54, "Team Upgrades");

        PlayerData playerData = this.game.getPlayers().get(this.playerId);

        if (playerData == null) {
            return this.getInventory();
        }

        BedwarsTeam team = this.game.getTeams().get(playerData.getTeam());

        if (team == null) {
            return this.getInventory();
        }

        TeamUpgrade sharpnessUpgrade = this.game.getTeamUpgradesConfig().getSharpnessUpgrade();
        ItemStack sharpnessItem = this.game.getPlugin().getItemStorage().getItem(sharpnessUpgrade.getItemId());
        this.createUpgradeItem(sharpnessItem, sharpnessUpgrade, team.getAttackDamageUpgrade(), "Sharpness");
        inventory.setItem(10, sharpnessItem);

        TeamUpgrade protectionUpgrade = this.game.getTeamUpgradesConfig().getProtectionUpgrade();
        ItemStack protectionItem = this.game.getPlugin().getItemStorage().getItem(protectionUpgrade.getItemId());
        this.createUpgradeItem(protectionItem, protectionUpgrade, team.getProtectionUpgrade(), "Protection");
        inventory.setItem(11, protectionItem);

        TeamUpgrade hasteUpgrade = this.game.getTeamUpgradesConfig().getHasteUpgrade();
        ItemStack hasteItem = this.game.getPlugin().getItemStorage().getItem(hasteUpgrade.getItemId());
        this.createUpgradeItem(hasteItem, hasteUpgrade, team.getHasteUpgrade(), "Haste");
        inventory.setItem(12, hasteItem);

        TeamUpgrade forgeUpgrade = this.game.getTeamUpgradesConfig().getForgeUpgrade();
        ItemStack forgeItem = this.game.getPlugin().getItemStorage().getItem(forgeUpgrade.getItemId());
        this.createUpgradeItem(forgeItem, forgeUpgrade, team.getForgeUpgrade(), "Generator");
        inventory.setItem(19, forgeItem);

        TeamUpgrade healPoolUpgrade = this.game.getTeamUpgradesConfig().getHealPoolUpgrade();
        ItemStack healPoolItem = this.game.getPlugin().getItemStorage().getItem(healPoolUpgrade.getItemId());
        this.createUpgradeItem(healPoolItem, healPoolUpgrade, team.getHealPoolUpgrade(), "Heal Pool");
        inventory.setItem(20, healPoolItem);

        TeamUpgrade dragonBuffUpgrade = this.game.getTeamUpgradesConfig().getDragonBuffUpgrade();
        ItemStack dragonBuffItem = this.game.getPlugin().getItemStorage().getItem(dragonBuffUpgrade.getItemId());
        this.createUpgradeItem(dragonBuffItem, dragonBuffUpgrade, team.getDragonBuffUpgrade(), "Dragon Buff");
        inventory.setItem(21, dragonBuffItem);

        return inventory;
    }

    private void createUpgradeItem(ItemStack item, TeamUpgrade upgrade, int upgradeLevel, String upgradeName) {

        if (item == null) {
            return;
        }

        item.setAmount(upgradeLevel + 1);

        if (upgrade.getUpgradeLevels().size() != upgrade.getUpgradePrices().size() || upgrade.getUpgradeLevels().size() != upgrade.getUpgradePriceCurrencies().size()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        for (int i = 0; i < upgrade.getUpgradeLevels().size(); i++) {

            int level = upgrade.getUpgradeLevels().get(i);
            int price = upgrade.getUpgradePrices().get(i);
            Material currency = upgrade.getUpgradePriceCurrencies().get(i);

            String upgradeMessage = "Tier " + (i + 1) + ": ";

            if (level < 0) {
                upgradeMessage = upgradeMessage + upgradeName;
            } else {
                upgradeMessage = upgradeMessage + upgradeName + " " + level;
            }

            if (upgradeLevel - 1 >= i) {
                upgradeMessage = "§r§a" + upgradeMessage;
                upgradeMessage = upgradeMessage + " UNLOCKED";
            } else {
                upgradeMessage = "§r§7" + upgradeMessage;
                upgradeMessage = upgradeMessage + " §b" + price + " " + currency.toString() + "S";
            }

            lore.add(upgradeMessage);

        }

        meta.setLore(lore);

        item.setItemMeta(meta);

    }

}
