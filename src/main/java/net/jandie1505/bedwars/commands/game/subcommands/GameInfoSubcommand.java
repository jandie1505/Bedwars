package net.jandie1505.bedwars.commands.game.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.base.GamePart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public GameInfoSubcommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        GamePart game = this.plugin.getGame();

        Component out = Component.empty();
        if (game != null) {
            out = out.append(Component.text("Game Information:", NamedTextColor.GRAY)).appendNewline()
                    .append(Component.text(" - Status: ", NamedTextColor.GRAY)).append(Component.text("running", NamedTextColor.GREEN)).appendNewline()
                    .append(Component.text(" - Type: " + game.getClass().getSimpleName(), NamedTextColor.GRAY)).appendNewline()
                    .append(Component.text(" - Paused: ")).append(Component.text(this.plugin.isPaused() ? "true" : "false", this.plugin.isPaused() ? NamedTextColor.GREEN : NamedTextColor.RED));
        } else {
            out = out.append(Component.text("Game Information:")).appendNewline()
                    .append(Component.text(" - Status: ", NamedTextColor.GRAY)).append(Component.text("stopped", NamedTextColor.RED)).appendNewline()
                    .append(Component.text(" - Paused: ", NamedTextColor.GRAY)).append(Component.text(this.plugin.isPaused() ? "true" : "false", this.plugin.isPaused() ? NamedTextColor.GREEN : NamedTextColor.RED));
        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
