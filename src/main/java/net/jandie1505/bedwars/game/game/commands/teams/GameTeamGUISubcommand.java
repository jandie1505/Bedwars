package net.jandie1505.bedwars.game.game.commands.teams;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameTeamGUISubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeamGUISubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        OptionParser.Result args = OptionParser.parse(oldArgs);

        try {

            // GET PLAYER

            Player player;
            if (args.args().length > 0) {
                player = PlayerUtils.getPlayerFromString(args.args()[0]);
            } else {
                if (sender instanceof Player p) {
                    player = p;
                } else {
                    sender.sendRichMessage("<red>You need to specify or be a player.");
                    return true;
                }
            }

            if (player == null) {
                sender.sendRichMessage("<red>Player not found.");
                return true;
            }

            // TEAM ID

            int teamId = -1;

            @Nullable PlayerData playerData = this.game.getPlayerData(player);

            if (playerData != null) {
                teamId = playerData.getTeam();
            }

            @Nullable String teamIdString = args.options().get("team");
            if (teamIdString != null) {
                try {
                    teamId = Integer.parseInt(teamIdString);
                } catch (IllegalArgumentException e) {
                    sender.sendRichMessage("<red>Invalid team ID: " + e.getMessage());
                }
            }

            // TEAM

            BedwarsTeam team = this.game.getTeam(teamId);
            if (team == null) {
                sender.sendRichMessage("<red>Team not found.");
                return true;
            }

            // FULL ACCESS

            boolean fullAccess = playerData != null && team.getId() == playerData.getTeam();

            @Nullable String fullAccessString = args.options().get("full-access");
            if (fullAccessString != null) {
                fullAccess = Boolean.parseBoolean(fullAccessString);
            }

            // PAGE

            int page = 0;

            @Nullable String pageString = args.options().get("page");
            if (pageString != null) {
                page = Integer.parseInt(pageString);
            }

            // OPEN INVENTORY

            player.openInventory(this.game.getTeamGUI().getInventory(player, page, team, args.hasOption("free-mode"), fullAccess));
            if (!args.hasOption("silent")) sender.sendRichMessage("<green>Team GUI of team " + team.getId() + " has been opened for <aqua>" + player.getName() + "<green>.");

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument: " + e.getMessage());
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender cmdSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        return OptionParser.complete(
                cmdSender,
                OptionParser.parse(oldArgs),
                (sender, args) -> {
                    if (args.args().length == 1) return this.game.getOnlinePlayers().stream().map(Player::getName).toList();
                    return List.of();
                },
                Set.of("silent", "free-mode"),
                Map.of(
                        "team", ((sender, args) -> List.of("true", "false")),
                        "full-access", ((sender, args) -> List.of("true", "false")),
                        "page", ((sender, args) -> List.of("0", "1", "2"))
                )
        );
    }

}
