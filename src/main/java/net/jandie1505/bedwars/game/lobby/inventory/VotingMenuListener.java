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
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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

        for (Map.Entry<String, MapData> entry : this.lobby.getMaps().entrySet()) {

            if (entry.getValue().world().equals(lore.get(1))) {

                event.getWhoClicked().closeInventory();

                if (entry.getKey().equals(playerData.getVote())) {

                    playerData.setVote(null);
                    event.getWhoClicked().sendMessage("§aYou removed your vote");

                } else {

                    playerData.setVote(entry.getKey());
                    event.getWhoClicked().sendMessage("§aYou changed your vote to " + entry.getValue().name());

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!this.lobby.isPlayerIngame(event.getPlayer())) return;
        if (!event.getAction().isRightClick()) return;

        int clickedItemId = this.getGame().getPlugin().getItemStorage().getItemId(event.getItem());
        if (clickedItemId < 0) return;
        if (clickedItemId != this.lobby.getMapVoteButtonItemId()) return;

        event.setCancelled(true);

        if (!this.lobby.isMapVoting()) {
            event.getPlayer().sendRichMessage("<red>Map voting is currently disabled");
            event.getPlayer().closeInventory();
            return;
        }

        if (this.lobby.getSelectedMap() != null) {
            event.getPlayer().sendRichMessage("<red>Map voting is already over");
            event.getPlayer().closeInventory();
            return;
        }

        event.getPlayer().openInventory(new VotingMenu(this.lobby, event.getPlayer().getUniqueId()).getVotingMenu());
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
