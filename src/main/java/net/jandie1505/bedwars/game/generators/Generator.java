package net.jandie1505.bedwars.game.generators;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Generator {
    private final Game game;
    private final GeneratorData data;
    private final int maxNearbyItems;
    private int generatorTimer;

    public Generator(Game game, GeneratorData data) {
        this.game = game;
        this.data = data;
        this.maxNearbyItems = 5;
        this.generatorTimer = 0;
    }

    public abstract boolean isEnabled();

    public abstract int getLevel();

    public double getSpeed() {
        int level = this.getLevel();

        if (this.data.upgradeSteps().isEmpty()) {
            return 0;
        }

        if (level >= this.data.upgradeSteps().size()) {
            level = (this.data.upgradeSteps().size() - 1);
        }

        return this.data.upgradeSteps().get(level);
    }

    public Game getGame() {
        return this.game;
    }

    public GeneratorData getData() {
        return data;
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

        boolean dropItem = true;

        for (Entity entity : List.copyOf(this.game.getWorld().getNearbyEntities(WorldUtils.locationWithWorld(this.data.location(), this.game.getWorld()), 1, 1, 1))) {

            if (!(entity instanceof Player)) {
                continue;
            }

            dropItem = false;

            Player player = (Player) entity;

            this.data.item().setAmount(amount);
            player.getInventory().addItem(this.data.item());

        }

        if (dropItem) {

            int nearbyItemAmount = 0;
            for (Entity entity : List.copyOf(this.game.getWorld().getNearbyEntities(WorldUtils.locationWithWorld(this.data.location(), this.game.getWorld()), 5, 5, 5))) {
                if (entity instanceof Item && ((Item) entity).getItemStack().getType() == this.data.item().getType()) {
                    nearbyItemAmount++;
                }
            }

            if (nearbyItemAmount >= this.maxNearbyItems) {
                return;
            }

            ItemStack item = this.data.item();

            item.setAmount(amount);

            this.game.getWorld().dropItem(WorldUtils.locationWithWorld(this.data.location(), this.game.getWorld()), item);

        }

    }

    public int getGeneratorTimer() {
        return generatorTimer;
    }
}
