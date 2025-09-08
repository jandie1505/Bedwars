package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamInfoSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamPlayersSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsListSubcommand;
import net.jandie1505.bedwars.game.game.commands.teams.GameTeamsValueSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        this.addSubcommand("players", SubcommandEntry.of(new GameTeamPlayersSubcommand(game)));
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

    /**
     * Gets a team from an input string (command).
     * @param game game
     * @param input input string
     * @return bedwars team if valid and found, else null
     */
    public static @Nullable BedwarsTeam getTeamFromUserInput(@NotNull Game game, @NotNull String input) {

        int teamId;
        try {
            teamId = Integer.parseInt(input);
        } catch (IllegalArgumentException e) {
            return null;
        }

        BedwarsTeam team = game.getTeam(teamId);
        if (team == null) {
            return null;
        }

        return team;
    }

}
