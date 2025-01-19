package net.jandie1505.bedwars.base;

import org.bukkit.event.Listener;

public interface ManagedListener extends Listener {
    boolean toBeRemoved();
}
