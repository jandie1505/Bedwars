package net.jandie1505.bedwars.base;

import net.chaossquad.mclib.dynamicevents.ListenerOwner;
import net.chaossquad.mclib.scheduler.TaskScheduler;
import net.jandie1505.bedwars.Bedwars;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class GameBase implements ListenerOwner {
    @NotNull private final Bedwars plugin;
    @NotNull private final TaskScheduler taskScheduler;
    @NotNull private final List<ManagedListener> listeners;

    public GameBase(@NotNull Bedwars plugin) {
        this.plugin = plugin;
        this.taskScheduler = new TaskScheduler(this.plugin.getLogger());
        this.listeners = new ArrayList<>();

        this.taskScheduler.scheduleRepeatingTask(this::cleanupListeners, 1, 100);
    }

    // ----- PLUGIN -----

    /**
     * Return the plugin
     * @return plugin
     */
    @NotNull
    public final Bedwars getPlugin() {
        return this.plugin;
    }

    // ----- TASKS -----

    /**
     * Returns the {@link TaskScheduler} that can be used to schedule repeating or one time tasks inside this executable.
     * @return task scheduler
     */
    @NotNull
    public final TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    /**
     * Should be called by a bukkit task to execute the executables tasks.
     * @return true if executed successfully. False if it should be stopped by the plugin.
     */
    public final boolean tick() {

        try {

            if (!this.shouldExecute()) {
                return false;
            }

            this.taskScheduler.tick();
            return true;

        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Exception in executable " + this + ": ", e);
        }

        return false;
    }

    /**
     * The tasks will be executed as long as this method returns true.
     * If it returns false, no tasks will be executed and the plugin is told that the executable should be removed.
     * @return true if the tasks should be executed
     * @apiNote override only, this method always returns true in the base class
     */
    @ApiStatus.OverrideOnly
    public boolean shouldExecute() {
        return true;
    }

    // ----- LISTENERS -----

    /**
     * Returns a list containing all listeners of this executable.
     * @return list of listeners
     */
    @NotNull
    public final List<Listener> getListeners() {
        return List.copyOf(this.listeners);
    }

    /**
     * Returns a list containing all listeners of this executable.
     * @return list of listeners
     */
    @NotNull
    public final List<ManagedListener> getManagedListeners() {
        return List.copyOf(this.listeners);
    }

    /**
     * Registers the specified listener.
     * @param listener listener
     * @param instant register the listener instantly
     */
    public final void registerListener(@NotNull ManagedListener listener, boolean instant) {
        for (ManagedListener l : this.listeners) if (l == listener) return;
        this.listeners.add(listener);
        if (instant) this.plugin.listenerManager().manageListeners();
    }

    /**
     * Registers the specific listener.
     * @param listener listener
     */
    public final void registerListener(@NotNull ManagedListener listener) {
        this.registerListener(listener, false);
    }

    /**
     * Unregisters the specified listener.
     * @param listener listener
     * @param instant unregister the listener instantly
     */
    public final void unregisterListener(@NotNull ManagedListener listener, boolean instant) {
        this.listeners.remove(listener);
        if (instant) this.plugin.listenerManager().manageListeners();
    }

    /**
     * Unregisters the specific listener.
     * @param listener unregisters the listener
     */
    public final void unregisterListener(@NotNull ManagedListener listener) {
        this.unregisterListener(listener, false);
    }

    /**
     * Checks if the specified listener is registered.
     * @param listener listener
     * @return listener registered
     */
    public final boolean isListenerRegistered(@NotNull ManagedListener listener) {
        for (ManagedListener l : this.listeners) if (l == listener) return true;
        return false;
    }

    /**
     * Removes all listeners marked as to be removed.
     */
    public final void cleanupListeners() {
        for (ManagedListener listener : List.copyOf(this.listeners)) {
            if (listener.toBeRemoved()) this.listeners.remove(listener);
        }
    }

    // ----- PLAYERS -----

    /**
     * Returns true if the player with the specified uuid is ingame.
     * @param playerId player uuid
     * @return player ingame
     */
    public abstract boolean isPlayerIngame(@Nullable UUID playerId);

    /**
     * Returns true if the specified player is ingame.
     * @param player player
     * @return player ingame
     */
    public final boolean isPlayerIngame(@Nullable OfflinePlayer player) {
        if (player == null) return false;
        return this.isPlayerIngame(player.getUniqueId());
    }

    // ----- HASH AND EQUALS -----

    /**
     * Prevents overriding hashcode to prevent double games in the HashSet in the plugin.
     * @return identity hash code
     */
    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Prevents overriding equals.
     * @param o other
     * @return equals
     */
    @Override
    public final boolean equals(Object o) {
        return this == o;
    }

}
