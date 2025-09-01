package net.jandie1505.bedwars.commands.game.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamePauseSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public GamePauseSubcommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        switch (args.length) {
            case 0 -> sender.sendRichMessage("<gold>Pause status: " + (this.plugin.isPaused() ? "<green>paused" : "<red>not paused"));
            case 1 -> {

                boolean prevStatus = this.plugin.isPaused();

                if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("on")) {

                    this.plugin.setPaused(true);

                    if (prevStatus) {
                        sender.sendRichMessage("<green>The game is already paused.");
                    } else {
                        sender.sendRichMessage("<green>The game has been paused.");
                    }

                } else {

                    this.plugin.setPaused(false);

                    if (prevStatus) {
                        sender.sendRichMessage("<green>The game is no longer paused.");
                    } else {
                        sender.sendMessage("<green>The game has already been not paused.");
                    }

                }

            }
            default -> sender.sendRichMessage("<red>Usage: /bedwars game pause [true|false]");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return List.of("true", "false");
        return List.of();
    }

}
