package net.jandie1505.bedwars.base;

import net.jandie1505.bedwars.Bedwars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameInstance {
    @NotNull private final Bedwars bedwars;
    @NotNull private final GameData data;
    @Nullable private GameBase game;

    public GameInstance(@NotNull Bedwars bedwars) {
        this.bedwars = bedwars;
        this.data = new GameData();
    }

    public @NotNull GameData data() {
        return data;
    }

    public @Nullable GameBase game() {
        return game;
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
