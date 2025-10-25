package net.jandie1505.bedwars.commands.chest;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.OptionParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ChestEditorOpenSubcommand {

    private ChestEditorOpenSubcommand() {}

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args, @Nullable Inventory inventory) {

        if (inventory == null) {
            sender.sendRichMessage("<red>Inventory not available!");
            return true;
        }

        @NotNull Player player;

        if (args.length > 0) {

            player = PlayerUtils.getPlayerFromString(args[0]);
            if (player == null) {
                sender.sendRichMessage("<red>Player not found!");
                return true;
            }

        } else if (sender instanceof Player p) {
            player = p;
        } else {
            sender.sendRichMessage("<red>You need to specify or be a player!");
            return true;
        }

        player.openInventory(inventory);
        return true;
    }

    public static @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}
