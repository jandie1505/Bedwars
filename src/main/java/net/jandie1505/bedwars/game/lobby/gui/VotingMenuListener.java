package net.jandie1505.bedwars.game.lobby.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.lobby.Lobby;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class VotingMenuListener implements ManagedListener {
    @NotNull private final Lobby lobby;

    public VotingMenuListener(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        // Only right-clicks from now on
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.LOBBY_ITEM_VOTING_MENU, PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);

            if (this.lobby.getSelectedMap() != null) {
                event.getPlayer().sendRichMessage("<red>Map voting is already over");
                event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 0.0F);
                return;
            }

            event.getPlayer().openInventory(this.lobby.getMapVoteGUI().getInventory(event.getPlayer()));
            event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        }/* else if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_TEAM_SELECTION_MENU, PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);
            event.getPlayer().openInventory(this.getTeamSelectionGUI().getInventory(event.getPlayer()));
            event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        }*/

    }

    // ----- OTHER -----

    public @NotNull Lobby getLobby() {
        return lobby;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
