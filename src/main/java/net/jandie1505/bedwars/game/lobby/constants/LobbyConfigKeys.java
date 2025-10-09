package net.jandie1505.bedwars.game.lobby.constants;

import org.jetbrains.annotations.NotNull;

public interface LobbyConfigKeys {
    String SLOT_SYSTEM_SECTION = "slotsystem";
    String CHAT = "chat";

    String SLOT_SYSTEM_TEAM_COUNT = SLOT_SYSTEM_SECTION + ".team_count";
    String SLOT_SYSTEM_PLAYERS_PER_TEAM = SLOT_SYSTEM_SECTION + ".players_per_team";

    String CHAT_ADD_PREFIX_SUFFIX = CHAT + ".add_prefix_suffix";

    static @NotNull String section(@NotNull String key) {
        return "lobby." + key;
    }
}
