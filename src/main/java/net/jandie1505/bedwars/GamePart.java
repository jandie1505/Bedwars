package net.jandie1505.bedwars;

import net.chaossquad.mclib.scheduler.TaskScheduler;

public abstract class GamePart {
    private final Bedwars plugin;
    private final TaskScheduler taskScheduler;

    public GamePart(Bedwars plugin) {
        this.plugin = plugin;
        this.taskScheduler = new TaskScheduler(this.plugin.getLogger());
    }

    public abstract boolean shouldRun();
    public abstract GamePart getNextStatus();

    public final boolean tick() {

        if (!this.shouldRun()) {
            this.plugin.getLogger().warning("§cRun condition failed");
            return false;
        }

        this.taskScheduler.tick();
        return true;
    }

    public Bedwars getPlugin() {
        return this.plugin;
    }

    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

}
