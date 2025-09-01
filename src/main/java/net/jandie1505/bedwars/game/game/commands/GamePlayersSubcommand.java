package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.game.game.commands.players.GamePlayersInfoSubcommand;
import net.jandie1505.bedwars.game.game.commands.players.GamePlayersListSubcommand;
import net.jandie1505.bedwars.game.game.commands.players.GamePlayersRemoveSubcommand;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import org.jetbrains.annotations.NotNull;

public class GamePlayersSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GamePlayersSubcommand(@NotNull Game game) {
        super(game.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));
        this.game = game;

        this.addSubcommand("list", SubcommandEntry.of(new GamePlayersListSubcommand(game)));
        this.addSubcommand("info", SubcommandEntry.of(new GamePlayersInfoSubcommand(game)));
        this.addSubcommand("remove", SubcommandEntry.of(new GamePlayersRemoveSubcommand(game)));
    }

}
