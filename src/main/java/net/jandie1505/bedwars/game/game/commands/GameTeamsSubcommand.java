package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsListSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsValueSubcommand;
import org.jetbrains.annotations.NotNull;

public class GameTeamsSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameTeamsSubcommand(@NotNull Game game) {
        super(game.getPlugin());
        this.game = game;

        this.addSubcommand("list", SubcommandEntry.of(new GameTeamsListSubcommand(game)));
        this.addSubcommand("value", SubcommandEntry.of(new GameTeamsValueSubcommand(game)));
    }
}
