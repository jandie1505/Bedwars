package net.jandie1505.bedwars.game.game.commands.teams;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameTeamsListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeamsListSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        List<BedwarsTeam> teams = this.game.getTeams();

        if (teams.isEmpty()) {
            sender.sendRichMessage("<red>There are no teams. This is most likely caused by an error. If the game does not stop the next seconds, please stop the game.");
            return true;
        }

        Component out = Component.empty()
                .append(Component.text("Available Teams:", NamedTextColor.GOLD))
                .appendNewline();

        for (int i = 0; i < teams.size(); i++) {
            BedwarsTeam team = teams.get(i);

            out = out.append(Component.empty().append(
                    Component.text(i + ": ", NamedTextColor.YELLOW)
                            .append(Component.text(team.getData().name(), ChatCompatibilityUtils.getTextColorFromChatColor(team.getData().chatColor()))).appendSpace()
                            .append(Component.text(team.isAlive(), team.isAlive() ? NamedTextColor.GREEN : NamedTextColor.RED))
                    ).hoverEvent(HoverEvent.showText(Component.empty()
                            .append(Component.text("Team ID: " + team.getId(), NamedTextColor.YELLOW)).appendNewline()
                            .append(Component.text("Name: ", NamedTextColor.YELLOW)).append(Component.text(team.getData().name(), ChatCompatibilityUtils.getTextColorFromChatColor(team.getData().chatColor()))).appendNewline()
                            .append(Component.text("Color: A" + team.getData().color().getAlpha() + " R" + team.getData().color().getRed() + " G" + team.getData().color().getGreen() + " B" + team.getData().color().getBlue(), NamedTextColor.YELLOW)).appendNewline()
                            .append(Component.text("Chat Color: " + team.getData().chatColor().toString(), ChatCompatibilityUtils.getTextColorFromChatColor(team.getData().chatColor()))).appendNewline()
                            .append(Component.text("Alive: ", NamedTextColor.YELLOW)).append(Component.text(team.isAlive(), team.isAlive() ? NamedTextColor.GREEN : NamedTextColor.RED)).appendNewline()
                            .append(Component.text("Has beds: ", NamedTextColor.YELLOW)).append(Component.text(team.hasBed(), team.hasBed() > 0 ? NamedTextColor.GREEN : NamedTextColor.RED))
                    ))
            );

            if (i < teams.size() - 1) out = out.appendNewline();
        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
