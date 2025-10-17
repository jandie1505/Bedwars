package net.jandie1505.bedwars.game.game.timeactions.base;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.actions.DestroyBedsAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.EndgameWitherTimeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.GeneratorUpgradeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.WorldborderChangeTimeAction;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Represents an action that is executed when a specific time is reached.
 * The recommended way of creating TimeActions is the {@link net.jandie1505.bedwars.game.game.timeactions.provider.TimeActionCreator}.
 */
public abstract class TimeAction implements Comparable<TimeAction> {
    @NotNull private final Game game;
    @NotNull private final String id;
    private final int time;

    private boolean completed;

    public TimeAction(@NotNull Game game, @NotNull String id,  int time) {
        this.game = game;
        this.id = id;
        this.time = time;

        this.completed = false;
    }

    // ----- RUN -----

    protected abstract void onRun();

    /**
     * Runs the TimeAction.
     */
    public final void run() {

        this.onRun();

        Component message = this.getChatMessage();
        if (message != null) {
            this.game.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        }

        this.completed = true;
    }

    // ----- MESSAGES -----

    public abstract @Nullable Component getChatMessage();

    public abstract @Nullable Component getScoreboardText();

    // ----- VALUES -----

    public final @NotNull Game getGame() {
        return this.game;
    }

    /**
     * Returns the id of this TimeAction.
     * @return id
     */
    public final @NotNull String getId() {
        return this.id;
    }

    /**
     * Returns the time the TimeAction is triggered.
     * @return time
     */
    public final int getTime() {
        return this.time;
    }

    /**
     * Checks if the time action has already be completed.
     * @return completed status
     */
    public final boolean isCompleted() {
        return this.completed;
    }

    /**
     * Sets if the time action has been completed.
     */
    public final void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // ----- COMPARE -----

    @Override
    public final int compareTo(@NotNull TimeAction other) {
        return Integer.compare(other.getTime(), this.getTime());
    }

    // ----- BUILDER -----

    public interface Data {
        @NotNull String id();
        @NotNull String type();
        @NotNull TimeAction build(@NotNull Game game);
        @NotNull JSONObject toJSON();
    }

    public static @Nullable Data getDataFromJSONByType(@NotNull JSONObject json) {
        return switch (json.optString("type")) {
            case "destroy_beds" -> DestroyBedsAction.Data.fromJSON(json);
            case "endgame_withers" -> EndgameWitherTimeAction.Data.fromJSON(json);
            case "generator_upgrade" -> GeneratorUpgradeAction.Data.fromJSON(json);
            case "worldborder_change" -> WorldborderChangeTimeAction.Data.fromJSON(json);
            default -> null;
        };
    }

}
