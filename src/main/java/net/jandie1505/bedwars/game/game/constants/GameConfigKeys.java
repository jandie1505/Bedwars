package net.jandie1505.bedwars.game.game.constants;

import org.jetbrains.annotations.NotNull;

public interface GameConfigKeys {
    @NotNull String SECTION_REWARDS = "rewards";

    @NotNull String TNT_PARTICLES = "tnt_particles";
    @NotNull String INVENTORY_SORT = "inventory_sort";

    @NotNull String REWARD_VICTORY = SECTION_REWARDS + ".victory";
    @NotNull String REWARD_BED_DESTROYED = SECTION_REWARDS + ".bed_destroyed";
    @NotNull String REWARD_PLAYER_KILL = SECTION_REWARDS + ".player_kill";
    @NotNull String REWARD_TEAM_UPGRADE_PURCHASE = SECTION_REWARDS + ".purchase.team_upgrade";
    @NotNull String REWARD_TEAM_TRAP_PURCHASE = SECTION_REWARDS + ".purchase.team_trap";
    @NotNull String REWARD_PLAYER_UPGRADE_PURCHASE = SECTION_REWARDS + ".purchase.player_upgrade";
    @NotNull String REWARD_LIMIT =  SECTION_REWARDS + ".limit";

    static @NotNull String section(@NotNull String key) {
        return "game." + key;
    }
}
