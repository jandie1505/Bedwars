package net.jandie1505.bedwars.commands.game.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.commands.DataStorageEditorCommand;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.base.GamePart;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameConfigSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public GameConfigSubcommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        GamePart game = this.plugin.getGame();
        if (game == null) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        return DataStorageEditorCommand.onCommand(game.getConfig(), sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();

        GamePart game = this.plugin.getGame();
        if (game == null) return List.of();

        return DataStorageEditorCommand.onTabComplete(game.getConfig(), sender, args);
    }

}
