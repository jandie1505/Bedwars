package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameTeleportToMapSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeleportToMapSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        Player player;

        if (args.length > 0) {
            player = PlayerUtils.getPlayerFromString(args[0]);
        } else {
            if (sender instanceof Player p) {
                player = p;
            } else {
                player = null;
            }
        }

        if (player == null) {
            sender.sendRichMessage("<red>Player not found.");
            return true;
        }

        Location spawnLocation = this.game.getWorld().getSpawnLocation().clone();
        player.teleport(spawnLocation);

        if (player == sender) {
            player.sendRichMessage("<green>You have been teleported to map.");
        } else {
            player.sendRichMessage("<green>The player has been teleported to map.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();
        if (args.length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}
