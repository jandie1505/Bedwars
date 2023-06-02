package net.jandie1505.bedwars.game.generators;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Generator {
    private final Game game;
    private final ItemStack item;
    private final Location location;
    private final int maxNearbyItems;
    private final List<Double> upgradeSteps;
    private int generatorTimer;

    public Generator(Game game, ItemStack item, Location location, int maxNearbyItems, List<Double> upgradeSteps) {
        this.game = game;
        this.item = item;
        this.location = location;
        this.maxNearbyItems = maxNearbyItems;
        this.upgradeSteps = new ArrayList<>(upgradeSteps);
        this.generatorTimer = 0;
    }

    public abstract boolean isEnabled();

    public abstract int getLevel();

    public double getSpeed() {
        int level = this.getLevel();

        if (this.upgradeSteps.size() == 0) {
            return 0;
        }

        if (level >= this.upgradeSteps.size()) {
            level = (this.upgradeSteps.size() - 1);
        }

        return this.upgradeSteps.get(level);
    }

    public Game getGame() {
        return this.game;
    }

    public void tick() {

        if (!this.isEnabled()) {
            return;
        }

        double speed = this.getSpeed();

        if (speed <= 0) {
            return;
        }

        if (this.generatorTimer >= speed) {

            if (speed < 1.0) {
                this.spawnItem((int) Math.round(1.0d / speed));
            } else {
                this.spawnItem(1);
            }

            this.generatorTimer = 0;
        }

        this.generatorTimer++;
    }

    public void spawnItem(int amount) {

        if (amount <= 0) {
            return;
        }

        int nearbyItemAmount = 0;
        for (Entity entity : List.copyOf(this.game.getWorld().getNearbyEntities(this.location, 5, 5, 5))) {
            if (entity instanceof Item && ((Item) entity).getItemStack().getType() == this.item.getType()) {
                nearbyItemAmount++;
            }
        }

        if (nearbyItemAmount >= this.maxNearbyItems) {
            return;
        }

        ItemStack item = this.item;

        item.setAmount(amount);

        this.game.getWorld().dropItem(this.location, item);
    }

}
