package net.jandie1505.bedwars.game.entities;

import net.jandie1505.bedwars.GameListener;
import net.jandie1505.bedwars.game.Game;
import org.bukkit.entity.Entity;

public abstract class ManagedEntity<T extends Entity> implements GameListener {
    private final Game game;
    private final T entity;

    protected ManagedEntity(Game game, T entity) {
        this.game = game;
        this.entity = entity;

        this.game.getPlugin().registerListener(this);

        this.game.addManagedEntity(this);
    }

    // GETTER

    public final Game getGame() {
        return this.game;
    }

    public final boolean toBeRemoved() {
        return this.entity == null || this.entity.isDead();
    }

    public T getEntity() {
        return this.entity;
    }

}
