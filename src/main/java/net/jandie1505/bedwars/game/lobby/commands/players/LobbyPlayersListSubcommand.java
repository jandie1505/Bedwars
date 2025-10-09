package net.jandie1505.bedwars.game.lobby.commands.players;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.jandie1505.bedwars.game.lobby.LobbyPlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LobbyPlayersListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyPlayersListSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        Set<Map.Entry<UUID, LobbyPlayerData>> players = this.lobby.getPlayerDataMap().entrySet();

        if (players.isEmpty()) {
            sender.sendRichMessage("<gold>No players ingame.");
            return true;
        }

        Component out = Component.empty().append(Component.text("Ingame players:", NamedTextColor.GOLD)).appendNewline();

        Iterator<Map.Entry<UUID, LobbyPlayerData>> i = players.iterator();
        while (i.hasNext()) {
            Map.Entry<UUID, LobbyPlayerData> entry = i.next();
            UUID playerId = entry.getKey();
            LobbyPlayerData playerData = entry.getValue();

            @Nullable Player player = Bukkit.getPlayer(playerId);
            String name = player != null ? player.getName() : playerId.toString();

            out = out.append(
                    Component.text(name, NamedTextColor.YELLOW)
                            .hoverEvent(HoverEvent.showText(Component.empty()
                                    .append(Component.text("Player Information:", NamedTextColor.GOLD).appendNewline()
                                            .append(Component.text("UUID: " + playerId, NamedTextColor.YELLOW)).appendNewline()
                                            .append(Component.text("Team: " + playerData.getTeam(), NamedTextColor.YELLOW)).appendNewline()
                                            .append(Component.text("Vote: ", NamedTextColor.YELLOW)).append(Component.text((playerData.getVote() != null ? playerData.getVote() : "---"), playerData.getVote() != null ? NamedTextColor.GREEN : NamedTextColor.RED))
                            )))
            );

            if (i.hasNext()) {
                out = out.append(Component.text(", ", NamedTextColor.YELLOW));
            }

        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
