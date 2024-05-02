package net.jandie1505.bedwars;

import org.bukkit.event.Listener;

public interface GameListener extends Listener {

    boolean toBeRemoved();
    GamePart getGame();

}
