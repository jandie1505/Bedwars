package net.jandie1505.bedwars.game.game.team;

import org.bukkit.Material;

import java.util.List;

@Deprecated(forRemoval = true)
public class TeamUpgrade {
    private final int itemId;
    private final List<Integer> upgradePrices;
    private final List<Material> upgradePriceCurrencies;
    private final List<Integer> upgradeLevels;

    public TeamUpgrade(int itemId, List<Integer> upgradePrices, List<Material> upgradePriceCurrencies, List<Integer> upgradeLevels) {
        this.itemId = itemId;
        this.upgradePrices = List.copyOf(upgradePrices);
        this.upgradePriceCurrencies = List.copyOf(upgradePriceCurrencies);
        this.upgradeLevels = List.copyOf(upgradeLevels);
    }

    public int getItemId() {
        return this.itemId;
    }

    public List<Integer> getUpgradePrices() {
        return List.copyOf(this.upgradePrices);
    }

    public List<Material> getUpgradePriceCurrencies() {
        return List.copyOf(this.upgradePriceCurrencies);
    }

    public List<Integer> getUpgradeLevels() {
        return List.copyOf(this.upgradeLevels);
    }
}
