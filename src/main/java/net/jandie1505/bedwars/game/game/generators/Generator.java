package net.jandie1505.bedwars.game.game.generators;

import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.json.JSONConfigUtils;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

public abstract class Generator {
    @NotNull private final Game game;

    @NotNull private final Location location;
    @NotNull private final ItemStack item;
    private final int maxNearbyItems;

    private int generatorTimer;

    public Generator(@NotNull Game game, @NotNull Data data) {
        this.game = game;

        this.location = WorldUtils.locationWithWorld(data.location(), this.game.getWorld());
        this.item = data.item().clone();

        this.maxNearbyItems = 5;
        this.generatorTimer = 0;
    }

    // ----- GET VALUES -----

    public abstract boolean isEnabled();

    /**
     * Returns n.
     * The generator drops an item every n ticks.
     * @return speed
     */
    public abstract int getSpawnRate();

    /**
     * Returns the amount of items the generator will drop.
     * @return amount
     */
    public abstract int getAmount();

    public final void tick() {
        if (!this.isEnabled()) return;

        int spawnRate = this.getSpawnRate();
        if (spawnRate <= 0) return;

        this.generatorTimer++;

        if (this.generatorTimer >= spawnRate) {
            this.generatorTimer = 0;
            this.spawnItem();
        }

    }

    public final void spawnItem() {

        if (this.item.getType() == Material.AIR) return;
        if (this.item.getAmount() <= 0) return;

        boolean dropItem = true;

        // Give items to players who are standing on top of the generator.
        for (Player player : List.copyOf(this.game.getWorld().getNearbyEntitiesByType(Player.class, this.location.clone(), 1, 1, 1))) {
            dropItem = false;
            player.getInventory().addItem(this.getDroppableItem());
        }

        if (!dropItem) return; // At least one player on top of the generator (receiving items directly into their inventory --> Don't drop
        if (this.getNearbyItemsCount() >= this.maxNearbyItems) return; // Nearby items limit reached --> Don't drop the item

        this.game.getWorld().dropItem(WorldUtils.locationWithWorld(this.location.clone(), this.game.getWorld()), this.getDroppableItem());
    }

    private @NotNull ItemStack getDroppableItem() {
        ItemStack item = this.item.clone();
        item.setAmount(Math.min(item.getAmount() * this.getAmount(), item.getMaxStackSize()));
        return item;
    }

    private int getNearbyItemsCount() {
        int nearbyItemAmount = 0;

        for (Item item : List.copyOf(this.game.getWorld().getNearbyEntitiesByType(Item.class, this.location.clone(), 5, 5, 5))) {
            if (!item.getItemStack().isSimilar(this.item)) continue;
            nearbyItemAmount++;
        }

        return nearbyItemAmount;
    }

    // ----- OTHER -----

    public final @NotNull Game getGame() {
        return this.game;
    }

    public @NotNull Location getLocation() {
        return this.location.clone();
    }

    public final @NotNull ItemStack getItem() {
        return this.item.clone();
    }

    public final int getMaxNearbyItems() {
        return this.maxNearbyItems;
    }

    public final int getGeneratorTimer() {
        return this.generatorTimer;
    }

    // ----- INNER CLASSES -----

    public record Data(
            @NotNull Location location,
            @NotNull ItemStack item,
            int maxNearbyItems
    ) {

        public Data(@NotNull Location location, @NotNull ItemStack item, int maxNearbyItems) {
            this.location = location.clone();
            this.item = item.clone();
            this.maxNearbyItems = maxNearbyItems;
        }

        @Override
        public @NotNull Location location() {
            return this.location.clone();
        }

        @Override
        public ItemStack item() {
            return item.clone();
        }

        public JSONObject serializeToJSON() {
            JSONObject json = new JSONObject();

            json.put("location", JSONConfigUtils.locationToJSONObject(this.location));
            json.put("item", JSONConfigUtils.serializeItem(this.item));
            json.put("max_nearby_items", this.maxNearbyItems);

            return json;
        }

        public static Data deserializeFromJSON(JSONObject json) {

            Location location = JSONConfigUtils.jsonObjectToLocation(json.getJSONObject("location"));
            if (location == null) throw new IllegalArgumentException("invalid location");

            ItemStack item = JSONConfigUtils.deserializeItem(json.getJSONObject("item"));

            int maxNearbyItems = json.getInt("max_nearby_items");

            return new Data(location, item, maxNearbyItems);
        }

    }

}
