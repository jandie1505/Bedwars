package net.jandie1505.bedwars.game.timeactions;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.entity.Player;

public abstract class TimeAction implements Comparable<TimeAction> {
    private final Game game;
    private final int time;
    private final String message;
    private final String scoreboardText;
    private boolean completed;

    public TimeAction(Game game, int time, String message, String scoreboardText) {
        this.game = game;
        this.time = time;
        this.message = message;
        this.scoreboardText = scoreboardText;
        this.completed = false;
    }

    protected abstract void run();

    public void execute() {

        this.run();

        if (this.message != null) {
            for (Player player : this.game.getWorld().getPlayers()) {
                player.sendMessage(this.message);
            }
        }

        this.completed = true;
    }

    public Game getGame() {
        return this.game;
    }

    public int getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getScoreboardText() {
        return scoreboardText;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int compareTo(TimeAction o) {
        return Integer.compare(o.getTime(), this.time);
    }
}
