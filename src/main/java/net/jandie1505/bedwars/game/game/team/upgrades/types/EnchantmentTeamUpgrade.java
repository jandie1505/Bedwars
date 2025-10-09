package net.jandie1505.bedwars.game.game.team.upgrades.types;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.chaossquad.mclib.json.JSONConfigUtils;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.KEM;

public class EnchantmentTeamUpgrade extends TeamUpgrade {
    @NotNull public static final String TYPE = "enchantment";
    @NotNull private final NamespacedKey persistentDataKey;
    @NotNull private final Enchantment enchantment;

    /**
     * Creates a new team upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public EnchantmentTeamUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id, @NotNull NamespacedKey persistentDataKey, @NotNull Enchantment enchantment) {
        super(manager, id);
        this.persistentDataKey = persistentDataKey;
        this.enchantment = enchantment;

        this.scheduleRepeatingTask(this::task, 1, 10*20, "enchant_items");
    }

    // ----- APPLY -----

    @Override
    protected void onApply(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.manageEnchantment(player, level);
    }

    @Override
    protected void onRemove(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.manageEnchantment(player, 0); // Level 0 because of remove, else it would apply the removed level
    }

    private void task(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.manageEnchantment(player, level);
    }

    // ----- STUFF -----

    private void manageEnchantment(@NotNull Player player, int level) {

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            if (!meta.getPersistentDataContainer().getOrDefault(this.persistentDataKey, PersistentDataType.BOOLEAN, false)) continue;

            if (level > 0) {

                if (meta.getEnchantLevel(this.enchantment) != level) {
                    meta.removeEnchant(this.enchantment);
                    meta.addEnchant(this.enchantment, level, true);
                    item.setItemMeta(meta);
                    continue;
                }

            } else {

                if (meta.getEnchantLevel(this.enchantment) > 0) {
                    meta.removeEnchant(this.enchantment);
                    item.setItemMeta(meta);
                    continue;
                }

            }

        }

    }

    // ----- INFO -----

    public @NotNull NamespacedKey getPersistentDataKey() {
        return persistentDataKey;
    }

    public @NotNull Enchantment getEnchantment() {
        return enchantment;
    }

    // ----- DATA -----

    /**
     * Data of EnchantmentTeamUpgrade.
     * @param id id
     * @param key key which has to be stored in the PersistentDataContainer of the item that the enchantment is applied to it
     * @param enchantment the enchantment that will be applied to affected items
     */
    public record Data(@NotNull String id, @NotNull NamespacedKey key, @NotNull Enchantment enchantment) implements TeamUpgrade.Data {

        @Override
        public @NotNull String type() {
            return TYPE;
        }

        @Override
        public @NotNull TeamUpgrade buildUpgrade(@NotNull TeamUpgradeManager manager) {
            return new EnchantmentTeamUpgrade(manager, this.id, this.key, this.enchantment);
        }

        public static @NotNull Data fromJSON(@NotNull String id, @NotNull JSONObject json) throws JSONException {

            // Key
            NamespacedKey key = JSONConfigUtils.deserializeNamespacedKey(json.getJSONObject("key"));

            // Enchantment
            NamespacedKey enchantmentKey = JSONConfigUtils.deserializeNamespacedKey(json.getJSONObject("enchantment"));
            Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(enchantmentKey);
            if (enchantment == null) throw new IllegalArgumentException("Enchantment not found for key " + enchantmentKey);

            return new Data(id, key, enchantment);
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("type", this.type());
            json.put("key", JSONConfigUtils.serializeNamespacedKey(this.key));
            json.put("enchantment", JSONConfigUtils.serializeNamespacedKey(this.enchantment.getKey()));

            return json;
        }
    }

    public @NotNull Data getData() {
        return new Data(this.getId(), this.persistentDataKey, this.enchantment);
    }

}
