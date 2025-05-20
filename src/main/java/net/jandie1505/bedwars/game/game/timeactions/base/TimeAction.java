package net.jandie1505.bedwars.game.game.timeactions.base;

import net.jandie1505.bedwars.game.game.Game;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an action that is executed when a specific time is reached.
 * The recommended way of creating TimeActions is the {@link net.jandie1505.bedwars.game.game.timeactions.provider.TimeActionCreator}.
 */
public abstract class TimeAction implements Comparable<TimeAction>, Runnable {
    private final Game game;
    private final TimeActionData data;
    private boolean completed;

    public TimeAction(Game game, TimeActionData data) {
        this.game = game;
        this.data = data;
        this.completed = false;
    }

    // RUN TIME ACTION

    protected abstract void onRun();

    @Nullable
    public abstract BaseComponent[] getMessage();

    @Nullable
    public abstract String getScoreboardText();

    /**
     * This method will be executed by the game when the time is reached.
     */
    public final void run() {

        this.onRun();

        BaseComponent[] message = this.getMessage();
        if (message != null && message.length > 0) {
            this.game.getPlugin().getServer().spigot().broadcast(message);
        }

        this.completed = true;

    }

    // GETTER / SETTER

    public final Game getGame() {
        return this.game;
    }

    public TimeActionData getData() {
        return data;
    }

    /**
     * Checks if the time action has already be completed.
     * @return completed status
     */
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * Marks the time action as completed without running it.
     */
    public void markAsCompleted() {
        this.completed = true;
    }

    // COMPARE

    @Override
    public int compareTo(TimeAction o) {
        return Integer.compare(o.data.time(), this.data.time());
    }

}
