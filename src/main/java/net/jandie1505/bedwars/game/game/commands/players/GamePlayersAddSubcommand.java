package net.jandie1505.bedwars.game.game.commands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GamePlayersAddSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GamePlayersAddSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>Usage: /bedwars game players add <player> <team>");
            return true;
        }

        if (args.length < 2) {
            sender.sendRichMessage("<red>You need to specify a team");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerId == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        if (this.game.isPlayerIngame(playerId)) {
            sender.sendRichMessage("<red>The player is already ingame");
            return true;
        }

        @Nullable Integer teamId;
        try {
            teamId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            teamId = null;
        }

        if (teamId == null) {

            String name = "";
            for (int i = 1; i < args.length; i++) {
                name += args[i] + (i == args.length - 1 ? "" : " ");
            }

            @Nullable BedwarsTeam team = null;
            for (BedwarsTeam t : this.game.getTeams()) {

                if (t.getData().name().equalsIgnoreCase(name)) {
                    team = t;
                    break;
                }

            }

            if (team == null) {
                sender.sendRichMessage("<red>Team not found");
                return true;
            }

            teamId = team.getId();
        }

        this.game.addPlayer(playerId, teamId);
        sender.sendRichMessage("<green>Player added successfully");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().filter(player -> !this.game.isPlayerIngame(player)).map(Player::getName).toList();
        }

        if (args.length == 2) {
            return this.game.getTeams().stream().map(team -> team.getData().name()).toList();
        }

        return List.of();
    }

}
