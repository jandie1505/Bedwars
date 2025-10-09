package net.jandie1505.bedwars.game.game.team.upgrades.types;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.chaossquad.mclib.json.JSONConfigUtils;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class PermanentPotionEffectTeamUpgrade extends TeamUpgrade {
    @NotNull public static final String TYPE = "permanent_potion_effect";
    @NotNull private final PotionEffectType effectType;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    /**
     * Creates a new team upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public PermanentPotionEffectTeamUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id, @NotNull PotionEffectType effectType, boolean ambient, boolean particles, boolean icon) {
        super(manager, id);
        this.effectType = effectType;
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

        player.addPotionEffect(new PotionEffect(this.effectType, -1, level, this.ambient, this.particles, this.icon));
    }

    private void removeEffect(@NotNull Player player) {
        player.removePotionEffect(this.effectType);
    }

    // ----- INFO -----

    public @NotNull PotionEffectType getEffectType() {
        return effectType;
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

    // ----- DATA -----

    /**
     * Data of PermanentPotionEffectTeamUpgrade.
     * @param id id
     * @param effectType effect type
     * @param ambient ambient
     * @param particles particles
     * @param icon icon
     */
    public record Data(@NotNull String id, @NotNull PotionEffectType effectType, boolean ambient, boolean particles, boolean icon) implements TeamUpgrade.Data {

        @Override
        public @NotNull String type() {
            return TYPE;
        }

        @Override
        public @NotNull TeamUpgrade buildUpgrade(@NotNull TeamUpgradeManager manager) {
            return new PermanentPotionEffectTeamUpgrade(manager, this.id(), this.effectType(), this.ambient, this.particles, this.icon);
        }

        public static @NotNull Data fromJSON(@NotNull String id, @NotNull JSONObject json) throws JSONException {

            PotionEffectType effectType = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(JSONConfigUtils.deserializeNamespacedKey(json.getJSONObject("effect")));
            if (effectType == null) throw new IllegalArgumentException("Invalid potion effect type");

            boolean ambient = json.getBoolean("ambient");
            boolean particles = json.getBoolean("particles");
            boolean icon = json.getBoolean("icon");

            return new Data(id, effectType, ambient, particles, icon);
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("type", this.type());
            json.put("effect", JSONConfigUtils.serializeNamespacedKey(this.effectType.getKey()));
            json.put("ambient", this.ambient);
            json.put("particles", this.particles);
            json.put("icon", this.icon);

            return json;
        }
    }

    public @NotNull Data getData() {
        return new Data(this.getId(), this.effectType, this.ambient, this.particles, this.icon);
    }

}
