package net.jandie1505.bedwars.game.game.commands.teams;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.GameTeamsSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GameTeamInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeamInfoSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>You need to specify a team!");
            return true;
        }

        BedwarsTeam team = GameTeamsSubcommand.getTeamFromUserInput(this.game, args[0]);
        if (team == null) {
            sender.sendRichMessage("<red>Team not found!");
            return true;
        }

        Location baseCenter = team.getBaseCenter();

        Component out = Component.empty()
                .append(Component.text("Team Info", NamedTextColor.GOLD)).appendNewline()
                .append(Component.text("Team ID: " + team.getId(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text("Name: ", NamedTextColor.YELLOW)).append(team.getFormattedName()).appendNewline()
                .append(Component.text("Color: A" + team.getColor().getAlpha() + " R" + team.getColor().getRed() + " G" + team.getColor().getGreen() + " B" + team.getColor().getBlue(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text("Chat Color: ", NamedTextColor.YELLOW)).append(Component.text(team.getChatColor().toString(), team.getChatColor())).appendNewline()
                .append(Component.text("Base center: " + baseCenter.getX() + " " + baseCenter.getY() + " " + baseCenter.getZ(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text("Base radius: " + team.getBaseRadius(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text("Alive: ", NamedTextColor.YELLOW)).append(Component.text(team.isAlive(), team.isAlive() ? NamedTextColor.GREEN : NamedTextColor.RED)).appendNewline()
                .append(Component.text("Has beds: ", NamedTextColor.YELLOW)).append(Component.text(team.getAvailableBedsCount(), team.getAvailableBedsCount() > 0 ? NamedTextColor.GREEN : NamedTextColor.RED)).appendNewline()
                .append(Component.text("Beds disabled: ", NamedTextColor.YELLOW)).append(Component.text(team.isBedDisabled(), team.isBedDisabled() ? NamedTextColor.RED : NamedTextColor.GREEN)).appendNewline()
                .append(Component.text("Players: " + team.getMemberUUIDs(), NamedTextColor.YELLOW));

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return GameTeamsSubcommand.completeTeamIds(this.game.getTeams().size());
        return List.of();
    }

}
