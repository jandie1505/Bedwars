package net.jandie1505.bedwars.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.commands.DataStorageEditorCommand;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public ConfigSubcommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.admin(sender)) {
            return true;
        }

        return DataStorageEditorCommand.onCommand(this.plugin.getConfig(), sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.admin(sender)) {
            return List.of();
        }

        return DataStorageEditorCommand.onTabComplete(this.plugin.getConfig(), sender, args);
    }

    public @NotNull Bedwars getPlugin() {
        return plugin;
    }

}
