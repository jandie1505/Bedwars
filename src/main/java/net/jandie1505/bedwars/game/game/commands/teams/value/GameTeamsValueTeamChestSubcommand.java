package net.jandie1505.bedwars.game.game.commands.teams.value;

import net.chaossquad.mclib.MiscUtils;
import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.commands.chest.ChestEditorClearSubcommand;
import net.jandie1505.bedwars.commands.chest.ChestEditorOpenSubcommand;
import net.jandie1505.bedwars.commands.chest.ChestEditorViewSubcommand;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.GameTeamsSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameTeamsValueTeamChestSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameTeamsValueTeamChestSubcommand(@NotNull Game game) {
        super(game.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));
        this.game = game;

        this.addSubcommand("inventory", SubcommandEntry.of(new InventorySubcommand()));
        this.addSubcommand("level", SubcommandEntry.of(new LevelSubcommand()));
    }

    private class InventorySubcommand implements TabCompletingCommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a team!");
                return true;
            }

            if (args.length < 2) {
                sender.sendRichMessage("<red>Available subcommands: clear, view, open");
                return true;
            }

            BedwarsTeam team = GameTeamsSubcommand.getTeamFromUserInput(game, args[0]);
            if (team == null) {
                sender.sendRichMessage("<red>Invalid team!");
                return true;
            }

            Inventory inventory = team.getTeamChest();
            if (inventory == null) {
                sender.sendRichMessage("<red>Team has no team chest!");
                return true;
            }

            switch (args[1]) {
                case "clear" -> ChestEditorClearSubcommand.onCommand(sender, MiscUtils.shiftArgs(args, 2), inventory);
                case "view" -> ChestEditorViewSubcommand.onCommand(sender, MiscUtils.shiftArgs(args, 2), inventory);
                case "open" -> ChestEditorOpenSubcommand.onCommand(sender, MiscUtils.shiftArgs(args, 2), inventory);
                default -> sender.sendRichMessage("<red>Unknown subcommand!");
            }

            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length > 2) {

                BedwarsTeam team = GameTeamsSubcommand.getTeamFromUserInput(game, args[0]);
                if (team == null) {
                    return List.of();
                }

                Inventory inventory = team.getTeamChest();
                if (inventory == null) return List.of();

                return switch (args[1]) {
                    case "clear" -> ChestEditorClearSubcommand.onTabComplete(sender, MiscUtils.shiftArgs(args, 2));
                    case "view" -> ChestEditorViewSubcommand.onTabComplete(MiscUtils.shiftArgs(args, 2), inventory);
                    case "open" -> ChestEditorOpenSubcommand.onTabComplete(sender, MiscUtils.shiftArgs(args, 2));
                    default -> List.of();
                };
            } else if (args.length == 2) {
                return List.of("clear", "view", "open");
            } else if (args.length == 1) {
                return GameTeamsSubcommand.completeTeamIds(game.getTeams().size());
            } else {
                return List.of();
            }

        }
    }

    private class LevelSubcommand implements TabCompletingCommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a team!");
                return true;
            }

            BedwarsTeam team = GameTeamsSubcommand.getTeamFromUserInput(game, args[0]);
            if (team == null) {
                sender.sendRichMessage("<red>Invalid team!");
                return true;
            }

            try {

                if (args.length > 1) {
                    team.setTeamChestLevel(Integer.parseInt(args[1]));
                    sender.sendRichMessage("<green>Updated team chest level to " + team.getTeamChestLevel());
                } else {
                    sender.sendRichMessage("<gold>Team chest level of " + team.getName() + ": " + team.getTeamChestLevel());
                }

                return true;
            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("<red>Invalid level!");
                return true;
            }

        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (args.length == 1) return GameTeamsSubcommand.completeTeamIds(game.getTeams().size());
            if (args.length == 2) return List.of("0", "1", "2", "3", "4", "5", "6");
            return List.of();
        }
    }

}
