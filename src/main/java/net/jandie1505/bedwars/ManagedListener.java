package net.jandie1505.bedwars;

import org.bukkit.event.Listener;

public interface ManagedListener extends Listener {

    boolean toBeRemoved();
    GamePart getGame();

}
