package net.jandie1505.bedwars.game.game.team.gui.enderchest_gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The gui manager for the resource storage.<br/>
 * Since the resource storage can be accessed on 2 different locations, this class manages and builds the ui, but don't provides the inventory where it is inserted.<br/>
 * To make the listener work,
 */
public class ResourceStorageGUIManager implements ManagedListener {
    @NotNull public static final NamespacedKey EVENT_LISTENER_ACTIVE = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.resource_storage_ui.event_listener_active");
    @NotNull public static final NamespacedKey ITEM_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.resource_storage_ui.item_id");
    @NotNull public static final NamespacedKey ITEM_AMOUNT = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.resource_storage_ui.item_amount");
    public static final int INVENTORY_PART_SIZE = 27;

    @NotNull private final Game game;
    @NotNull private final InventoryHolder inventoryHolder;
    @NotNull private final Removable removeCondition;

    public ResourceStorageGUIManager(@NotNull Game game, @NotNull InventoryHolder inventoryHolder, @Nullable Removable removeCondition) {
        this.game = game;
        this.inventoryHolder = inventoryHolder;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;

        this.game.registerListener(this);
    }

    // ----- INVENTORY BUILDER -----

    public @NotNull Inventory buildInventory(@NotNull Inventory inventory, int row, @NotNull BedwarsTeam team) {

        int zeroSlot;
        switch (row) {
            case 0 -> zeroSlot = 0;
            case 1 -> zeroSlot = 9;
            case 2 -> zeroSlot = 18;
            case 3 -> zeroSlot = 27;
            case 4 -> zeroSlot = 36;
            case 5 -> zeroSlot = 45;
            default -> throw new IllegalArgumentException("Invalid row: " + row);
        }

        if (zeroSlot + INVENTORY_PART_SIZE > inventory.getSize()) throw new IllegalArgumentException("Inventory too small");

        int availableAmount = 0;

        // Iron
        if (team.getTeamChestLevel() >= 1) {
            inventory.setItem(zeroSlot + 1, buildStorableItem("iron", availableAmount, 1, new ItemStack(Material.IRON_INGOT)));
            inventory.setItem(zeroSlot + 10, buildStorableItem("iron", availableAmount, 16, new ItemStack(Material.IRON_INGOT)));
            inventory.setItem(zeroSlot + 19, buildStorableItem("iron", availableAmount, 32, new ItemStack(Material.IRON_INGOT)));
        } else {
            inventory.setItem(zeroSlot + 1, buildBlockedItem(1));
            inventory.setItem(zeroSlot + 10, buildBlockedItem(1));
            inventory.setItem(zeroSlot + 19, buildBlockedItem(1));
        }

        // Gold
        if (team.getTeamChestLevel() >= 2) {
            inventory.setItem(zeroSlot + 3, buildStorableItem("gold", availableAmount, 1, new ItemStack(Material.GOLD_INGOT)));
            inventory.setItem(zeroSlot + 12, buildStorableItem("gold", availableAmount, 16, new ItemStack(Material.GOLD_INGOT)));
            inventory.setItem(zeroSlot + 21, buildStorableItem("gold", availableAmount, 32, new ItemStack(Material.GOLD_INGOT)));
        } else {
            inventory.setItem(zeroSlot + 3, buildBlockedItem(2));
            inventory.setItem(zeroSlot + 12, buildBlockedItem(2));
            inventory.setItem(zeroSlot + 21, buildBlockedItem(2));
        }

        // DIAMOND
        if (team.getTeamChestLevel() >= 4) {
            inventory.setItem(zeroSlot + 5, buildStorableItem("diamond", availableAmount, 1, new ItemStack(Material.DIAMOND)));
            inventory.setItem(zeroSlot + 14, buildStorableItem("diamond", availableAmount, 16, new ItemStack(Material.DIAMOND)));
            inventory.setItem(zeroSlot + 23,  buildStorableItem("diamond", availableAmount, 32, new ItemStack(Material.DIAMOND)));
        } else {
            inventory.setItem(zeroSlot + 5, buildBlockedItem(4));
            inventory.setItem(zeroSlot + 14, buildBlockedItem(4));
            inventory.setItem(zeroSlot + 23, buildBlockedItem(4));
        }

        // EMERALD
        if (team.getTeamChestLevel() >= 5) {
            inventory.setItem(zeroSlot + 7, buildStorableItem("emerald", availableAmount, 1, new ItemStack(Material.EMERALD)));
            inventory.setItem(zeroSlot + 16, buildStorableItem("emerald", availableAmount, 16, new ItemStack(Material.EMERALD)));
            inventory.setItem(zeroSlot + 25, buildStorableItem("emerald", availableAmount, 32, new ItemStack(Material.EMERALD)));
        } else {
            inventory.setItem(zeroSlot + 7, buildBlockedItem(5));
            inventory.setItem(zeroSlot + 16, buildBlockedItem(5));
            inventory.setItem(zeroSlot + 25, buildBlockedItem(5));
        }

        for (int slot = zeroSlot; slot < Math.min(inventory.getSize(), zeroSlot + INVENTORY_PART_SIZE); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null) continue;
            inventory.setItem(slot, buildPlaceholderItem());
        }

        return inventory;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this.inventoryHolder) return;
        if (!isListenerEnabled(event.getInventory())) return;
        event.setCancelled(true);


    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this.inventoryHolder) return;
        if (!isListenerEnabled(event.getInventory())) return;
        event.setCancelled(true);


    }

    // ----- LISTENER MANAGEMENT -----

    /**
     * Returns if the listener is enabled for the specified inventory.<br/>
     * Make sure that the information is stored in the item slot 0.
     * @param inventory inventory to check for
     * @return listener enabled
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isListenerEnabled(@NotNull Inventory inventory) {
        ItemStack item = inventory.getItem(0);
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getPersistentDataContainer().getOrDefault(EVENT_LISTENER_ACTIVE, PersistentDataType.BOOLEAN, false);
    }

    /**
     * Sets if the listener should be enabled for the specified inventory.<br/>
     * This information is stored in the item slot 0, so make sure that the Persistent Data Container of that item is not replaced.
     * @param inventory inventory
     * @param enabled enabled
     */
    public static void setListenerEnabled(@NotNull Inventory inventory, boolean enabled) {

        ItemStack item = inventory.getItem(0);
        if (item == null || item.getType() == Material.AIR) item = buildPlaceholderItem();

        item.editPersistentDataContainer(container -> container.set(EVENT_LISTENER_ACTIVE, PersistentDataType.BOOLEAN, enabled));

        inventory.setItem(0, item);
    }

    // ----- ITEMS -----

    public static @NotNull ItemStack buildStorableItem(@NotNull String id, int availableAmount, int getOrStoreAmount, @NotNull ItemStack item) {
        item = item.clone();

        item.editPersistentDataContainer(container -> {
            container.set(ITEM_ID, PersistentDataType.STRING, id);
            container.set(ITEM_AMOUNT, PersistentDataType.INTEGER, getOrStoreAmount);
        });

        item.setAmount(getOrStoreAmount);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        if (!(lore instanceof ArrayList<Component>)) lore = new ArrayList<>(lore);

        if (!lore.isEmpty()) lore.add(Component.empty());

        lore.add(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Available amount: " + availableAmount + " items.", NamedTextColor.GOLD)));
        lore.add(Component.empty());
        lore.add(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("- Right-click to store " + getOrStoreAmount + " items.", NamedTextColor.GOLD)));
        lore.add(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("- Left-click to take " + getOrStoreAmount + " items.", NamedTextColor.GOLD)));

        item.setItemMeta(meta);

        return item;
    }

    public static @NotNull ItemStack buildBlockedItem(int unlockLevel) {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);

        item.setData(DataComponentTypes.CUSTOM_NAME, ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("N/A", NamedTextColor.RED)));
        item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Requires Team Chest Lvl. " + unlockLevel, NamedTextColor.RED)))));

        return item;
    }

    public static @NotNull ItemStack buildPlaceholderItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(" "));
        return item;
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }

}
