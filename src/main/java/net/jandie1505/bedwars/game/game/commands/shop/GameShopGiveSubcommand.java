package net.jandie1505.bedwars.game.game.commands.shop;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.shop.ItemShop;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Command that can give shop items to players.
 */
public class GameShopGiveSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameShopGiveSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        OptionParser.Result args = OptionParser.parse(oldArgs);

        if (args.args().length < 2) {
            sender.sendRichMessage("Usage: /bedwars game players give <player> <item> [amount]");
            return true;
        }

        Player player = PlayerUtils.getPlayerFromString(args.args()[0]);
        if (player == null) {
            sender.sendRichMessage("Player not found.");
            return true;
        }

        ItemShop itemShop = this.game.getItemShop();

        ShopEntry entry = itemShop.getItem(args.args()[1]);
        if (entry == null) {
            sender.sendRichMessage("Item not found.");
            return true;
        }

        ItemStack item = entry.item();

        if (args.args().length > 2) {

            try {
                item.setAmount(Integer.parseInt(args.args()[2]));
            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("Invalid amount.");
                return true;
            }

        }

        player.getInventory().addItem(item);
        if (!args.hasOption("silent")) sender.sendRichMessage("<green>Item has been given to player.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        return OptionParser.complete(
                commandSender,
                OptionParser.parse(oldArgs),
                (sender, args) -> {
                    if (args.args().length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                    if (args.args().length == 2) return this.game.getItemShop().getItems().keySet().stream().toList();
                    if (args.args().length == 3) return List.of("1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024");
                    return List.of();
                },
                Set.of("silent"),
                null
        );
    }

}
