package net.jandie1505.bedwars.game.game.commands.players;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
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

public class GamePlayersListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GamePlayersListSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        Set<Map.Entry<UUID, PlayerData>> players = this.game.getPlayerDataMap().entrySet();

        if (players.isEmpty()) {
            sender.sendRichMessage("<gold>No players ingame.");
            return true;
        }

        Component out = Component.empty().append(Component.text("Ingame players:", NamedTextColor.GOLD)).appendNewline();

        Iterator<Map.Entry<UUID, PlayerData>> i = players.iterator();
        while (i.hasNext()) {
            Map.Entry<UUID, PlayerData> entry = i.next();
            UUID playerId = entry.getKey();
            PlayerData playerData = entry.getValue();

            @Nullable Player player = Bukkit.getPlayer(playerId);
            String name = player != null ? player.getName() : playerId.toString();

            out = out.append(
                    Component.text(name, NamedTextColor.YELLOW)
                            .hoverEvent(HoverEvent.showText(Component.empty()
                                    .append(Component.text("Player Information:", NamedTextColor.GOLD).appendNewline()
                                            .append(Component.text("UUID: " + playerId, NamedTextColor.YELLOW)).appendNewline()
                                            .append(Component.text("Team: " + playerData.getTeam(), NamedTextColor.YELLOW)).appendNewline()
                                            .append(Component.text("Alive: " + playerData.isAlive(), NamedTextColor.YELLOW))
                            )))
            );

            if (i.hasNext()) {
                out = out.append(Component.text(", ", NamedTextColor.YELLOW));
            }

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
