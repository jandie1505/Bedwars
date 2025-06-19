package net.jandie1505.bedwars.game.utils;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.base.GamePart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class LobbyChatListener implements ManagedListener {
    @NotNull private final GamePart game;

    public LobbyChatListener(@NotNull GamePart game) {
        this.game = game;
    }

    // ----- LISTENER -----

    @EventHandler
    public void onPlayerChat(@NotNull AsyncChatEvent event) {

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (event.getPlayer().hasPermission(Permissions.CHAT_FORMATTING)) {
            event.message(MiniMessage.miniMessage().deserialize(message));
        } else {
            event.message(Component.text(message));
        }

        if (this.game.getPlugin().getConfigManager().getConfig().optBoolean("add_prefix_suffix", false)) {

            event.renderer(ChatRenderer.viewerUnaware(((source, sourceDisplayName, msg) -> Component.empty()
                    .append(PlayerUtils.getPlayerPrefix(source).append(sourceDisplayName)).color(NamedTextColor.GRAY)
                    .append(Component.text(": ")).color(NamedTextColor.GRAY)
                    .append(Component.empty().append(msg).color(NamedTextColor.GRAY))))
            );

        } else {

            event.renderer(ChatRenderer.viewerUnaware(((source, sourceDisplayName, msg) -> Component.empty()
                    .append(Component.empty().append(sourceDisplayName).color(NamedTextColor.GRAY))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.empty().append(msg).color(NamedTextColor.GRAY))))
            );

        }

    }

    // ----- OTHER -----

    public @NotNull GamePart getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
