package net.jandie1505.bedwars.game.game.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameChatListener implements ManagedListener {
    @NotNull private final Game game;

    public GameChatListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- JOIN/LEAVE -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!this.game.isPlayerIngame(event.getPlayer())) {
            event.joinMessage(null);
            return;
        }

        event.joinMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("reconnected", NamedTextColor.YELLOW))
        );

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!this.game.isPlayerIngame(event.getPlayer())) {
            event.quitMessage(null);
            return;
        }

        event.quitMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("disconnected", NamedTextColor.YELLOW))
        );

    }

    // ----- CHAT -----

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.viewers().clear();
        event.viewers().add(this.getGame().getPlugin().getServer().getConsoleSender());

        PlayerData playerData = this.getGame().getPlayerData(event.getPlayer());

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (playerData != null) {
            // INGAME CHAT

            // Chat type (global / team)
            if (message.startsWith("@all ") || message.startsWith("@everyone ") || message.startsWith("@shout ") || message.startsWith("@global ")) {
                // GLOBAL INGAME CHAT

                // Check if global chat is enabled
                /*
                if (!this.getGame().getConfig().optBoolean(ConquestConfigKeys.GLOBAL_CHAT, false)) {
                    event.getPlayer().sendMessage(Component.text("Global chat is disabled", NamedTextColor.RED));
                    event.setCancelled(true);
                    return;
                }

                 */

                // Remove @all string
                if (message.startsWith("@everyone ")) {
                    message = message.substring(10);
                } else if (message.startsWith("@shout ")) {
                    message = message.substring(7);
                } else if (message.startsWith("@global ")) {
                    message = message.substring(8);
                } else if (message.startsWith("@all ")) {
                    message = message.substring(5);
                }

                // Viewers
                event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers());

                // Format
                event.renderer((source, sourceDisplayName, msg, viewer) -> {

                    TeamNameAndColor nameAndColor = this.getTeamNameAndColor(playerData);

                    return Component.empty()
                            .append(Component.text("[").color(NamedTextColor.GRAY))
                            .append(Component.text("Global").color(NamedTextColor.GOLD))
                            .append(Component.text("]").color(NamedTextColor.GRAY))
                            .appendSpace()
                            .append(Component.text("[").color(NamedTextColor.GRAY))
                            .append(nameAndColor.name())
                            .append(Component.text("]").color(NamedTextColor.GRAY))
                            .appendSpace()
                            .append(event.getPlayer().displayName()).color(nameAndColor.color())
                            .append(Component.text(": ").color(NamedTextColor.GRAY))
                            .append(Component.empty().append(msg).color(NamedTextColor.GRAY));
                });

            } else {
                // TEAM CHAT

                // Viewers
                event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                        .filter(player -> {
                            PlayerData otherPlayerData = this.getGame().getPlayerData(player);
                            if (otherPlayerData == null) return false;
                            return otherPlayerData.getTeam() == playerData.getTeam();
                        })
                        .toList()
                );

                TeamNameAndColor nameAndColor = this.getTeamNameAndColor(playerData);

                Component displayName = Component.empty()
                        .append(Component.text("[").color(NamedTextColor.GRAY))
                        .append(nameAndColor.name())
                        .append(Component.text("]").color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(event.getPlayer().displayName().color(nameAndColor.color()))
                        .append(Component.text(": ").color(NamedTextColor.GRAY));

                // Format
                event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> Component.empty()
                        .append(displayName)
                        .append(Component.empty().append(msg).color(NamedTextColor.GRAY)))
                );

            }

        } else {
            // SPECTATOR CHAT

            // Viewers
            event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                    .filter(player -> this.getGame().getPlayerData(player) == null)
                    .toList()
            );

            // Format
            event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> Component.empty()
                    .append(Component.text("[Spectator] ").color(NamedTextColor.GRAY))
                    .append(sourceDisplayName.color(NamedTextColor.GRAY))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.empty().append(msg).color(NamedTextColor.GRAY)))
            );

        }

        // BYPASSING PLAYERS

        for (Player player : this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                .filter(player -> this.getGame().getPlugin().isPlayerBypassing(player.getUniqueId()))
                .toList()
        ) {
            event.viewers().add(player);
        }

        // SET MESSAGE

        Component builtMessage;
        if (Permissions.hasPermission(event.getPlayer(), Permissions.CHAT_FORMATTING)) {
            builtMessage = MiniMessage.miniMessage().deserialize(message);
        } else {
            builtMessage = Component.text(message);
        }

        event.message(builtMessage);
    }

    // ----- UTILITIES -----

    private @NotNull TeamNameAndColor getTeamNameAndColor(@NotNull PlayerData playerData) {
        return TeamNameAndColor.fromTeam(this.game.getTeam(playerData.getTeam()));
    }

    private record TeamNameAndColor(@NotNull Component name, NamedTextColor color) {

        public static @NotNull TeamNameAndColor fromTeam(@Nullable BedwarsTeam team) {
            if (team == null) return new TeamNameAndColor(Component.text("???"), NamedTextColor.GRAY);
            NamedTextColor color = ChatCompatibilityUtils.getTextColorFromChatColor(team.getData().chatColor());
            return new TeamNameAndColor(Component.text(team.getData().name(), color), color);
        }

    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
