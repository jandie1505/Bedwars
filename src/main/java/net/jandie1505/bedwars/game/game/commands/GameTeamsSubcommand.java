package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamInfoSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsListSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsValueSubcommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameTeamsSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameTeamsSubcommand(@NotNull Game game) {
        super(game.getPlugin());
        this.game = game;

        this.addSubcommand("list", SubcommandEntry.of(new GameTeamsListSubcommand(game)));
        this.addSubcommand("value", SubcommandEntry.of(new GameTeamsValueSubcommand(game)));
        this.addSubcommand("info", SubcommandEntry.of(new GameTeamInfoSubcommand(game)));
    }

    /**
     * Helper method for creating a team id completer.
     * @param size team id list size
     * @return list of all team ids
     */
    public static List<String> completeTeamIds(int size) {
        List<String> completions = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            completions.add(String.valueOf(i));
        }

        return completions;
    }

}
