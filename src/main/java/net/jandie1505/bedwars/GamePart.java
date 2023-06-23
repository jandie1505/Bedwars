package net.jandie1505.bedwars;

import org.bukkit.World;

public interface GamePart {
    GameStatus tick();
    GamePart getNextStatus();
}
