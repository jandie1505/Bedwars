package net.jandie1505.bedwars.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StopSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public StopSubcommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.admin(sender)) {
            return true;
        }

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("all")) {
                this.plugin.stopAllGames();
                sender.sendMessage(Component.text("Stopped all games", NamedTextColor.GREEN));
            } else {

                try {
                    int id = Integer.parseInt(args[0]);
                    this.plugin.stopGame(id);
                    sender.sendMessage(Component.text("Game stopped", NamedTextColor.GREEN));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("You need to specify a valid int value or \"all\"", NamedTextColor.RED));
                }

            }

        } else {
            sender.sendMessage(Component.text("Usage: " + label + " stop (<gameId: int>|all)", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("all");
            list.addAll(this.plugin.getRunningGameInstances().keySet().stream().map(String::valueOf).toList());
        }

        return list;
    }

    public @NotNull Bedwars getPlugin() {
        return plugin;
    }
}
