package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.generators.GameGeneratorsListSubcommand;
import org.jetbrains.annotations.NotNull;

public class GameGeneratorsSubcommand extends SubcommandCommand {

    public GameGeneratorsSubcommand(@NotNull Game game) {
        super(game.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));

        this.addSubcommand("list", SubcommandEntry.of(new GameGeneratorsListSubcommand(game)));
    }

}
