package net.jandie1505.bedwars.game.entities.base;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class ExpiringManagedEntity<T extends Entity> extends ManagedEntity<T> {
    private final int maxTime;
    private int time;

    protected ExpiringManagedEntity(@NotNull Game game, @NotNull T entity, int maxTime) {
        super(game, entity);
        this.maxTime = maxTime;
        this.time = this.maxTime;

        this.scheduleRepeatingTask(this::countDownTask, 1, 20, "managed_entity_expiration");
    }

    // TASKS

    /**
     * Counts down until the timer reaches 0, then removes the entity.
     */
    private void countDownTask() {
        if (this.getEntity() == null || this.getEntity().isDead()) return;

        if (this.time > 0) {
            this.time--;
        } else {
            this.getEntity().remove();
        }

    }

    // GETTER AND SETTER

    public final int getTime() {
        return this.time;
    }

    public final void setTime(int time) {
        this.time = time;
    }

    public final int getMaxTime() {
        return this.maxTime;
    }

}
