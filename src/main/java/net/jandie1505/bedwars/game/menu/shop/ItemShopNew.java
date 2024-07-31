package net.jandie1505.bedwars.game.menu.shop;

import net.chaossquad.mclib.JSONConfigUtils;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.ManagedListener;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ItemShopNew implements ManagedListener, InventoryHolder {
    private final Game game;
    private final Map<String, ShopEntry> shopEntries;
    private final ItemStack[] menuBar;

    public ItemShopNew(Game game) {
        this.game = game;
        this.shopEntries = new HashMap<>();
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

    // GUI

    /**
     * Creates a new gui inventory and returns it.
     * @return gui inventory
     */
    public Inventory createGUIInventory() {
        return this.createInventoryPage(0);
    }

    /**
     * Open the shop inventory for the specified player.
     * @param player player
     */
    public void openInventory(@NotNull Player player) {
        player.openInventory(this.createGUIInventory());
    }

    // PRIVATE GUI

    private ItemStack getQuickBuyItem(int page) {
        ItemStack quickBuyItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(quickBuyItem.getType());
        meta.setDisplayName("§b§lQuick Buy");
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(new NamespacedKey(this.game.getPlugin(), "current_page"), PersistentDataType.INTEGER, 0);
        quickBuyItem.setItemMeta(meta);
        return quickBuyItem;
    }

    /**
     * Creates the inventory base.
     * The page is required to set it to the utility item on slot 0.
     * @param page page
     * @return inventory
     */
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
     * Creates the specified inventory page.
     * @param page inventory page (1 < page < 9)
     * @return
     */
    private Inventory createInventoryPage(int page) {
        Inventory inventory = this.createInventoryBase(page);
        if (page < 1 || page >= 9) return inventory;

        for (Map.Entry<String, ShopEntry> entry : Map.copyOf(this.shopEntries).entrySet()) {
            for (ShopEntry.GUIPosition position : entry.getValue().positions()) {
                if (position.slot() <= 9 || position.slot() >= 54) continue;
                if (position.page() != page) continue;

                ItemStack item = entry.getValue().item();
                ItemMeta meta = item.getItemMeta() != null ? item.getItemMeta() : this.game.getPlugin().getServer().getItemFactory().getItemMeta(item.getType());
                meta.getPersistentDataContainer().set(new NamespacedKey(this.game.getPlugin(), "shop_id"), PersistentDataType.STRING, entry.getKey());
                item.setItemMeta(meta);

                inventory.setItem(position.slot(), item);

            }
        }

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

        return inventory;
    }

    // EVENTS

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

        PlayerData playerData = this.game.getPlayer(player.getUniqueId());
        if (playerData == null) {
            player.closeInventory();
            player.sendMessage("§cYou are not in game");
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (event.getSlot() < 9) {

            player.openInventory(this.createInventoryPage(event.getSlot()));

        } else {

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(clickedItem.getType());
            String id = meta.getPersistentDataContainer().get(new NamespacedKey(this.game.getPlugin(), "shop_id"), PersistentDataType.STRING);

            ShopEntry shopEntry = this.shopEntries.get(id);
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

    public record ShopEntry(
            @NotNull ItemStack item,
            @NotNull Material currency,
            int price,
            @NotNull List<GUIPosition> positions
    ) {

        public ShopEntry(@NotNull ItemStack item, @NotNull Material currency, int price, @NotNull List<GUIPosition> positions) {
            this.item = item.clone();
            this.currency = currency;
            this.price = price;
            this.positions = List.copyOf(positions);
        }

        @Override
        public ItemStack item() {
            return this.item.clone();
        }

        public static ShopEntry createFromJSON(JSONObject data) {

            JSONObject itemData = data.optJSONObject("item");
            //if (itemData == null) return null;
            ItemStack item;
            try {
                item = JSONConfigUtils.deserializeItem(itemData);
                //if (item == null) return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            Material currency = Material.getMaterial(data.getString("currency"));
            //if (currency == null) return null;

            List<GUIPosition> positions = new ArrayList<>();
            JSONArray guiPositions = data.optJSONArray("positions");
            //if (guiPositions == null) return null;
            for (int i = 0; i < guiPositions.length(); i++) {
                JSONObject guiPosition = guiPositions.getJSONObject(i);
                //if (guiPosition == null) continue;
                GUIPosition position = GUIPosition.createFromJSON(guiPosition);
                //if (position == null) continue;
                positions.add(position);
            }

            return new ShopEntry(
                    item,
                    currency,
                    data.optInt("price", 1),
                    positions
            );
        }

        public static JSONObject convertToJSON(ShopEntry shopEntry) {
            JSONObject data = new JSONObject();

            data.put("item", JSONConfigUtils.serializeItem(shopEntry.item()));
            data.put("currency", shopEntry.currency().name());
            data.put("price", shopEntry.price());

            JSONArray positions = new JSONArray();
            for (GUIPosition guiPosition : shopEntry.positions) {
                positions.put(GUIPosition.convertToJSON(guiPosition));
            }
            data.put("positions", positions);

            return data;
        }

        public record GUIPosition(int page, int slot) {

            public static GUIPosition createFromJSON(JSONObject data) {
                int page = data.optInt("page", -1);
                int slot = data.optInt("slot", -1);
                //if (page < 0 || slot < 0) return null;
                return new GUIPosition(page, slot);
            }

            public static JSONObject convertToJSON(GUIPosition guiPosition) {
                JSONObject data = new JSONObject();
                data.put("page", guiPosition.page());
                data.put("slot", guiPosition.slot());
                return data;
            }

        }

    }

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
