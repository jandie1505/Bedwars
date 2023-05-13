package net.jandie1505.bedwars;

import java.util.List;
import java.util.UUID;

public interface GamePart {
    GameStatus tick();
    GamePart getNextStatus();
}
