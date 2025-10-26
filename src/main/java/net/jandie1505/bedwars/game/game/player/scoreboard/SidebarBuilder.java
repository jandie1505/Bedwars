package net.jandie1505.bedwars.game.game.player.scoreboard;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SidebarBuilder {
    @NotNull private final Game game;

    public SidebarBuilder(@NotNull Game game) {
        this.game = game;
    }

    public List<Component> buildSidebar(@NotNull Player player) {
        @Nullable PlayerData playerData = this.game.getPlayerData(player);
        List<Component> sidebarDisplayStrings = new ArrayList<>();

        sidebarDisplayStrings.add(Component.empty());

        sidebarDisplayStrings.addAll(this.buildTimeActionPart());

        sidebarDisplayStrings.add(Component.empty());

        sidebarDisplayStrings.addAll(this.buildTeamOverviewPart(playerData != null ? playerData.getTeam() : -1));

        sidebarDisplayStrings.add(Component.empty());

        if (playerData != null) {
            sidebarDisplayStrings.addAll(this.buildStatsPartForIngamePlayers(playerData));
        } else {
            sidebarDisplayStrings.addAll(this.buildStatsPartForSpectators());
        }

        sidebarDisplayStrings.add(Component.empty());

        return List.copyOf(sidebarDisplayStrings);
    }

    private List<Component> buildTimeActionPart() {
        List<Component> sidebarDisplayStrings = new ArrayList<>();

        int timeActionCount = 0;
        for (TimeAction timeAction : this.game.getTimeActions()) {

            if (timeActionCount >= 2) {
                break;
            }

            if (timeAction.getScoreboardText() == null || timeAction.isCompleted()) {
                continue;
            }

            int inTime = this.game.getTime() - timeAction.getTime();

            sidebarDisplayStrings.add(Component.empty()
                    .append(timeAction.getScoreboardText()).color(NamedTextColor.WHITE)
                    .append(Component.text(" in ", NamedTextColor.WHITE))
                    .append(Component.text(Bedwars.getDurationFormat(inTime), NamedTextColor.GREEN))
            );

            timeActionCount++;
        }

        if (timeActionCount < 2) {
            sidebarDisplayStrings.add(Component.empty()
                    .append(Component.text("Game End in ", NamedTextColor.WHITE))
                    .append(Component.text(Bedwars.getDurationFormat(this.game.getTime()), NamedTextColor.GREEN))
            );
        }

        return sidebarDisplayStrings;
    }

    private List<Component> buildTeamOverviewPart(int playerTeamId) {
        List<Component> sidebarDisplayStrings = new ArrayList<>();

        for (BedwarsTeam team : this.game.getTeams()) {

            Component teamStatusIndicator;

            if (team.isAlive()) {

                int availableBeds = team.getAvailableBedsCount();
                if (availableBeds > 1) {

                    teamStatusIndicator = Component.empty()
                            .append(Component.text(team.getAvailableBedsCount(), NamedTextColor.GREEN))
                            .append(Component.text("✓", NamedTextColor.GREEN, TextDecoration.BOLD));

                } else if (availableBeds == 1) {

                    teamStatusIndicator = Component.text("✓", NamedTextColor.GREEN, TextDecoration.BOLD);

                } else {
                    teamStatusIndicator = Component.text(team.getOnlineMembers().size(), NamedTextColor.GOLD);
                }

            } else {
                teamStatusIndicator = Component.text("❌", NamedTextColor.RED);
            }

            Component youIndicator = Component.empty();
            if (playerTeamId >= 0 & playerTeamId == team.getId()) {
                youIndicator = Component.text(" (you)", NamedTextColor.GRAY);
            }

            sidebarDisplayStrings.add(Component.empty()
                    .append(team.getFormattedName())
                    .append(Component.text(": ", NamedTextColor.WHITE))
                    .append(teamStatusIndicator)
                    .append(youIndicator)
            );

        }

        return sidebarDisplayStrings;
    }

    private List<Component> buildStatsPartForIngamePlayers(@NotNull PlayerData playerData) {
        List<Component> sidebarDisplayStrings = new ArrayList<>();

        sidebarDisplayStrings.add(Component.empty().append(Component.text("Kills: ", NamedTextColor.WHITE)).append(Component.text(playerData.getKills(), NamedTextColor.GREEN)));
        sidebarDisplayStrings.add(Component.empty().append(Component.text("Beds broken: ", NamedTextColor.WHITE)).append(Component.text(playerData.getBedsBroken(), NamedTextColor.GREEN)));
        sidebarDisplayStrings.add(Component.empty().append(Component.text("Deaths: ", NamedTextColor.WHITE)).append(Component.text(playerData.getDeaths(), NamedTextColor.GREEN)));

        return sidebarDisplayStrings;
    }

    private List<Component> buildStatsPartForSpectators() {
        List<Component> sidebarDisplayStrings = new ArrayList<>();

        sidebarDisplayStrings.add(Component.text("You are", NamedTextColor.WHITE));
        sidebarDisplayStrings.add(Component.text("spectator"));

        return sidebarDisplayStrings;
    }

}
