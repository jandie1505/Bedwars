package net.jandie1505.bedwars.game.game.entities.entities;

import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.base.GuiNpc;
import net.jandie1505.bedwars.game.game.menu.shop.old.ShopMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ShopVillager extends GuiNpc {
    final int team;

    /**
     * Creates a new ShopVillager.
     *
     * @param game     game
     * @param location location
     * @param team     the team the villager belongs to
     */
    public ShopVillager(@NotNull Game game, @NotNull Location location, int team) {
        super(game, location, () -> {
            Villager villager = game.getWorld().spawn(location.clone(), Villager.class);
            villager.setAI(false);
            villager.customName(Component.text("ITEM SHOP", NamedTextColor.GOLD, TextDecoration.BOLD));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setProfession(Villager.Profession.WEAPONSMITH);
            villager.addScoreboardTag("shop.team." + team);
            villager.getPersistentDataContainer().set(NamespacedKeys.ENTITY_PEARL_SWAP_EXCLUDED, PersistentDataType.BOOLEAN, true);
            return villager;
        }, player -> new ShopMenu(game, player.getUniqueId()).getPage(0));
        this.team = team;
    }

}
