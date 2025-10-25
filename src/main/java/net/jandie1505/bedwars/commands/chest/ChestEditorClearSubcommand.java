package net.jandie1505.bedwars.commands.chest;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ChestEditorClearSubcommand {

    private ChestEditorClearSubcommand() {}

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args, @Nullable Inventory inventory) {

        if (inventory == null) {
            sender.sendRichMessage("<red>Inventory not available!");
            return true;
        }

        inventory.clear();
        sender.sendRichMessage("<green>Inventory has been cleared!");

        return true;
    }

    public static @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
