package net.jandie1505.bedwars.old.game.menu.upgrades;

import net.jandie1505.bedwars.old.game.Game;
import net.jandie1505.bedwars.old.game.player.PlayerData;
import net.jandie1505.bedwars.old.game.team.BedwarsTeam;
import net.jandie1505.bedwars.old.game.team.TeamUpgrade;
import net.jandie1505.bedwars.old.game.team.traps.BedwarsTrap;
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

        // Team Upgrades

        TeamUpgrade sharpnessUpgrade = this.game.getTeamUpgradesConfig().getSharpnessUpgrade();
        ItemStack sharpnessItem = this.game.getPlugin().getItemStorage().getItem(sharpnessUpgrade.getItemId());
        this.createUpgradeItem(sharpnessItem, sharpnessUpgrade, team.getAttackDamageUpgrade(), "Sharpness ");
        inventory.setItem(10, sharpnessItem);

        TeamUpgrade protectionUpgrade = this.game.getTeamUpgradesConfig().getProtectionUpgrade();
        ItemStack protectionItem = this.game.getPlugin().getItemStorage().getItem(protectionUpgrade.getItemId());
        this.createUpgradeItem(protectionItem, protectionUpgrade, team.getProtectionUpgrade(), "Protection ");
        inventory.setItem(11, protectionItem);

        TeamUpgrade hasteUpgrade = this.game.getTeamUpgradesConfig().getHasteUpgrade();
        ItemStack hasteItem = this.game.getPlugin().getItemStorage().getItem(hasteUpgrade.getItemId());
        this.createUpgradeItem(hasteItem, hasteUpgrade, team.getHasteUpgrade(), "Haste ");
        inventory.setItem(12, hasteItem);

        TeamUpgrade forgeUpgrade = this.game.getTeamUpgradesConfig().getForgeUpgrade();
        ItemStack forgeItem = this.game.getPlugin().getItemStorage().getItem(forgeUpgrade.getItemId());
        this.createUpgradeItem(forgeItem, forgeUpgrade, team.getForgeUpgrade(), "Generator ");
        inventory.setItem(19, forgeItem);

        TeamUpgrade healPoolUpgrade = this.game.getTeamUpgradesConfig().getHealPoolUpgrade();
        ItemStack healPoolItem = this.game.getPlugin().getItemStorage().getItem(healPoolUpgrade.getItemId());
        this.createUpgradeItem(healPoolItem, healPoolUpgrade, team.getHealPoolUpgrade(), "Heal Pool ");
        inventory.setItem(20, healPoolItem);

        TeamUpgrade dragonBuffUpgrade = this.game.getTeamUpgradesConfig().getEndgameBuffUpgrade();
        ItemStack dragonBuffItem = this.game.getPlugin().getItemStorage().getItem(dragonBuffUpgrade.getItemId());
        this.createUpgradeItem(dragonBuffItem, dragonBuffUpgrade, team.getEndgameBuffUpgrade(), "Wither x");
        inventory.setItem(21, dragonBuffItem);

        // Buy Traps Button

        inventory.setItem(14, this.createTrapPurchaseButton(this.game.getTeamUpgradesConfig().getAlarmTrap(), team));
        inventory.setItem(15, this.createTrapPurchaseButton(this.game.getTeamUpgradesConfig().getItsATrap(), team));
        inventory.setItem(16, this.createTrapPurchaseButton(this.game.getTeamUpgradesConfig().getMiningFatigueTrap(), team));
        inventory.setItem(23, this.createTrapPurchaseButton(this.game.getTeamUpgradesConfig().getCountermeasuresTrap(), team));

        // Active Traps

        ItemStack noTrapItem = this.game.getPlugin().getItemStorage().getItem(this.game.getTeamUpgradesConfig().getNoTrap());

        BedwarsTrap trap1 = team.getPrimaryTraps()[0];

        if (trap1 != null) {
            inventory.setItem(38, createTrapDisplayItem(trap1));
        } else {
            inventory.setItem(38, noTrapItem);
        }

        BedwarsTrap trap2 = team.getPrimaryTraps()[1];

        if (trap2 != null) {
            inventory.setItem(39, createTrapDisplayItem(trap2));
        } else {
            inventory.setItem(39, noTrapItem);
        }

        BedwarsTrap trap3 = team.getSecondaryTraps()[0];

        if (trap3 != null) {
            inventory.setItem(41, createTrapDisplayItem(trap3));
        } else {
            inventory.setItem(41, noTrapItem);
        }

        BedwarsTrap trap4 = team.getSecondaryTraps()[1];

        if (trap4 != null) {
            inventory.setItem(42, createTrapDisplayItem(trap4));
        } else {
            inventory.setItem(42, noTrapItem);
        }

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
                upgradeMessage = upgradeMessage + upgradeName + level;
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

    public ItemStack createTrapPurchaseButton(int itemId, BedwarsTeam team) {

        ItemStack item = this.game.getPlugin().getItemStorage().getItem(itemId);

        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return new ItemStack(Material.AIR);
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        int primaryPrice = getTrapPrice(false, team);

        int secondaryPrice = getTrapPrice(true, team);

        lore.add("");

        if (primaryPrice < 0) {
            lore.add("§r§7Buy as primary:");
            lore.add("§r§cSlots full");
        } else {
            lore.add("§r§7Buy as primary:");
            lore.add("§r§b" + primaryPrice + " " + Material.DIAMOND.name() + "S");
        }

        lore.add("");

        if (secondaryPrice < 0) {
            lore.add("§r§7Buy as secondary:");
            lore.add("§r§cSlots full");
        } else {
            lore.add("§r§7Buy as secondary:");
            lore.add("§r§b" + secondaryPrice + " " + Material.DIAMOND.name() + "S");
        }

        lore.add("");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createTrapDisplayItem(BedwarsTrap trap) {

        if (trap == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = this.game.getPlugin().getItemStorage().getItem(trap.getItemId());

        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return new ItemStack(Material.AIR);
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add("");
        lore.add("§r§aRight-click to delete");
        lore.add("");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static int getTrapPrice(boolean secondary, BedwarsTeam team) {

        if (secondary) {
            if (BedwarsTeam.getTrapsCount(team.getSecondaryTraps()) == 0) {
                return  2;
            } else if (BedwarsTeam.getTrapsCount(team.getSecondaryTraps()) == 1) {
                return  8;
            } else {
                return  -1;
            }
        } else {
            if (BedwarsTeam.getTrapsCount(team.getPrimaryTraps()) == 0) {
                return  1;
            } else if (BedwarsTeam.getTrapsCount(team.getPrimaryTraps()) == 1) {
                return  4;
            } else {
                return  -1;
            }
        }

    }

}
