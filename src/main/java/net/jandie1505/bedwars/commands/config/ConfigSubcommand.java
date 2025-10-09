package net.jandie1505.bedwars.commands.config;

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
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;
        return DataStorageEditorCommand.onCommand(this.plugin.config(), sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();
        return DataStorageEditorCommand.onTabComplete(this.plugin.config(), sender, args);
    }

}
