package net.jandie1505.bedwars.game.base;

import net.chaossquad.mclib.executable.CoreExecutable;
import net.jandie1505.bedwars.Bedwars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GamePart extends CoreExecutable {
    @NotNull private final Bedwars plugin;

    public GamePart(Bedwars plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
    }

    // ----- NEXT STATUS -----

    public abstract @Nullable GamePart getNextStatus();

    // ----- OTHER

    public final @NotNull Bedwars getPlugin() {
        return this.plugin;
    }

}
