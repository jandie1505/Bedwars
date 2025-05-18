package net.jandie1505.bedwars.game.entities.base;

import net.chaossquad.mclib.entity.SingleUseManagedEntity;
import net.jandie1505.bedwars.game.Game;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class ManagedEntity<T extends Entity> extends SingleUseManagedEntity<T> {
    private final Game game;

    protected ManagedEntity(@NotNull Game game, @NotNull T entity) {
        super(game.getTaskScheduler(), game, () -> entity);
        this.game = game;

        this.game.addManagedEntity(this);
    }

    // GETTER

    public final Game getGame() {
        return this.game;
    }

}
