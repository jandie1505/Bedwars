package net.jandie1505.bedwars.game.game.commands.shop;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameShopOpenSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameShopOpenSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        OptionParser.Result args = OptionParser.parse(oldArgs);

        Player player;
        if (args.args().length > 0) {
            player = PlayerUtils.getPlayerFromString(args.args()[0]);
        } else {
            if (sender instanceof Player p) {
                player = p;
            } else {
                sender.sendRichMessage("<red>You need to specify or be a player.");
                return true;
            }
        }

        if (player == null) {
            sender.sendRichMessage("<red>Player not found.");
            return true;
        }

        player.openInventory(this.game.getShopGUI().getInventory(player, 0, args.hasOption("free-mode")));
        if (!args.hasOption("silent")) sender.sendRichMessage("<green>Shop has been opened for <aqua>" + player.getName() + "<green>.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        return OptionParser.complete(sender, OptionParser.parse(oldArgs), (s, args) -> {
            if (args.args().length == 1) return this.game.getOnlinePlayers().stream().map(Player::getName).toList();
            return List.of();
        }, Set.of("silent", "free-mode"), Map.of());
    }

}
