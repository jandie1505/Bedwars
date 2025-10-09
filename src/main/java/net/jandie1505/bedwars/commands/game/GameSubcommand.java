package net.jandie1505.bedwars.commands.game;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.commands.game.subcommands.*;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.base.GamePart;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GameSubcommand extends SubcommandCommand {

    public GameSubcommand(@NotNull Bedwars plugin) {
        super(
                plugin,
                sender -> Permissions.hasPermission(sender, Permissions.ADMIN),
                () -> {
                    GamePart game = plugin.getGame();
                    if (game == null) return Map.of();
                    return game.getDynamicSubcommands();
                }
        );

        this.addSubcommand("stop", SubcommandEntry.of(new GameStopSubcommand(plugin)));
        this.addSubcommand("info", SubcommandEntry.of(new GameInfoSubcommand(plugin)));
        this.addSubcommand("pause", SubcommandEntry.of(new GamePauseSubcommand(plugin)));
        this.addSubcommand("tasks", SubcommandEntry.of(new GameTasksSubcommand(plugin)));
        this.addSubcommand("config", SubcommandEntry.of(new GameConfigSubcommand(plugin)));
        this.addSubcommand("start-lobby", SubcommandEntry.of(new GameStartLobbySubcommand(plugin)));
    }

}
