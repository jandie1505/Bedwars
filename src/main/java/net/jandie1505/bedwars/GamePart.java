package net.jandie1505.bedwars;

import net.chaossquad.mclib.executable.CoreExecutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GamePart extends CoreExecutable {
    @NotNull private final Bedwars plugin;

    public GamePart(Bedwars plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
    }

    public final @NotNull Bedwars getPlugin() {
        return this.plugin;
    }

    public abstract @Nullable GamePart getNextStatus();

}
