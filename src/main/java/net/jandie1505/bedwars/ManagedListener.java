package net.jandie1505.bedwars;

import net.jandie1505.bedwars.game.base.GamePart;

/**
 * @deprecated Use {@link net.chaossquad.mclib.executable.ManagedListener}
 */
@Deprecated(forRemoval = true)
public interface ManagedListener extends net.chaossquad.mclib.executable.ManagedListener {
    GamePart getGame();
}
