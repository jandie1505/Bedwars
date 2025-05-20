package net.jandie1505.bedwars.game.lobby.inventory;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.jandie1505.bedwars.game.lobby.LobbyPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VotingMenuListener implements ManagedListener {
    @NotNull private final Lobby lobby;

    public VotingMenuListener(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof VotingMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        LobbyPlayerData playerData = this.lobby.getPlayerData(player);
        if (playerData == null) return;

        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) return;
        if (event.getCurrentItem() == null) return;

        int itemId = this.lobby.getPlugin().getItemStorage().getItemId(event.getCurrentItem());
        if (itemId < 0) return;

        if (itemId != this.lobby.getMapButtonItemId()) {
            return;
        }

        if (!this.lobby.isMapVoting()) {
            event.getWhoClicked().sendMessage("§cMap voting is currently disabled");
            event.getWhoClicked().closeInventory();
            return;
        }

        if (this.lobby.getSelectedMap() != null) {
            event.getWhoClicked().sendMessage("§cMap voting is already over");
            event.getWhoClicked().closeInventory();
            return;
        }

        List<String> lore = event.getCurrentItem().getItemMeta().getLore();

        if (lore.size() < 2) {
            return;
        }

        for (MapData map : this.lobby.getMaps()) {

            if (map.world().equals(lore.get(1))) {

                event.getWhoClicked().closeInventory();

                if (playerData.getVote() == map) {

                    playerData.setVote(null);
                    event.getWhoClicked().sendMessage("§aYou removed your vote");

                } else {

                    playerData.setVote(map);
                    event.getWhoClicked().sendMessage("§aYou changed your vote to " + map.world());

                }

                return;
            }

        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof VotingMenu)) return;
        event.setCancelled(true);
    }

    // ----- OTHER -----

    public @NotNull Lobby getGame() {
        return lobby;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
