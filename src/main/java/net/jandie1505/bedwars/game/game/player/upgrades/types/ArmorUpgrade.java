package net.jandie1505.bedwars.game.game.player.upgrades.types;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgradeManager;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ArmorUpgrade extends ItemUpgrade implements ManagedListener {
    @NotNull private final List<ArmorSet> armors;

    /**
     * Creates a new player upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public ArmorUpgrade(@NotNull PlayerUpgradeManager manager, @NotNull String id, @NotNull List<ArmorSet> armors) {
        super(manager, id, false, false);
        this.armors = armors;
    }

    // ----- REGISTER AND REMOVE -----

    @Override
    public void onRegister() {
        this.getManager().getGame().registerListener(this);
    }

    @Override
    public void onUnregister() {
        this.getManager().getGame().unregisterListener(this);
    }

    // ----- CHECKS -----

    @Override
    public boolean isEnabled(@NotNull UUID playerId, @Nullable Player player, @NotNull PlayerData playerData) {
        if (player == null) return false;
        return !player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }

    // ----- GIVE AND REMOVE ARMOR -----

    /**
     * Returns the ArmorSet of the specified level.
     * @param level level
     * @return ArmorSet of that level
     */
    private @Nullable ArmorSet getArmorSet(int level) {
        if (this.armors.isEmpty()) return null;

        if (level <= 0) return null;
        level -= 1;

        if (level >= this.armors.size()) return this.armors.getLast();

        return this.armors.get(level);
    }

    /**
     * Returns true if the specified item is an item of this upgrade.
     * @param item item
     * @return is upgrade item
     */
    public boolean isUpgradeItem(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").equals(this.getId());
    }

    /**
     * Sets the armor.
     * @param player player
     * @param level level
     */
    protected void giveItem(@NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeItem(player);

        @Nullable ArmorSet armorSet = getArmorSet(level);
        if (armorSet == null) return;

        @Nullable ItemStack helmet = this.prepareItem(armorSet.helmet(), player, playerData, level);
        @Nullable ItemStack chest =  this.prepareItem(armorSet.chest(), player, playerData, level);
        @Nullable ItemStack leggings =  this.prepareItem(armorSet.leggings(), player, playerData, level);
        @Nullable ItemStack boots = this.prepareItem(armorSet.boots(), player, playerData, level);

        if (helmet != null) player.getInventory().setHelmet(helmet);
        if (chest != null) player.getInventory().setChestplate(chest);
        if (leggings != null) player.getInventory().setLeggings(leggings);
        if (boots != null) player.getInventory().setBoots(boots);
    }

    /**
     * Sets the upgrade id to the item.
     * @param item item
     * @return changed item
     */
    private @Nullable ItemStack prepareItem(@Nullable ItemStack item, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        if (item == null) return null;
        item = item.clone();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // Set data values

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, this.getId());
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_UPGRADE_LEVEL, PersistentDataType.INTEGER, level);

        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_PREVENT_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(NamespacedKeys.GAME_ITEM_KEEP_IN_PLAYER_INVENTORY, PersistentDataType.BOOLEAN, true);

        // Set color

        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            BedwarsTeam team = this.getManager().getGame().getTeam(playerData.getTeam());
            if (team != null) {
                leatherArmorMeta.setColor(team.getData().color());
            }
        }

        item.setItemMeta(meta);

        return item;
    }

    // ----- EVENTS -----

    /**
     * Prevents removing the armor items from the armor slots.
     * @param event event
     */
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").equals(this.getId())) return;

        event.setCancelled(true);
    }

    /**
     * Prevents removing the armor items from the armor slots.
     * @param event event
     */
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.isCancelled()) return;

        for (ItemStack item : event.getNewItems().values()) {
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").equals(this.getId())) continue;

            event.setCancelled(true);
            break;
        }
    }

    /**
     * Prevents removing the armor items from the armor slots.
     * @param event event
     */
    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        if (event.isCancelled()) return;

        ItemStack item = event.getItemDrop().getItemStack();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.GAME_ITEM_UPGRADE_ID, PersistentDataType.STRING, "").equals(this.getId())) return;

        event.setCancelled(true);
    }

    // ----- INNER CLASSES -----

    public record ArmorSet(@Nullable ItemStack helmet, @Nullable ItemStack chest, @Nullable ItemStack leggings, @Nullable ItemStack boots) {

        public ArmorSet(@Nullable ItemStack helmet, @Nullable ItemStack chest, @Nullable ItemStack leggings, @Nullable ItemStack boots) {
            this.helmet = helmet != null ? helmet.clone(): null;
            this.chest = chest != null ? chest.clone(): null;
            this.leggings = leggings != null ? leggings.clone(): null;
            this.boots = boots != null ? boots.clone(): null;
        }

        @Override
        public @Nullable ItemStack helmet() {
            return helmet != null ? helmet.clone(): null;
        }

        @Override
        public @Nullable ItemStack chest() {
            return chest != null ? chest.clone(): null;
        }

        @Override
        public @Nullable ItemStack leggings() {
            return leggings != null ? leggings.clone(): null;
        }

        @Override
        public @Nullable ItemStack boots() {
            return boots != null ? boots.clone(): null;
        }

        @Override
        protected ArmorSet clone() throws CloneNotSupportedException {
            return new ArmorSet(helmet, chest, leggings, boots);
        }

        public @Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
            return switch (slot) {
                case HEAD -> this.helmet();
                case CHEST -> this.chest();
                case LEGS -> this.leggings();
                case FEET -> this.boots();
                default -> null;
            };
        }

    }

}
