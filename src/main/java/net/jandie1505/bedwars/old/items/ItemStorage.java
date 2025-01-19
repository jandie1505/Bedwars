package net.jandie1505.bedwars.old.items;

import net.jandie1505.bedwars.old.Bedwars;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ItemStorage {
    private final Bedwars plugin;
    private final Map<Integer, ItemStack> items;

    public ItemStorage(Bedwars plugin) {
        this.plugin = plugin;
        this.items = Collections.synchronizedMap(new HashMap<>());
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    private Map<Integer, ItemStack> getItemsInternal() {
        return Map.copyOf(this.items);
    }

    public ItemStack getItem(int itemId) {
        ItemStack i1 = this.items.get(itemId);

        if (i1 == null) {
            return null;
        }

        ItemStack item = i1.clone();

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = this.plugin.getServer().getItemFactory().getItemMeta(item.getType());
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(0, String.valueOf(itemId));

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public int getItemId(ItemStack item) {

        if (item == null) {
            return -1;
        }

        if (item.getItemMeta() == null) {
            return -1;
        }

        if (item.getItemMeta().getLore() == null) {
            return -1;
        }

        if (item.getItemMeta().getLore().isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(item.getItemMeta().getLore().get(0));
        } catch (IllegalArgumentException e) {
            return -1;
        }

    }

    public void addItem(int id, ItemStack item) {
        this.items.put(id, item.clone());
    }

    public boolean removeItem(int id) {
        return this.items.remove(id) != null;
    }

    public void clearItems() {
        this.items.clear();
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

            // Amount

            int amount = itemValue.optInt("amount", -1);

            if (amount > 0) {
                item.setAmount(amount);
            }

            // Name

            String itemName = itemValue.optString("name");

            if (itemName != null && !itemName.equals("")) {
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

            // Potion Meta

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;

                String basePotionData = itemValue.optString("basePotionData");

                if (basePotionData != null) {

                    try {
                        potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(basePotionData)));
                    } catch (IllegalArgumentException ignored) {
                        // ignored
                    }

                }

                JSONArray potionEffects = itemValue.optJSONArray("potionEffects");

                if (potionEffects != null) {

                    for (Object object : potionEffects) {

                        if (!(object instanceof JSONObject)) {
                            continue;
                        }

                        JSONObject potionEffect = (JSONObject) object;

                        PotionEffectType effectType = PotionEffectType.getByName(potionEffect.optString("type"));

                        if (effectType == null) {
                            continue;
                        }

                        int duration = potionEffect.optInt("duration", -1);

                        if (duration < 0) {
                            continue;
                        }

                        int amplifier = potionEffect.optInt("amplifier", -1);

                        if (amplifier < 0) {
                            continue;
                        }

                        boolean ambient = potionEffect.optBoolean("ambient", true);

                        boolean particles = potionEffect.optBoolean("particles", true);

                        boolean override = potionEffect.optBoolean("override", false);

                        potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, particles), override);

                    }

                }

                int color = itemValue.optInt("color", -1);

                if (color >= 0) {
                    try {
                        potionMeta.setColor(Color.fromRGB(color));
                    } catch (IllegalArgumentException ignored) {
                        // ignored
                    }
                }

            }

            // Set item meta

            item.setItemMeta(meta);

            // Add item

            this.addItem(itemId, item);
        }

    }

    public Map<Integer, ItemStack> getItems() {
        Map<Integer, ItemStack> returnMap = new HashMap<>();

        for (Integer key : this.getItemsInternal().keySet()) {
            ItemStack value = this.items.get(key);

            if (value == null) {
                continue;
            }

            returnMap.put(key.intValue(), value.clone());
        }

        return Map.copyOf(returnMap);
    }

    public boolean isArmorItem(ItemStack item) {

        if (item == null) {
            return false;
        }

        Material itemType = item.getType();

        return itemType == Material.LEATHER_BOOTS ||
                itemType == Material.LEATHER_LEGGINGS ||
                itemType == Material.LEATHER_CHESTPLATE ||
                itemType == Material.LEATHER_HELMET ||
                itemType == Material.CHAINMAIL_BOOTS ||
                itemType == Material.CHAINMAIL_LEGGINGS ||
                itemType == Material.CHAINMAIL_CHESTPLATE ||
                itemType == Material.CHAINMAIL_HELMET ||
                itemType == Material.IRON_BOOTS ||
                itemType == Material.IRON_LEGGINGS ||
                itemType == Material.IRON_CHESTPLATE ||
                itemType == Material.IRON_HELMET ||
                itemType == Material.GOLDEN_BOOTS ||
                itemType == Material.GOLDEN_LEGGINGS ||
                itemType == Material.GOLDEN_CHESTPLATE ||
                itemType == Material.GOLDEN_HELMET ||
                itemType == Material.DIAMOND_BOOTS ||
                itemType == Material.DIAMOND_LEGGINGS ||
                itemType == Material.DIAMOND_CHESTPLATE ||
                itemType == Material.DIAMOND_HELMET ||
                itemType == Material.NETHERITE_BOOTS ||
                itemType == Material.NETHERITE_LEGGINGS ||
                itemType == Material.NETHERITE_CHESTPLATE ||
                itemType == Material.NETHERITE_HELMET;
    }

    public ItemStack colorArmor(ItemStack item, Color color) {

        if (item == null) {
            return null;
        }

        if (item.getItemMeta() == null) {
            item.setItemMeta(this.plugin.getServer().getItemFactory().getItemMeta(item.getType()));
        }

        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
            return item;
        }

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(color);

        item.setItemMeta(meta);

        return item;
    }

    public Material getArmorPiece(Material type, int piece) {
        String typeString = type.name();
        String[] parts = typeString.split("_");

        if (parts.length != 2) {
            return type;
        }

        String newPart;

        switch (piece) {
            case 0:
                newPart = "HELMET";
                break;
            case 1:
                newPart = "CHESTPLATE";
                break;
            case 2:
                newPart = "LEGGINGS";
                break;
            case 3:
                newPart = "BOOTS";
                break;
            default:
                newPart = parts[1];
                break;
        }

        return Material.getMaterial(parts[0] + "_" + newPart);
    }

    public ItemStack copyItemMeta(ItemStack oldArmor, Material target) {
        ItemStack item = oldArmor.clone();

        item.setType(target);

        return item;
    }
}
