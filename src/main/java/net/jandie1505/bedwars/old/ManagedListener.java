package net.jandie1505.bedwars.old;

import org.bukkit.event.Listener;

public interface ManagedListener extends Listener {

    boolean toBeRemoved();
    GamePart getGame();

}
