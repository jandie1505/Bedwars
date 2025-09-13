package net.jandie1505.bedwars.game.game.shop.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.shop.shop.ShopEntry;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.shop.ItemShop;
import net.jandie1505.bedwars.game.game.shop.shop.ShopGUIPosition;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ShopGUI implements ManagedListener, InventoryHolder {
    // The current page, which is stored in the quick buy item
    @NotNull public static NamespacedKey MENU_CURRENT_PAGE = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.current_page");
    // The menu bar page id, which is stored in all menu bar items
    @NotNull public static NamespacedKey MENU_BAR_PAGE_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.menu_bar_page_id");
    // The shop item id that is used to identify the item in ItemShop. Is used to get the original item and price.
    @NotNull public static NamespacedKey MENU_SHOP_ITEM_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.shop.shop_item_id");

    @NotNull private final Game game;
    @NotNull private final ItemStack[] menuBarItems;

    public ShopGUI(@NotNull Game game) {
        this.game = game;
        this.menuBarItems = DefaultConfigValues.getShopMenuBar();
        this.game.registerListener(this);
    }

    public @NotNull Inventory getInventory(@NotNull Player player, @Nullable Integer page) {

        if (page == null) {
            page = 0;
        }

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return Bukkit.createInventory(this, 9, Component.text("ShopGUI: Player data not found", NamedTextColor.RED));

        Inventory inventory = Bukkit.createInventory(this, 54, Component.text("Item Shop", NamedTextColor.GOLD, TextDecoration.BOLD));

        // MENU BAR

        inventory.setItem(0, this.getQuickBuyItem(page == 0));
        this.generateMenuBarItems(inventory, page);
        this.generateMenuBarSpacer(inventory);

        // PAGE

        if (page > 0) {
            buildShopMenuPage(inventory, page);
        } else {
            buildQuickBuyMenu(inventory);
        }

        // RETURN

        return inventory;
    }

    /**
     * Returns the quick buy item.
     * @return quick buy item
     */
    private @NotNull ItemStack getQuickBuyItem(boolean selected) {
        ItemStack quickBuyItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta quickBuyMeta = quickBuyItem.getItemMeta();

        quickBuyMeta.displayName(Component.text("Quick Buy", NamedTextColor.GOLD,  TextDecoration.BOLD));
        quickBuyMeta.addItemFlags(ItemFlag.values());
        quickBuyMeta.getPersistentDataContainer().set(MENU_CURRENT_PAGE, PersistentDataType.INTEGER, 0);
        quickBuyMeta.getPersistentDataContainer().set(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER, 0);

        if (selected) {
            quickBuyMeta.lore(List.of(Component.text("selected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
            quickBuyMeta.addEnchant(Enchantment.EFFICIENCY, 1, true); // Adds glint effect
        }

        quickBuyItem.setItemMeta(quickBuyMeta);
        return quickBuyItem;
    }

    /**
     * Generates the menu bar.
     * @param inventory gui inventory
     */
    private void generateMenuBarItems(@NotNull Inventory inventory, int page) {

        int slot = 1;
        for (int i = 0; i < this.menuBarItems.length; i++) {
            if (slot > 8 || slot >= inventory.getSize()) break;

            ItemStack item = this.menuBarItems[i].clone();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                slot++;
                continue;
            }

            if (page == i+1) {
                meta.lore(List.of(Component.text("selected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
                meta.addEnchant(Enchantment.EFFICIENCY, 1, true); // Add glint effect
            }

            meta.addItemFlags(ItemFlag.values());
            meta.getPersistentDataContainer().set(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER, i + 1);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

    }

    /**
     * Generates the glass pane bar below the menu bar.
     * @param inventory gui inventory
     */
    private void generateMenuBarSpacer(@NotNull Inventory inventory) {

        for (int slot = 9; slot <= 17; slot++) {
            inventory.setItem(slot, this.getPlaceholderItem());
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Shop GUI", NamedTextColor.RED, TextDecoration.STRIKETHROUGH));
    }

    // ----- QUICK BUY MENU -----

    private void buildQuickBuyMenu(@NotNull Inventory inventory) {



    }

    // ----- SHOP PAGES -----

    private void buildShopMenuPage(@NotNull Inventory inventory, int page) {
        ItemShop itemShop = new ItemShop(this.game); // Will be added later

        for (int slot = 18; slot < inventory.getSize(); slot++) {

            // PLACEHOLDERS

            if (slot % 9 == 0 || (slot + 1) % 9 == 0) {
                inventory.setItem(slot, this.getPlaceholderItem());
                continue;
            }

            // ITEMS

            boolean itemLoopInterrupted = false;
            for (Map.Entry<String, ShopEntry> e : itemShop.getItems().entrySet()) {
                if (!e.getValue().positions().contains(new ShopGUIPosition(page, slot))) continue;

                ItemStack item = e.getValue().item().clone();
                ItemMeta meta = item.getItemMeta();

                meta.getPersistentDataContainer().set(MENU_SHOP_ITEM_ID, PersistentDataType.STRING, e.getKey());

                List<Component> lore = meta.lore();
                lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
                if (!lore.isEmpty()) lore.add(Component.text(" "));
                lore.add(Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .append(Component.text("Price: ", NamedTextColor.GRAY))
                        .append(Component.text(e.getValue().price(), NamedTextColor.YELLOW))
                        .appendSpace()
                        .append(Component.text(e.getValue().currency().name(), NamedTextColor.YELLOW))
                );
                meta.lore(lore);

                item.setItemMeta(meta);
                inventory.setItem(slot, item);

                itemLoopInterrupted = true;
                break;
            }

            if (itemLoopInterrupted) continue;

            // UPGRADES



        }

    }

    // ----- EVENTS -----

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        PersistentDataContainer data = clickedItem.getItemMeta().getPersistentDataContainer();

        if (data.has(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER)) {
            player.openInventory(this.getInventory(player, data.get(MENU_BAR_PAGE_ID, PersistentDataType.INTEGER)));
            return;
        }

        if (data.has(MENU_SHOP_ITEM_ID, PersistentDataType.STRING)) {
            String itemId = data.get(MENU_SHOP_ITEM_ID, PersistentDataType.STRING);
            if (itemId == null) return;

            ShopEntry entry = this.game.getItemShop().getItem(itemId);
            if (entry == null) return;

           this.purchaseItem(player, entry, event.isShiftClick());
            return;
        }

    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- OTHER -----

    private @NotNull ItemStack getPlaceholderItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.customName(Component.empty());
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

    // ----- PURCHASE PROCESS -----

    private void purchaseItem(@NotNull Player player, @NotNull ShopEntry entry, boolean stackPurchase) {
        int availableCurrency = this.getAvailableCurrency(player.getInventory(), entry.currency());
        int price;
        int itemAmount;

        if (stackPurchase) {
            int[] stackPurchaseResult = this.calculateStackPurchase(entry, availableCurrency);
            itemAmount = stackPurchaseResult[0];
            price = stackPurchaseResult[1];
        } else {
            itemAmount = entry.item().getAmount();
            price = entry.price();
        }

        // Check for enough currency
        if (this.getAvailableCurrency(player.getInventory(), entry.currency()) < price) {
            player.sendRichMessage("<red>You don't have enough money to purchase the item!");
            player.playSound(player.getLocation().clone(), Sound.ENTITY_PLAYER_TELEPORT, 1, 2);
            return;
        }

        // Prepare purchased item
        ItemStack purchasedItemStack = entry.item().clone();
        purchasedItemStack.setAmount(itemAmount);

        // Enough space check
        if (!this.hasEnoughSpace(purchasedItemStack, player.getInventory())) {
            player.sendRichMessage("<red>You don't have enough space to purchase this item!");
            player.playSound(player.getLocation().clone(), Sound.ENTITY_PLAYER_TELEPORT, 1, 2);
            return;
        }

        // Purchase success
        Bedwars.removeSpecificAmountOfItems(player.getInventory(), entry.currency(), price);
        player.getInventory().addItem(purchasedItemStack);
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

    /**
     * Calculates the amount and price of a stack purchase.
     * @param entry shop entry
     * @param availableMoney money the purchasing player has available
     * @return array: [item amount, price]
     */
    private int[] calculateStackPurchase(@NotNull ShopEntry entry, int availableMoney) {
        int amount = 0;
        int price = 0;

        while (amount < entry.item().getMaxStackSize() || price <= availableMoney) {
            int nextAmount = amount + entry.item().getAmount();
            int nextPrice = price + entry.price();

            if (nextAmount > entry.item().getMaxStackSize()) break; // Item has reached max stack size, can't add more.
            if (nextPrice > availableMoney) break; // Player can't afford next amount.

            amount = nextAmount;
            price = nextPrice;
        }

        return  new int[]{amount, price};
    }

    /**
     * Checks if there is enough space for an item.
     * @param purchasedItem purchased item stack
     * @param inventory inventory of the player
     * @return true = enough space
     */
    private boolean hasEnoughSpace(@NotNull ItemStack purchasedItem, @NotNull Inventory inventory) {

        for (int slot = 0;  slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);

            // If there is a free slot in the inventory, there is always space for an item.
            if (item == null || item.getType() == Material.AIR) return true;

            // If the amount of the currently available stack plus the amount of the purchased item
            // is less or equal than the max stack size of the item, there is enough space.
            if (!item.isSimilar(purchasedItem)) continue;
            if (item.getAmount() + purchasedItem.getAmount() <= purchasedItem.getMaxStackSize()) return true;
        }

        return false;
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
