package net.jandie1505.bedwars.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.commands.game.GameSubcommand;
import net.jandie1505.bedwars.commands.worlds.WorldsSubcommand;
import net.jandie1505.bedwars.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BedwarsCommand extends SubcommandCommand {
    @NotNull private final Bedwars plugin;

    public BedwarsCommand(@NotNull Bedwars plugin) {
        super(plugin);
        this.plugin = plugin;

        this.addSubcommand("game", SubcommandEntry.of(new GameSubcommand(plugin), sender -> Permissions.hasPermission(sender, Permissions.ADMIN)));
        this.addSubcommand("worlds", SubcommandEntry.of(new WorldsSubcommand(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN)));

        this.addSubcommand("old-command", SubcommandEntry.of(new BedwarsCommandOld(plugin), sender -> Permissions.hasPermission(sender, Permissions.ADMIN))); // TODO: Remove
    }

    @Override
    protected void onExecutionWithoutSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label) {
        sender.sendRichMessage("<gold>Bedwars " + this.plugin.getPluginMeta().getVersion() + " by " + this.plugin.getPluginMeta().getAuthors());
        if (Permissions.hasPermission(sender, Permissions.ADMIN)) sender.sendRichMessage("<gold>Use <aqua>/bedwars<gold> help view a list of commands.");
    }

    @Override
    protected void onExecutionWithUnknownSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        sender.sendRichMessage("<red>Unknown subcommand. Use <aqua>/bedwars help<red> to get a list of available subcommands.");
    }

}
