package net.jandie1505.bedwars.game.game.commands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GamePlayersRemoveSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GamePlayersRemoveSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>Usage: /bedwars game players remove <player>");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerId == null) {
            sender.sendRichMessage("<red>Player not found!");
            return true;
        }

        boolean ingameBefore = this.game.isPlayerIngame(playerId);

        this.game.removePlayer(playerId);

        if (ingameBefore) {
            sender.sendRichMessage("<green>Player has been removed.");
        } else {
            sender.sendRichMessage("<red>Player was already not ingame.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();

        if (args.length == 1) return this.game.getRegisteredPlayers().stream()
                .map(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    return player != null ? player.getName() : uuid.toString();
                })
                .toList();

        return List.of();
    }

}
