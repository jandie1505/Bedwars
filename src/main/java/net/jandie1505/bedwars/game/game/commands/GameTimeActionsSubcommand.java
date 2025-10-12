package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.timeactions.GameTimeActionsListSubcommand;
import org.jetbrains.annotations.NotNull;

public class GameTimeActionsSubcommand extends SubcommandCommand {

    public GameTimeActionsSubcommand(@NotNull Game game) {
        super(game.getPlugin());

        this.addSubcommand("list", SubcommandEntry.of(new GameTimeActionsListSubcommand(game)));
    }

}
