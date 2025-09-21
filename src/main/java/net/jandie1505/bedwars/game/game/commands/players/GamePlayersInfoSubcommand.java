package net.jandie1505.bedwars.game.game.commands.players;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GamePlayersInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GamePlayersInfoSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        if (args.length < 1) {
            sender.sendRichMessage("<red>Usage: /bedwars game players info <player>");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerId == null)  {
            sender.sendRichMessage("<red>Player not found!");
            return true;
        }

        PlayerData playerData = this.game.getPlayerData(playerId);
        if (playerData == null) {
            sender.sendRichMessage("<red>Player not ingame!");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);

        Component out = Component.empty()
                .append(Component.text("Player Information:", NamedTextColor.GOLD)).appendNewline()
                .append(Component.text(" - UUID: " + playerId, NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text(" - Name: " + player.getName(), NamedTextColor.YELLOW)).appendNewline();

        @Nullable BedwarsTeam team = this.game.getTeam(playerData.getTeam());
        if (team != null) {
            out = out.append(Component.text(" - Team: ", NamedTextColor.YELLOW))
                    .append(Component.text(playerData.getTeam(), team.getChatColor())).appendSpace()
                    .append(team.getFormattedName()).appendNewline();
        } else {
            out = out.append(Component.text(" - Team: " + playerData.getTeam(), NamedTextColor.YELLOW)).appendNewline();
        }

        out = out.append(Component.text(" - Alive: ", NamedTextColor.YELLOW)).append(Component.text(playerData.isAlive() ? "true" : "false", playerData.isAlive() ? NamedTextColor.GREEN : NamedTextColor.RED)).appendNewline()
                .append(Component.text(" - Respawn Countdown: ", NamedTextColor.YELLOW)).append(Component.text(playerData.getRespawnCountdown(), playerData.isAlive() ? NamedTextColor.GREEN : NamedTextColor.RED)).appendNewline()
                .append(Component.text(" - Upgrades: ", NamedTextColor.YELLOW)).append(Component.text(playerData.getUpgrades().toString(), NamedTextColor.GRAY)).appendNewline()
                .append(Component.text(" - Kills: " + playerData.getKills(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text(" - Deaths: " + playerData.getDeaths(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text(" - Beds broken: " + playerData.getBedsBroken(), NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text(" - Reward points: " + playerData.getRewardPoints(), NamedTextColor.YELLOW));

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();

        if (args.length == 1) return this.game.getRegisteredPlayers().stream()
                .map(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    return player != null ? player.getName() : uuid.toString();
                })
                .toList();

        return List.of();
    }

}
