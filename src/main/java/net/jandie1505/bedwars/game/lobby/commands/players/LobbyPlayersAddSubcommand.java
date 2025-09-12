package net.jandie1505.bedwars.game.lobby.commands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class LobbyPlayersAddSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyPlayersAddSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>Usage: /bedwars game players add <player>");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerId == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        if (this.lobby.isPlayerIngame(playerId)) {
            sender.sendRichMessage("<red>The player is already ingame");
            return true;
        }

        this.lobby.addPlayer(playerId);
        sender.sendRichMessage("<green>Player added successfully");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().filter(player -> !this.lobby.isPlayerIngame(player)).map(Player::getName).toList();
        }

        return List.of();
    }

}
