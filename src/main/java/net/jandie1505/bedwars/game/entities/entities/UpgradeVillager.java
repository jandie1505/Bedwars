package net.jandie1505.bedwars.game.entities.entities;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.base.GuiNpc;
import net.jandie1505.bedwars.game.menu.upgrades.UpgradesMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

public class UpgradeVillager extends GuiNpc {
    final int team;

    /**
     * Creates a new UpgradesVillager.
     *
     * @param game     game
     * @param location location
     * @param team     the team the villager belongs to
     */
    public UpgradeVillager(@NotNull Game game, @NotNull Location location, int team) {
        super(game, location, () -> {
            Villager villager = game.getWorld().spawn(location.clone(), Villager.class);
            villager.setAI(false);
            villager.customName(Component.text("TEAM UPGRADES", NamedTextColor.GOLD, TextDecoration.BOLD));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setProfession(Villager.Profession.LIBRARIAN);
            villager.addScoreboardTag("upgrades.team." + team);
            return villager;
        }, player -> new UpgradesMenu(game, player.getUniqueId()).getUpgradesMenu());
        this.team = team;
    }

}
