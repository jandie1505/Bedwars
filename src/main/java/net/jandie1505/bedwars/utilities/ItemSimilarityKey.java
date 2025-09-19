package net.jandie1505.bedwars.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ItemSimilarityKey(@NotNull Material type, @Nullable ItemMeta meta) {

    public ItemSimilarityKey(@NotNull Material type, @Nullable ItemMeta meta) {
        this.type = type;
        this.meta = meta != null ? meta.clone() : null;
    }

    @Override
    public @Nullable ItemMeta meta() {
        return this.meta != null ? this.meta.clone() : null;
    }

    @Override
    public @NotNull String toString() {
        return "ItemSimilarityKey[type=" + this.type + ", meta=" + this.meta + "]";
    }

    public static @NotNull ItemSimilarityKey of(@NotNull ItemStack item) {
        return new ItemSimilarityKey(item.getType(), item.getItemMeta());
    }

}
