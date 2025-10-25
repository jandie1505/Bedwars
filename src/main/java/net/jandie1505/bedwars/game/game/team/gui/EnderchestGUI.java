package net.jandie1505.bedwars.game.game.team.gui;

import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderchestGUI implements ManagedListener, InventoryHolder {
    @NotNull public static final NamespacedKey BUTTON_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "item.game.enderchest_gui.button_id");

    @NotNull private final Game game;
    @NotNull private final Removable removable;

    public EnderchestGUI(@NotNull Game game, @Nullable Removable removable) {
        this.game = game;
        this.removable = removable != null ? removable : () -> false;

        this.game.registerListener(this);
    }

    // ----- INVENTORY -----

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, Component.text("Enderchest Menu", NamedTextColor.DARK_AQUA));

        inventory.setItem(11, this.getTeamChestIcon());
        inventory.setItem(13, this.getPlayerChestIcon());
        inventory.setItem(15, this.getResourceStorageIcon());

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) continue;
            inventory.setItem(slot, this.game.getTeamGUI().getPlaceholderItem());
        }

        return inventory;
    }

    private @NotNull ItemStack getTeamChestIcon() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();

        meta.customName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Team Chest", NamedTextColor.GOLD)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Opens the team chest", NamedTextColor.GRAY))));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(BUTTON_ID, PersistentDataType.STRING, "team_chest");

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getPlayerChestIcon() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();

        meta.customName(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Player Chest", NamedTextColor.GOLD)));
        meta.lore(List.of(ItemUtils.CLEARED_LORE_COMPONENT.append(Component.text("Opens your private chest", NamedTextColor.GRAY))));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(BUTTON_ID, PersistentDataType.STRING, "player_chest");

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack getResourceStorageIcon() {
        ItemStack item = this.game.getTeamGUI().getResourceStorageIcon(false);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(BUTTON_ID, PersistentDataType.STRING, "resource_storage");
        item.setItemMeta(meta);
        return item;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String buttonId = meta.getPersistentDataContainer().get(BUTTON_ID, PersistentDataType.STRING);
        if (buttonId == null) return;

        switch (buttonId) {
            case "team_chest" -> {

                BedwarsTeam team = this.game.getTeam(playerData.getTeam());
                if (team == null) return;

                Inventory teamChest = team.getTeamChest();
                if (teamChest == null) {
                    player.closeInventory();
                    player.sendRichMessage("<red>Your team does not have a team chest!");
                    player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1, 0);
                    return;
                }

                player.openInventory(teamChest);
            }
            case "player_chest" -> {
                player.openInventory(playerData.getEnderchest());
            }
            case "resource_storage" -> {
                player.closeInventory();
                player.sendRichMessage("<red>Currently not implemented!");
            }
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractForEnderChest(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENDER_CHEST) return;

        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        event.setCancelled(true);
        event.getPlayer().openInventory(this.game.getEnderchestGUI().getInventory());
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return this.removable.toBeRemoved();
    }

}
