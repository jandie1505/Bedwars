package net.jandie1505.bedwars.lobby.inventory;

import net.jandie1505.bedwars.lobby.Lobby;
import net.jandie1505.bedwars.lobby.LobbyPlayerData;
import net.jandie1505.bedwars.game.MapData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class VotingMenu implements InventoryHolder {
    private final Lobby lobby;
    private final UUID playerId;

    public VotingMenu(Lobby lobby, UUID playerId) {
        this.lobby = lobby;
        this.playerId = playerId;
    }

    @Override
    public Inventory getInventory() {
        return this.lobby.getPlugin().getServer().createInventory(this, 27, "§c§mMap Voting");
    }

    public Inventory getVotingMenu() {
        int inventorySize = ((this.lobby.getMaps().size() / 9) + 1) * 9;

        if (inventorySize > 54) {
            inventorySize = 54;
        }

        if (inventorySize < 27) {
            inventorySize = 27;
        }

        Inventory inventory = this.lobby.getPlugin().getServer().createInventory(this, inventorySize, "§6§lMap Voting");

        LobbyPlayerData playerData = this.lobby.getPlayers().get(this.playerId);

        if (playerData == null) {
            return this.getInventory();
        }

        int slot = 0;
        for (MapData map : this.lobby.getMaps()) {

            if (slot >= inventorySize) {
                break;
            }

            inventory.setItem(slot, getLobbyVoteMapButton(map.name(), map.world(), playerData.getVote() == map));

            slot++;
        }

        return inventory;
    }

    public ItemStack getLobbyVoteMapButton(String mapName, String worldName, boolean selected) {
        String colorCode;

        if (selected) {
            colorCode = "§r§a";
        } else {
            colorCode = "§r§6";
        }

        ItemStack itemStack = this.lobby.getPlugin().getItemStorage().getItem(this.lobby.getMapButtonItemId());

        if (itemStack == null) {
            return new ItemStack(Material.AIR);
        }

        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName("§r" + colorCode + mapName);

        List<String> lore = meta.getLore();
        lore.add(worldName);
        meta.setLore(lore);

        if (selected) {
            meta.addEnchant(Enchantment.FORTUNE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
