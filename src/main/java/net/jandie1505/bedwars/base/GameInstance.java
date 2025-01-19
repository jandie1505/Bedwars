package net.jandie1505.bedwars.base;

import net.jandie1505.bedwars.Bedwars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data structure to save the game in the plugin.<br/>
 * Saves data about games such as game pause state.<br/>
 * If a game switches between lobby, game and endlobby, the game object is changed in this instance.
 */
public final class GameInstance {
    @NotNull private final Bedwars bedwars;
    private final int gameId;
    @NotNull private final GameData data;
    @Nullable private GameBase game;

    public GameInstance(@NotNull Bedwars bedwars, int gameId) {
        this.bedwars = bedwars;
        this.gameId = gameId;
        this.data = new GameData();
    }

    public @NotNull Bedwars plugin() {
        return bedwars;
    }

    public int gameId() {
        return gameId;
    }

    public @NotNull GameData data() {
        return data;
    }

    public @Nullable GameBase game() {
        return game;
    }

    public void setGame(@Nullable GameBase game) {
        this.game = game;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(game);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

}
