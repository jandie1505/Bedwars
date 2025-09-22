package net.jandie1505.bedwars.game.game.team.upgrades.types;

import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PermanentPotionEffectTeamUpgrade extends TeamUpgrade {
    @NotNull private final PotionEffectType type;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    /**
     * Creates a new team upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public PermanentPotionEffectTeamUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id, @NotNull PotionEffectType type, boolean ambient, boolean particles, boolean icon) {
        super(manager, id);
        this.type = type;
        this.ambient = ambient;
        this.particles = particles;
        this.icon = icon;
    }

    // ----- APPLY/REMOVE -----

    @Override
    protected void onApply(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.applyEffect(player, level);
    }

    @Override
    protected void onAffectedPlayerRespawn(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.applyEffect(player, level);
    }

    @Override
    protected void onRemove(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {
        this.removeEffect(player);
    }

    // ----- ACTIONS -----

    private void applyEffect(@NotNull Player player, int level) {
        this.removeEffect(player);

        level -= 1;
        if (level < 0) return;

        player.addPotionEffect(new PotionEffect(this.type, -1, level, this.ambient, this.particles, this.icon));
    }

    private void removeEffect(@NotNull Player player) {
        player.removePotionEffect(this.type);
    }

    // ----- INFO -----

    public @NotNull PotionEffectType getType() {
        return type;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isParticles() {
        return particles;
    }

    public boolean isIcon() {
        return icon;
    }

}
