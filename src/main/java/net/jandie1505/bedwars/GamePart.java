package net.jandie1505.bedwars;

public interface GamePart {
    GameStatus tick();
    GamePart getNextStatus();
}
