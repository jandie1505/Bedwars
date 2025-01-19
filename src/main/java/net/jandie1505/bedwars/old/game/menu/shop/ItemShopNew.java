package net.jandie1505.bedwars.old.game.menu.shop;

import net.jandie1505.bedwars.old.Bedwars;
import net.jandie1505.bedwars.old.GamePart;
import net.jandie1505.bedwars.old.ManagedListener;
import net.jandie1505.bedwars.old.game.Game;
import net.jandie1505.bedwars.old.game.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;

public class ItemShopNew implements ManagedListener, InventoryHolder {
    private static final String DATA_INVENTORY_PAGE_CURRENT = "current_page";

    private final Game game;
    private final Map<String, ShopEntry> shopEntries;
    private final Map<String, UpgradeEntry> upgradeEntries;
    private final ItemStack[] menuBar;

    public ItemShopNew(Game game) {
        this.game = game;
        this.shopEntries = new HashMap<>();
        this.upgradeEntries = new HashMap<>();
        this.menuBar = new ItemStack[8];

        this.menuBar[0] = new ItemStack(Material.DIAMOND);

        this.game.getPlugin().registerListener(this);
    }

    // GETTER

    public Map<String, ShopEntry> getShopEntries() {
        return Map.copyOf(this.shopEntries);
    }

    public ShopEntry getShopEntry(String shopName) {
        return this.shopEntries.get(shopName);
    }

    public Map<String, UpgradeEntry> getUpgradeEntries() {
        return Map.copyOf(this.upgradeEntries);
    }

    public UpgradeEntry getUpgradeEntry(String upgradeName) {
        return this.upgradeEntries.get(upgradeName);
    }

    // GUI

    /**
     * Creates a new gui inventory and returns it.
     * @return gui inventory
     */
    public Inventory createGUIInventory(Player player) {
        return this.createInventoryPage(0, player);
    }

    /**
     * Open the shop inventory for the specified player.
     * @param player player
     */
    public void openInventory(@NotNull Player player) {
        player.openInventory(this.createGUIInventory(player));
    }

    // ----- PRIVATE GUI -----

    // Inventory Base

    private ItemStack getQuickBuyItem(int page) {
        ItemStack quickBuyItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(quickBuyItem.getType());
        meta.setDisplayName("§b§lQuick Buy");
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(new NamespacedKey(this.game.getPlugin(), DATA_INVENTORY_PAGE_CURRENT), PersistentDataType.INTEGER, page);
        quickBuyItem.setItemMeta(meta);
        return quickBuyItem;
    }

    /**
     * Creates the inventory base.
     * The page is required to set it to the utility item on slot 0.
     * @param page page
     * @return inventory
     */
    @NotNull
    private Inventory createInventoryBase(int page) {
        Inventory inventory = this.game.getPlugin().getServer().createInventory(this, 54, "Item Shop");

        inventory.setItem(0, this.getQuickBuyItem(page));

        for (int i = 1; i < 9; i++) {

            ItemStack item = this.menuBar[i-1];
            if (item == null) {
                item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§cUNSET");
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
            }

            inventory.setItem(i, item.clone());

        }

        return inventory;
    }

    /**
     * Returns the current shop page the given inventory is on.
     * Returns {@value -1} if the given inventory is not a shop page.
     * @param inventory inventory
     * @return current inventory page
     */
    private int getCurrentShopPage(@NotNull Inventory inventory) {
        ItemStack quickBuyItem = inventory.getItem(0);
        if (quickBuyItem == null) return -1;
        ItemMeta meta = quickBuyItem.getItemMeta();
        if (meta == null) return -1;
        return meta.getPersistentDataContainer().getOrDefault(new NamespacedKey(this.game.getPlugin(), DATA_INVENTORY_PAGE_CURRENT), PersistentDataType.INTEGER, -1);
    }

    // Inventory page

    /**
     * Creates the specified inventory page.
     * @param page inventory page (1 < page < 9)
     * @return
     */
    @NotNull
    private Inventory createInventoryPage(int page, @Nullable Player player) {
        Inventory inventory = this.createInventoryBase(page);
        if (page < 1 || page >= 9) return inventory;

        // Add items

        this.addItems(inventory, page);
        if (player != null) this.addUpgrades(inventory, page, player);
        this.addPlaceholders(inventory);

        // Return

        return inventory;
    }

    /**
     * Adds the shop items to the item shop.
     * @param inventory inventory
     * @param page shop page
     */
    private void addItems(@NotNull Inventory inventory, int page) {

        for (Map.Entry<String, ShopEntry> entry : Map.copyOf(this.shopEntries).entrySet()) {
            for (ShopGUIPosition position : entry.getValue().positions()) {
                if (position.slot() <= 9 || position.slot() >= 54) continue;
                if (position.page() != page) continue;

                ItemStack item = entry.getValue().item();
                ItemMeta meta = item.getItemMeta() != null ? item.getItemMeta() : this.game.getPlugin().getServer().getItemFactory().getItemMeta(item.getType());
                meta.getPersistentDataContainer().set(new NamespacedKey(this.game.getPlugin(), "shop_entry_id"), PersistentDataType.STRING, entry.getKey());
                item.setItemMeta(meta);

                inventory.setItem(position.slot(), item);

            }
        }

    }

    /**
     * Adds the upgrades to the item shop.
     * @param inventory inventory
     * @param page page
     * @param player player
     */
    private void addUpgrades(@NotNull Inventory inventory, int page, @NotNull Player player) {

        PlayerData playerData = this.game.getPlayer(player.getUniqueId());
        if (playerData != null) {
            for (Map.Entry<String, UpgradeEntry> entry : Map.copyOf(this.upgradeEntries).entrySet()) {

                // Get current upgrade step of the entry

                List<UpgradeEntry.UpgradeStep> upgradeSteps = entry.getValue().upgradeSteps();
                if (upgradeSteps.isEmpty()) continue;

                int upgradeLevel = playerData.getUpgrade(entry.getKey());
                if (upgradeLevel < 0) continue;

                // Create and modify item

                ItemStack item;

                if (upgradeLevel < upgradeSteps.size()) {

                    // UPGRADE STEP FOUND

                    UpgradeEntry.UpgradeStep upgradeStep = upgradeSteps.get(upgradeLevel);

                    item = upgradeStep.displayItem();
                    ItemMeta meta = item.getItemMeta();

                    meta.getPersistentDataContainer().set(new NamespacedKey(this.game.getPlugin(), "shop_upgrade_id"), PersistentDataType.STRING, entry.getKey());

                    List<String> lore = new ArrayList<>();
                    lore.add("§r§7Price: §a" + upgradeStep.price() + " " + upgradeStep.currency().name() + (upgradeStep.price() != 1 ? "s" : ""));
                    lore.addAll(Objects.requireNonNullElse(meta.getLore(), List.of()));
                    meta.setLore(lore);

                    item.setItemMeta(meta);

                } else {

                    // UPGRADE STEP NOT FOUND

                    item = entry.getValue().maxLevelItem();

                }

                // Set item

                for (ShopGUIPosition position : entry.getValue().guiPositions()) {
                    if (position.slot() <= 9 || position.slot() >= 54) continue;
                    if (position.page() != page) continue;
                    inventory.setItem(position.slot(), item.clone());
                }

            }
        }

    }

    /**
     * Adds the placeholder glass panes to the item shop.
     * @param inventory inventory
     */
    private void addPlaceholders(@NotNull Inventory inventory) {

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                ItemStack placeholderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = placeholderItem.getItemMeta();
                meta.setDisplayName(" ");
                meta.addItemFlags(ItemFlag.values());
                placeholderItem.setItemMeta(meta);
                inventory.setItem(i, placeholderItem);
            }

        }

    }

    // ----- EVENTS -----

    /**
     * Checks if there is enough space in the player's inventory to add the specified item.
     * @param inventory player inventory
     * @param itemToCheckFor the item that should be checked for
     * @return if there is enough space
     */
    private boolean checkSpace(@NotNull Inventory inventory, @NotNull ItemStack itemToCheckFor) {

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) return true;
            if (item.getType() != itemToCheckFor.getType()) continue;

            if ((item.getItemMeta() == null && itemToCheckFor.getItemMeta() == null) || item.getItemMeta().equals(itemToCheckFor.getItemMeta())) {
                return item.getMaxStackSize() - itemToCheckFor.getMaxStackSize() > 0;
            }

        }

        return false;
    }

    /**
     * Purchases an item and returns if it was successful.
     * @param inventory inventory of the player
     * @param price price of the item
     * @param currency currency of the price
     * @return success
     */
    private boolean purchaseItem(@NotNull Inventory inventory, int price, @NotNull Material currency) {

        int availableCurrency = 0;
        for (ItemStack item : Arrays.copyOf(inventory.getContents(), inventory.getContents().length)) {

            if (item != null && item.getType() == currency) {
                availableCurrency += item.getAmount();
            }

        }

        if (availableCurrency < price) return false;

        Bedwars.removeSpecificAmountOfItems(inventory, currency, price);
        return true;
    }

    /**
     * Handles purchasing items in the item shop.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getInventory().getHolder() != this) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        event.setCancelled(true);

        // This will prevent the players from interacting with the shop inventory through clicking in their own inventories
        if (event.getClickedInventory() == null || event.getClickedInventory().getHolder() != this) return;

        PlayerData playerData = this.game.getPlayer(player.getUniqueId());
        if (playerData == null) {
            player.closeInventory();
            player.sendMessage("§cYou are not in game");
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (event.getSlot() < 9) {

            // ITEM IS IN MENU BAR

            player.openInventory(this.createInventoryPage(event.getSlot(), player));

        } else {

            // ITEM IS NOT IN MENU BAR

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(clickedItem.getType());
            String shopId = meta.getPersistentDataContainer().get(new NamespacedKey(this.game.getPlugin(), "shop_entry_id"), PersistentDataType.STRING);
            String upgradeId = meta.getPersistentDataContainer().get(new NamespacedKey(this.game.getPlugin(), "shop_upgrade_id"), PersistentDataType.STRING);

            if (shopId != null) {

                // ITEM IS A SHOP ITEM

                ShopEntry shopEntry = this.shopEntries.get(shopId);
                if (shopEntry == null) return;

                if (!this.checkSpace(player.getInventory(), shopEntry.item())) {
                    player.sendMessage("§cYou don't have enough space in your inventory to purchase this item!");
                    return;
                }

                boolean success = this.purchaseItem(player.getInventory(), shopEntry.price(), shopEntry.currency());

                if (success) {
                    player.getInventory().addItem(shopEntry.item());
                    player.sendMessage("§aItem successfully purchased!");
                } else {
                    player.sendMessage("§cYou don't have enough " + shopEntry.currency().name() + "S to purchase this item!");
                }

            } else if (upgradeId != null) {

                // ITEM IS AN UPGRADE ITEM

                UpgradeEntry entry = this.upgradeEntries.get(upgradeId);
                if (entry == null) return;

                int level = playerData.getUpgrade(upgradeId);
                if (level < 0) return;
                if (level >= entry.upgradeSteps().size()) return;

                UpgradeEntry.UpgradeStep step = entry.upgradeSteps().get(level);
                if (step == null) return;

                boolean success = this.purchaseItem(player.getInventory(), step.price(), step.currency());

                if (success) {
                    playerData.setUpgrade(upgradeId, level + 1);
                    int page = this.getCurrentShopPage(event.getInventory());
                    player.openInventory(this.createInventoryPage(page, player));
                    player.sendMessage("§aUpgrade successfully purchased!");
                } else {
                    player.sendMessage("§cYou don't have enough " + step.currency().name() + "S to purchase this upgrade!");
                }

            }

            return;
        }

    }

    /**
     * Cancels any inventory drag in this inventory.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (event.getInventory().getHolder() != this) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        event.setCancelled(true);
    }

    // OTHER

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.game.getPlugin().getServer().createInventory(this, 9, "§4§mError");
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    @Override
    public GamePart getGame() {
        return this.game;
    }

    // INNER CLASSES

    // STATIC

    public static JSONObject serializeShopEntries(Map<String, ShopEntry> shopEntries) {
        JSONObject data = new JSONObject();

        for (Map.Entry<String, ShopEntry> shopEntry : shopEntries.entrySet()) {
            data.put(shopEntry.getKey(), ShopEntry.convertToJSON(shopEntry.getValue()));
        }

        return data;
    }

    public static Map<String, ShopEntry> deserializeShopEntries(JSONObject data) {
        Map<String, ShopEntry> shopEntries = new HashMap<>();

        for (String key : data.keySet()) {

            try {
                ShopEntry shopEntry = ShopEntry.createFromJSON(data.getJSONObject(key));
                //if (shopEntry == null) continue;
                shopEntries.put(key, shopEntry);
            } catch (Exception e) {
                continue;
            }

        }

        return shopEntries;
    }

}
