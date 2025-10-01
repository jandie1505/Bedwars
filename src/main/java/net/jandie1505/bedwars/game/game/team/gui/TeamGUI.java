package net.jandie1505.bedwars.game.game.team.gui;

import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.shop.entries.ShopGUIPosition;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeamGUI implements ManagedListener, InventoryHolder {

    // The current page, which is stored in the quick buy item (slot 0).
    @NotNull public static NamespacedKey MENU_CURRENT_PAGE = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.current_page");
    // The team id the menu is currently opened for.
    @NotNull public static NamespacedKey MENU_TEAM_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.team_id");

    // The status of the free mode (all items can be purchased without cost), which is stored in the quick buy item (slot 0).
    @NotNull public static NamespacedKey MENU_FREE_MODE = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.free_mode");
    // The status of full access. If team locking (which is currently not implemented) is enabled, players from other teams will have limited access to the team gui of other teams.
    @NotNull public static NamespacedKey MENU_FULL_ACCESS = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.team_gui.full_access");
    // The menu bar page id, which is stored in all menu bar items
    @NotNull public static NamespacedKey MENU_BAR_PAGE_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.menu_bar_page_id");

    // The upgrade id (in the map of ItemShop) that is used to identify the item in ItemShop. It is used to get the upgrade and the price.
    @NotNull public static NamespacedKey MENU_SHOP_UPGRADE_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.upgrade_id");

    // Type: BOOLEAN; If set to true, the item is a trap clear button for the stored SLOT_INDEX and TRAP.
    @NotNull public static NamespacedKey MENU_TRAP_OVERVIEW_CLEAR_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.clear_button");
    // Type: STRING; Stores the trap id that should be purchased. Items with this tag on are purchase buttons.
    @NotNull public static NamespacedKey MENU_TRAP_PURCHASE_MENU_CONFIRM_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.purchase.confirm");
    // Type: BOOLEAN; If set to true, the item is a trap purchase cancel button. When clicked, the user gets back to the trap overview menu.
    @NotNull public static NamespacedKey MENU_TRAP_PURCHASE_MENU_CANCEL_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.purchase.cancel");
    // Type: BOOLEAN; This button opens the trap purchase menu for the slot specified in SLOT_INDEX and TRAP.
    @NotNull public static NamespacedKey MENU_TRAP_OVERVIEW_PURCHASE_MENU_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.purchase_menu_button");
    // Type: STRING; The slot on which the trap entry is stored in the team (index in trap slot list).
    @NotNull public static NamespacedKey MENU_TRAP_OVERVIEW_SLOT_INDEX = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.slot_index");
    // Type: STRING; The trap in the trap slot (single value).
    @NotNull public static NamespacedKey MENU_TRAP_OVERVIEW_TRAP = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.traps.trap");

    @NotNull private final Game game;
    @NotNull private final ItemStack[] menuBarItems;
    @NotNull private final Map<String, UpgradeEntry> teamUpgradeEntries;
    @NotNull private final Map<String, TrapEntry> teamTrapEntries;
    @NotNull private final Removable removable;

    public TeamGUI(@NotNull Game game, @Nullable Map<String, UpgradeEntry> teamUpgradeEntries, @Nullable Map<String, TrapEntry> teamTrapEntries, @NotNull Removable removable) {
        this.game = game;
        this.menuBarItems = DefaultConfigValues.getShopMenuBar();
        this.game.registerListener(this);
        this.teamUpgradeEntries = teamUpgradeEntries != null ? Map.copyOf(teamUpgradeEntries) : Map.of();
        this.teamTrapEntries = teamTrapEntries != null ? Map.copyOf(teamTrapEntries) : Map.of();
        this.removable = removable;
    }

    public @NotNull Inventory getInventory(@NotNull Player player, @Nullable Integer page, @NotNull BedwarsTeam team, boolean freeMode, boolean fullAccess) {

        if (page == null) {
            page = 0;
        }

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return Bukkit.createInventory(this, 9, Component.text("TeamGUI: Player data not found", NamedTextColor.RED));

        Inventory inventory = Bukkit.createInventory(this, 54, Component.text("Team Menu", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));

        // MENU BAR

        this.generateMenuBarItems(inventory, page, page < 9);
        if (page < 9) this.generateMenuBarSpacers(inventory);

        // PAGE

        switch (page) {
            case 0 -> this.buildTeamUpgradesMenu(inventory, team);
            case 1 -> this.buildTrapsMenu(inventory, team);
            case 10 -> this.buildTrapsPurchaseMenu(inventory, team);
        }

        // RETURN

        this.makeSystemItem(inventory, team.getId(), page, freeMode, fullAccess);
        return inventory;
    }

    public @NotNull Inventory getInventory(@NotNull Player player, @Nullable Integer page) {

        // PLAYER AND TEAM

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return Bukkit.createInventory(this, 9, Component.text("TeamGUI: Player data not found", NamedTextColor.RED));

        BedwarsTeam team = this.getTeam(playerData);
        if (team == null) return Bukkit.createInventory(this, 9, Component.text("TeamGUI: Team not found", NamedTextColor.RED));

        boolean fullAccess = team == this.game.getTeam(playerData.getTeam()); // See the comment of #getTeam() for more information

        // CREATE

        return this.getInventory(player, page, team, false, fullAccess);
    }

    /**
     * Returns the system item.
     * @param inventory inventory
     * @param teamId team id
     * @param page page
     * @param freeMode free mode
     * @param fullAccess full access
     */
    public void makeSystemItem(@NotNull Inventory inventory, int teamId, int page, boolean freeMode, boolean fullAccess) {

        ItemStack item = inventory.getItem(0);
        if (item == null || item.getType() == Material.AIR) item = this.getPlaceholderItem();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalStateException("Item meta is null");

        meta.getPersistentDataContainer().set(MENU_TEAM_ID, PersistentDataType.INTEGER, teamId);
        meta.getPersistentDataContainer().set(MENU_CURRENT_PAGE, PersistentDataType.INTEGER, page);
        meta.getPersistentDataContainer().set(MENU_FREE_MODE, PersistentDataType.BOOLEAN, freeMode);
        meta.getPersistentDataContainer().set(MENU_FULL_ACCESS, PersistentDataType.BOOLEAN, fullAccess);

        item.setItemMeta(meta);

        inventory.setItem(0, item.clone());
    }

    /**
     * Generates the menu bar.
     * @param inventory gui inventory
     */
    private void generateMenuBarItems(@NotNull Inventory inventory, int page, boolean show) {

        if (show) {
            inventory.setItem(0, this.getTeamUpgradesIcon(page == 0));
            inventory.setItem(1, this.getTrapsIcon(page == 1));
            inventory.setItem(2, this.getResourceStorageIcon(page == 2));
        } else {
            for (int slot = 0; slot <= 8; slot++) {
                inventory.setItem(slot, this.getPlaceholderItem());
            }
        }

    }

    /**
     * Generates the glass pane bar below the menu bar.
     * @param inventory gui inventory
     */
    private void generateMenuBarSpacers(@NotNull Inventory inventory) {

        for (int slot = 9; slot <= 17; slot++) {
            inventory.setItem(slot, this.getPlaceholderItem());
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Team Menu", NamedTextColor.RED, TextDecoration.STRIKETHROUGH));
    }

    // ----- TEAM UPGRADES MENU -----

    private void buildTeamUpgradesMenu(@NotNull Inventory inventory, @NotNull BedwarsTeam team) {

        for (int slot = 18; slot < inventory.getSize(); slot++) {

            for (Map.Entry<String, UpgradeEntry> e : this.teamUpgradeEntries.entrySet()) {
                if (!e.getValue().positions().contains(new ShopGUIPosition(0, slot))) continue;
                UpgradeEntry entry = e.getValue();

                int level = team.getUpgrade(entry.upgradeId()) + 1;
                inventory.setItem(slot, this.createUpgradeItem(e.getKey(), e.getValue(), level));

                break;
            }

        }

        // FILL WITH PLACEHOLDERS

        this.fillUnusedSlotsWithPlaceholderItem(inventory);

    }

    // ----- TRAPS MENU -----

    private void buildTrapsMenu(@NotNull Inventory inventory, @NotNull BedwarsTeam team) {

        // Trap Slot Column Icons
        inventory.setItem(19, this.getFirstTrapSlotItem());
        inventory.setItem(21, this.getSecondTrapSlotItem());
        inventory.setItem(23, this.getThirdTrapSlotItem());
        inventory.setItem(25, this.getFourthTrapSlotItem());

        // Trap Slot Icons
        this.buildTrapSlots(inventory, team, 28, 37, 0);
        this.buildTrapSlots(inventory, team, 30, 39, 1);
        this.buildTrapSlots(inventory, team, 32, 41, 2);
        this.buildTrapSlots(inventory, team, 34, 43, 3);

        // FILL WITH PLACEHOLDER ITEMS
        this.fillUnusedSlotsWithPlaceholderItem(inventory);

    }

    private void buildTrapSlots(@NotNull Inventory inventory, @NotNull BedwarsTeam team, int firstSlotId, int secondSlotId, int slotIndex) {

        List<BedwarsTeam.TrapSlot> traps = team.getTraps();

        // Check for closed slots
        if (slotIndex > traps.size()) {
            inventory.setItem(firstSlotId, this.getSlotClosedItem());
            inventory.setItem(secondSlotId, this.getSlotClosedItem());
            return;
        }

        // Traps slots ready for purchase
        if (slotIndex == traps.size()) {
            inventory.setItem(firstSlotId, this.getSlotPurchasableItem());
            inventory.setItem(secondSlotId, this.getSlotPurchasableItem());
            return;
        }

        BedwarsTeam.TrapSlot trap = traps.get(slotIndex);

        this.buildTrapSlot(inventory, trap.first(), firstSlotId, slotIndex, 0);
        this.buildTrapSlot(inventory, trap.second(), secondSlotId, slotIndex, 1);

    }

    private void buildTrapSlot(@NotNull Inventory inventory, @Nullable String trapId, int slot, int trapSlotIndex, int trapOnSlotIndex) {

        if (trapId == null) {
            inventory.setItem(slot, this.getSlotPurchasableItem());
            return;
        }

        TrapEntry entry = this.teamTrapEntries.get(trapId);
        if (entry == null) {
            inventory.setItem(slot, this.getSlotBrokenItem(slot, trapOnSlotIndex));
            return;
        }

        ItemStack icon = entry.icon();

        ItemMeta meta = icon.getItemMeta();
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_SLOT_INDEX, PersistentDataType.INTEGER, trapSlotIndex);
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_TRAP, PersistentDataType.INTEGER, trapOnSlotIndex);
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_CLEAR_BUTTON, PersistentDataType.BOOLEAN, true);

        List<Component> lore = meta.lore();
        lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        if (!lore.isEmpty()) lore.add(Component.text(" "));
        lore.add(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Click to remove this trap.", NamedTextColor.GRAY)));
        lore.add(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("You won't get the money back!", NamedTextColor.GRAY)));
        meta.lore(lore);

        icon.setItemMeta(meta);

        inventory.setItem(slot, icon);
    }

    private void buildTrapsPurchaseMenu(@NotNull Inventory inventory, @NotNull BedwarsTeam team) {

        for (int slot = 18; slot < inventory.getSize(); slot++) {

            for (Map.Entry<String, TrapEntry> e : this.teamTrapEntries.entrySet()) {
                if (!e.getValue().positions().contains(new ShopGUIPosition(0, slot))) continue;
                TrapEntry entry = e.getValue();
                inventory.setItem(slot, this.createTrapItem(e.getKey(), entry));
                break;
            }

        }

        // FILL WITH PLACEHOLDERS

        this.fillUnusedSlotsWithPlaceholderItem(inventory);

    }

    // ----- EVENTS -----

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        // RETRIEVE VALUES

        int currentPage = this.getCurrentPage(event.getInventory());
        if (currentPage < 0) return;

        BedwarsTeam team = this.game.getTeam(this.getCurrentTeam(event.getInventory()));
        if (team == null) return;

        boolean freeMode = this.isFreeMode(event.getInventory());
        boolean fullAccess = this.isFullAccess(event.getInventory());

        // GET ITEM INFO

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        PersistentDataContainer data = clickedItem.getItemMeta().getPersistentDataContainer();

        // MENU BAR CLICK

        if (data.has(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER)) {
            player.openInventory(this.getInventory(player, data.get(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER), team, freeMode, fullAccess));
            return;
        }

        // TEAM UPGRADES CLICK

        if (data.has(MENU_SHOP_UPGRADE_ID, PersistentDataType.STRING)) {
            this.onInventoryClickForUpgradesMenu(data.get(MENU_SHOP_UPGRADE_ID, PersistentDataType.STRING), event, player, team, currentPage, freeMode, fullAccess);
            return;
        }

        // TRAP MENU

        if (data.has(MENU_TRAP_OVERVIEW_CLEAR_BUTTON, PersistentDataType.BOOLEAN)) {
            if (!data.getOrDefault(MENU_TRAP_OVERVIEW_CLEAR_BUTTON, PersistentDataType.BOOLEAN, false)) return;
            this.onInventoryClickForTrapClear(data.getOrDefault(MENU_TRAP_OVERVIEW_SLOT_INDEX, PersistentDataType.INTEGER, -1), data.getOrDefault(MENU_TRAP_OVERVIEW_TRAP, PersistentDataType.INTEGER, -1), event, player, team);
            return;
        }

        if (data.has(MENU_TRAP_OVERVIEW_PURCHASE_MENU_BUTTON, PersistentDataType.BOOLEAN)) {
            if (!data.getOrDefault(MENU_TRAP_OVERVIEW_PURCHASE_MENU_BUTTON, PersistentDataType.BOOLEAN, false)) return;
            this.onInventoryClickForOpeningTrapPurchaseMenu(data.getOrDefault(MENU_TRAP_OVERVIEW_SLOT_INDEX, PersistentDataType.INTEGER, -1), data.getOrDefault(MENU_TRAP_OVERVIEW_TRAP, PersistentDataType.INTEGER, -1), event, player, team);
            Bukkit.broadcastMessage("purchase menu open");
            return;
        }

        if (data.has(MENU_TRAP_PURCHASE_MENU_CANCEL_BUTTON, PersistentDataType.BOOLEAN)) {
            if (!data.getOrDefault(MENU_TRAP_PURCHASE_MENU_CANCEL_BUTTON, PersistentDataType.BOOLEAN, false)) return;
            player.openInventory(this.getInventory(player, 1));
            return;
        }

        if (data.has(MENU_TRAP_PURCHASE_MENU_CONFIRM_BUTTON, PersistentDataType.STRING)) {
            // TODO
            return;
        }

    }

    private void onInventoryClickForUpgradesMenu(@Nullable String upgradeId, @NotNull InventoryClickEvent event, @NotNull Player player, @NotNull BedwarsTeam team, int currentPage, boolean freeMode, boolean fullAccess) {
        if (upgradeId == null) return;

        UpgradeEntry entry = this.teamUpgradeEntries.get(upgradeId);
        if (entry == null) return;

        this.purchaseUpgrade(player, team, entry, freeMode);
        player.openInventory(this.getInventory(player, currentPage, team, freeMode, fullAccess));
        return;
    }

    private void onInventoryClickForTrapClear(int trapSlotIndex, int trapOnTrapSlotIndex, @NotNull InventoryClickEvent event, @NotNull Player player, @NotNull BedwarsTeam team) {
        if (trapSlotIndex < 0 || trapOnTrapSlotIndex < 0) return;

        BedwarsTeam.TrapSlot slot = team.getTrap(trapSlotIndex);
        if (slot == null) return;

        if (slot.values().size() < 2) {
            team.getTraps().remove(trapSlotIndex);
            player.openInventory(this.getInventory(player, 1));
            return;
        }

        switch (trapOnTrapSlotIndex) {
            case 0 -> team.getTraps().set(trapSlotIndex, new BedwarsTeam.TrapSlot(null, slot.second()));
            case 1 -> team.getTraps().set(trapSlotIndex, new BedwarsTeam.TrapSlot(slot.first(), null));
            default -> {
                return;
            }
        }

        player.openInventory(this.getInventory(player, 1));
    }

    private void onInventoryClickForOpeningTrapPurchaseMenu(int trapSlotIndex, int trapOnTrapSlotIndex, @NotNull InventoryClickEvent event, @NotNull Player player, @NotNull BedwarsTeam team) {
        if (trapSlotIndex < 0 ||  trapOnTrapSlotIndex < 0) return;

        Inventory inventory = this.getInventory(player, 10);

        // Update system item to store the trap slot index and slot
        ItemStack systemItem = inventory.getItem(0);
        if (systemItem == null) return;
        ItemMeta systemMeta = systemItem.getItemMeta();
        systemMeta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_SLOT_INDEX, PersistentDataType.INTEGER, trapSlotIndex);
        systemMeta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_TRAP, PersistentDataType.INTEGER, trapOnTrapSlotIndex);
        systemMeta.getPersistentDataContainer().set(MENU_TRAP_PURCHASE_MENU_CANCEL_BUTTON, PersistentDataType.BOOLEAN, true);
        systemItem.setItemMeta(systemMeta);

        // Open inventory
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- ITEMS -----

    private void fillUnusedSlotsWithPlaceholderItem(@NotNull Inventory inventory) {

        for (int slot = 9; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) continue;
            inventory.setItem(slot, this.getPlaceholderItem());
        }

    }

    public @NotNull ItemStack getPlaceholderItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.customName(Component.empty());
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getTeamUpgradesIcon(boolean selected) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text((selected ? "•" : "") + "Team Upgrades", NamedTextColor.GOLD)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Enhancements for your team", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER, 0);

        if (selected) meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getTrapsIcon(boolean selected) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text((selected ? "•" : "") + "Traps", NamedTextColor.GOLD)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Defensive measures against enemies", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk5ODNmYTFkOGMzYTA4N2MxMTVhM2JmNDJhY2UyODBiZjhhOTQ5NWEzNzBiNjkzY2UyOTkyY2EyYTdlNmIwMyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER, 1);

        if (selected) meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getResourceStorageIcon(boolean selected) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text((selected ? "•" : "") + "Resource Storage", NamedTextColor.GOLD)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Store and retrieve resources", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNiZGJhZWRkN2Q2NDQ0ZTc5YWE4MjIyZjg5ODEyNDAyMDRjYzNjYzFjOTY1NTExODY4NzYxOGRiOGNlYyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER, 2);

        if (selected) meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getFirstTrapSlotItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot 1", NamedTextColor.YELLOW)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("These Traps will be triggered first", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZlMmNjMWM0ZjhjZGJmMTAwZTE4NDUzNmExMWM1NDViZmNjMWNjZDQyZDgwZGIyZDlkZjE5ODc4MjgxNWIwYyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getSecondTrapSlotItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot 2", NamedTextColor.YELLOW)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("These Traps will be triggered second", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE3NzRhMWQ2YTQzYjM3ZGU1NWE3ZjZlY2Y4NzBkYzVjNDhjZTlkZDNkNmMwYzVkNzkyZTk4NGQ1OWM0Y2RhNyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getThirdTrapSlotItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot 3", NamedTextColor.YELLOW)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("These Traps will be triggered third", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzU5YmI5NTE3MzQ5MzQ1MzkwMDcyYmUzNWZhNTRjNWMzY2I2YmE0MmQ4OWM1ODYzMTdkNTA0OWVkMWY4MjZlYyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getFourthTrapSlotItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot 4", NamedTextColor.YELLOW)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("These Traps will be triggered fourth", NamedTextColor.GRAY))));
        ItemUtils.setCustomHeadForSkullMeta(meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVlNTM5MDQ0YjdjMGRhZjIwMWY3ZmM1YjI4NDVmNDdlNWU5ZmMwNjU3Y2JkNDJhZDJiOWM3MDUyYmE0NjY3YyJ9fX0=");
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getSlotClosedItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot closed")));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("You need to purchase a trap on the previous slots first."))));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getSlotPurchasableItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot empty")));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Click to purchase a trap.", NamedTextColor.GRAY))));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_PURCHASE_MENU_BUTTON, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getSlotBrokenItem(int slotIndex, int trapOnSlotIndex) {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Slot broken")));
        meta.lore(List.of(
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Unknown trap.", NamedTextColor.GRAY)),
                ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Click to delete it", NamedTextColor.GRAY))
        ));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_SLOT_INDEX, PersistentDataType.INTEGER, slotIndex);
        meta.getPersistentDataContainer().set(MENU_TRAP_OVERVIEW_TRAP, PersistentDataType.INTEGER, trapOnSlotIndex);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getTrapPurchaseCancelButton() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Cancel purchase", NamedTextColor.RED, TextDecoration.BOLD)));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(MENU_TRAP_PURCHASE_MENU_CANCEL_BUTTON, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    // ----- ITEMS -----

    private @NotNull ItemStack createUpgradeItem(@NotNull String key, @NotNull UpgradeEntry entry, int level) {
        @Nullable UpgradeEntry.PriceEntry price = entry.prices().get(level);

        ItemStack icon = entry.icons().get(level);
        if (icon == null) return new ItemStack(Material.AIR);

        ItemMeta iconMeta = icon.getItemMeta();

        iconMeta.getPersistentDataContainer().set(MENU_SHOP_UPGRADE_ID, PersistentDataType.STRING, key);

        List<Component> lore = iconMeta.lore();
        lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        if (!lore.isEmpty()) lore.add(Component.text(" "));

        if (price != null) {
            lore.add(Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .append(Component.text("Price: ", NamedTextColor.GRAY))
                    .append(Component.text(price.amount(), NamedTextColor.YELLOW))
                    .appendSpace()
                    .append(Component.text(price.currency().name(), NamedTextColor.YELLOW))
            );
        } else {
            lore.add(Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .append(Component.text("No upgrade available", NamedTextColor.GRAY))
            );
        }
        iconMeta.lore(lore);

        icon.setItemMeta(iconMeta);
        return icon;
    }

    private @NotNull ItemStack createTrapItem(@NotNull String key, @NotNull TrapEntry entry) {
        @Nullable UpgradeEntry.PriceEntry price = entry.priceEntry();

        ItemStack icon = entry.icon();
        ItemMeta iconMeta = icon.getItemMeta();

        iconMeta.getPersistentDataContainer().set(MENU_TRAP_PURCHASE_MENU_CONFIRM_BUTTON, PersistentDataType.STRING, key);

        List<Component> lore = iconMeta.lore();
        lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        if (!lore.isEmpty()) lore.add(Component.text(" "));
        lore.add(Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .append(Component.text("Price: ", NamedTextColor.GRAY))
                .append(Component.text(price.amount(), NamedTextColor.YELLOW))
                .appendSpace()
                .append(Component.text(price.currency().name(), NamedTextColor.YELLOW))
        );
        iconMeta.lore(lore);

        icon.setItemMeta(iconMeta);
        return icon;
    }

    // ----- PURCHASE PROCESS -----

    private void purchaseUpgrade(@NotNull Player player, @NotNull BedwarsTeam team, @NotNull UpgradeEntry entry, boolean freeMode) {

        // Get next upgrade level
        int level = team.getUpgrade(entry.upgradeId()) + 1;

        // Get and check the price (done before the free mode check to prevent purchasing higher upgrade values than allowed
        UpgradeEntry.PriceEntry price = entry.prices().get(level);
        if (price == null) return;
        if (price.amount() < 0) return;

        // Purchase
        if (!freeMode) {

            // Get the amount of the currency the player currently has
            int availableCurrency = this.getAvailableCurrency(player.getInventory(), price.currency());

            // Check for enough money
            if (availableCurrency < price.amount()) {
                player.sendRichMessage("<red>You don't have enough money to purchase the upgrade!");
                player.playSound(player.getLocation().clone(), Sound.ENTITY_PLAYER_TELEPORT, 1, 2);
                return;
            }

            // Remove amount of money
            Bedwars.removeSpecificAmountOfItems(player.getInventory(), price.currency(), price.amount());

        }

        // Give upgrade
        team.setUpgrade(entry.upgradeId(), level);
        player.playSound(player.getLocation().clone(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
    }

    private int getAvailableCurrency(@NotNull Inventory inventory, @NotNull Material currency) {
        int availableCurrency = 0;

        for (ItemStack item : Arrays.copyOf(inventory.getContents(), inventory.getContents().length)) {

            if (item != null && item.getType() == currency) {
                availableCurrency += item.getAmount();
            }

        }

        return availableCurrency;
    }

    // ----- UTILITIES -----

    /**
     * Returns the current page of the specified Team GUI Inventory page.
     * @param inventory team gui inventory
     * @return current page (negative if invalid)
     */
    private int getCurrentPage(@NotNull Inventory inventory) {

        ItemStack item = inventory.getItem(0);
        if (item == null) return -1;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;

        return meta.getPersistentDataContainer().getOrDefault(MENU_CURRENT_PAGE, PersistentDataType.INTEGER, -1);
    }

    /**
     * Returns the current Team ID the GUI is opened for.
     * @param inventory team gui inventory
     * @return current page (negative if invalid)
     */
    private int getCurrentTeam(@NotNull Inventory inventory) {

        ItemStack item = inventory.getItem(0);
        if (item == null) return -1;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;

        return meta.getPersistentDataContainer().getOrDefault(MENU_TEAM_ID, PersistentDataType.INTEGER, -1);
    }

    /**
     * Returns the status of free mode of the current inventory.
     * @param inventory shop gui inventory
     * @return free mode status (false if invalid)
     */
    private boolean isFreeMode(@NotNull Inventory inventory) {

        ItemStack item = inventory.getItem(0);
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().getOrDefault(MENU_FREE_MODE, PersistentDataType.BOOLEAN, false);
    }

    /**
     * Returns the status of full access of the current inventory.
     * @param inventory team gui inventory
     * @return free mode status (false if invalid)
     */
    public boolean isFullAccess(@NotNull Inventory inventory) {

        ItemStack item = inventory.getItem(0);
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().getOrDefault(MENU_FULL_ACCESS, PersistentDataType.BOOLEAN, false);
    }

    /**
     * Returns the team for the gui.<br/>
     * Currently, this just simply returns the player's team,
     * but this method exists because in the future there might be a team lock option,
     * which would mean that if set, a team gui villager would be locked to a team,
     * and if any player purchases something there, it will be added to the shop gui
     * villagers team instead of the purchasing player's team.
     * @param playerData player data
     * @return team
     */
    private @Nullable BedwarsTeam getTeam(@NotNull PlayerData playerData) {
        return this.game.getTeam(playerData.getTeam());
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return this.removable.toBeRemoved();
    }

    // ----- INNER CLASSES -----

    public record TrapEntry(@NotNull String trapId, @NotNull UpgradeEntry.PriceEntry priceEntry, List<ShopGUIPosition> positions, @NotNull ItemStack icon) {

        public TrapEntry(@NotNull String trapId, @NotNull UpgradeEntry.PriceEntry priceEntry, List<ShopGUIPosition> positions, @NotNull ItemStack icon) {
            this.trapId = trapId;
            this.priceEntry = priceEntry;
            this.positions = List.copyOf(positions);
            this.icon = icon.clone();
        }

        @Override
        public @NotNull ItemStack icon() {
            return icon.clone();
        }
    }

}
