package net.jandie1505.bedwars.game.game.commands.teams;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.GameTeamsSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameTeamPlayersSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeamPlayersSubcommand(@NotNull final Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>You need to specify a team!");
            return true;
        }

        BedwarsTeam team = GameTeamsSubcommand.getTeamFromUserInput(this.game, args[0]);
        if (team == null) {
            sender.sendRichMessage("<red>Team not found!");
            return true;
        }

        Set<UUID> players = team.getMemberUUIDs();
        if (players.isEmpty()) {
            sender.sendRichMessage("<red>The specified team has no members!");
            return true;
        }

        Component out = Component.empty()
                .append(Component.text("Team members: ", NamedTextColor.GOLD)).appendNewline();

        Iterator<UUID> i = players.iterator();
        while (i.hasNext()) {
            UUID playerId = i.next();

            @Nullable Player player = Bukkit.getPlayer(playerId);

            out = out.append(Component.text(" -", NamedTextColor.YELLOW)).appendSpace();

            if (player != null) {
                out = out.append(Component.text(player.getName(), team.getChatColor())).appendSpace();
            } else {
                out = out.append(Component.text("---offline---", team.getChatColor())).appendSpace();
            }

            out = out.append(Component.text(playerId.toString(), NamedTextColor.YELLOW));

            if (i.hasNext()) out = out.appendNewline();
        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return GameTeamsSubcommand.completeTeamIds(this.game.getTeams().size());
        return List.of();
    }
}
