package net.jandie1505.bedwars.game.game.commands.timeactions;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class GameTimeActionsListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTimeActionsListSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        List<TimeAction> timeActions = this.game.getTimeActions();
        if (timeActions.isEmpty()) {
            sender.sendRichMessage("<gold>There are no time actions");
            return true;
        }

        Component msg = Component.empty()
                .append(Component.text("Time Actions:", NamedTextColor.GOLD));

        for (TimeAction timeAction : timeActions) {

            msg = msg.appendNewline()
                    .append(Component.text(" - " + timeAction.getId() + ":", NamedTextColor.YELLOW)).appendSpace()
                    .append(Component.text(timeAction.getClass().getSimpleName(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(timeAction.getTime(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(timeAction.isCompleted(), NamedTextColor.DARK_AQUA));

        }

        sender.sendMessage(msg);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
