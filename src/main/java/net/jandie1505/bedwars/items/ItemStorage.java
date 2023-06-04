package net.jandie1505.bedwars.items;

import net.jandie1505.bedwars.Bedwars;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ItemStorage {
    private final Bedwars plugin;
    private final Map<Integer, ItemStack> items;
    private final Map<Integer, Integer> priceList;

    public ItemStorage(Bedwars plugin) {
        this.plugin = plugin;
        this.items = Collections.synchronizedMap(new HashMap<>());
        this.priceList = Collections.synchronizedMap(new HashMap<>());
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public Map<Integer, ItemStack> getItems() {
        return Map.copyOf(this.items);
    }

    public ItemStack getItem(int itemId, boolean menuItem) {

        ItemStack item = this.items.get(itemId);

        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = this.plugin.getServer().getItemFactory().getItemMeta(item.getType());
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        String menuItemString;

        if (menuItem) {
            menuItemString = "1-";
        } else {
            menuItemString = "0-";
        }

        String shopItemString;

        if (this.priceList.containsKey(itemId)) {
            shopItemString = "1-";
        } else {
            shopItemString = "0-";
        }

        lore.add(0, menuItemString + shopItemString + itemId);

        return item;
    }

    private String[] getIdArray(ItemStack item) {

        if (item == null) {
            return null;
        }

        if (item.getItemMeta() == null) {
            return null;
        }

        if (item.getItemMeta().getLore() == null) {
            return null;
        }

        if (item.getItemMeta().getLore().isEmpty()) {
            return null;
        }

        String[] itemIdArray = item.getItemMeta().getLore().get(0).split("-");

        if (itemIdArray.length < 3) {
            return null;
        }

        return itemIdArray;
    }

    public int getItemId(ItemStack item) {

        try {
            return Integer.parseInt(this.getIdArray(item)[2]);
        } catch (IllegalArgumentException e) {
            return -1;
        }

    }

    public boolean isMenuItem(ItemStack item) {
        String[] itemIdArray = this.getIdArray(item);

        if (itemIdArray == null) {
            return false;
        }

        return itemIdArray[0].equals("1");
    }

    public boolean isShopItem(ItemStack item) {
        String[] itemIdArray = this.getIdArray(item);

        if (itemIdArray == null) {
            return false;
        }

        return itemIdArray[1].equals("1");
    }

    public void addItem(int id, ItemStack item) {
        this.items.put(id, item);
    }

    public void initItems() {

        JSONObject itemsConfig = this.plugin.getItemConfig().getConfig();

        for (String itemKey : itemsConfig.keySet()) {

            // Item Id

            int itemId;

            try {
                itemId = Integer.parseInt(itemKey);
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Item " + itemKey + " could not be initialized: Key could not be converted to int");
                continue;
            }

            // Check if object is json object

            Object valueObject = itemsConfig.get(itemKey);

            if (!(valueObject instanceof JSONObject)) {
                this.plugin.getLogger().warning("Item " + itemKey + " could not be initialized: Not a json object");
                continue;
            }

            // Material Type

            JSONObject itemValue = (JSONObject) valueObject;

            Material material = Material.getMaterial(itemValue.optString("type", ""));

            if (material == null) {
                this.plugin.getLogger().info("Item " + itemKey + " could not be initialized: Unknown item type");
                continue;
            }

            // Item Stack Init

            ItemStack item = new ItemStack(material);
            ItemMeta meta = this.plugin.getServer().getItemFactory().getItemMeta(item.getType());

            // Name

            String itemName = itemValue.optString("name");

            if (itemName != null) {
                meta.setDisplayName("§r" + itemName);
            }

            // Lore

            JSONArray itemLoreArray = itemValue.optJSONArray("lore");

            if (itemLoreArray != null) {

                List<String> itemLore = new ArrayList<>();

                for (Object object : itemLoreArray) {

                    if (!(object instanceof String)) {
                        continue;
                    }

                    itemLore.add((String) object);

                }

                if (!itemLore.isEmpty()) {
                    meta.setLore(itemLore);
                }

            }

            // Enchantments

            JSONArray enchantmentsArray = itemValue.optJSONArray("enchantments");

            if (enchantmentsArray != null) {

                for (Object object : enchantmentsArray) {

                    if (!(object instanceof JSONObject)) {
                        continue;
                    }

                    JSONObject enchantmentObject = (JSONObject) object;

                    String enchantmentTypeString = enchantmentObject.optString("type");

                    Enchantment enchantment = Enchantment.getByName(enchantmentTypeString);

                    if (enchantment == null) {
                        continue;
                    }

                    int enchamtmentLevel = enchantmentObject.optInt("level", -1);

                    if (enchamtmentLevel < 0) {
                        continue;
                    }

                    meta.addEnchant(enchantment, enchamtmentLevel, true);

                }

            }

            // Unbreakable

            meta.setUnbreakable(itemValue.optBoolean("unbreakable", true));

            // Item Flags

            JSONArray itemFlagsArray = itemValue.optJSONArray("lore");

            if (itemFlagsArray != null) {

                for (Object object : itemFlagsArray) {

                    if (!(object instanceof String)) {
                        continue;
                    }

                    ItemFlag itemFlag;
                    try {
                        itemFlag = ItemFlag.valueOf((String) object);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }

                    meta.addItemFlags(itemFlag);

                }

            }

            this.addItem(itemId, item);
        }

    }

}
