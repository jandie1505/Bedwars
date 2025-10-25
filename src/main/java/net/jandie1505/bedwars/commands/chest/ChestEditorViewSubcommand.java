package net.jandie1505.bedwars.commands.chest;

import net.chaossquad.mclib.command.OptionParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ChestEditorViewSubcommand {

    private ChestEditorViewSubcommand() {}

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] oldArgs, @Nullable Inventory inventory) {

        OptionParser.Result args = OptionParser.parse(oldArgs);

        if (inventory == null) {
            sender.sendRichMessage("<red>Inventory not available!");
            return true;
        }

        if (args.args().length > 0) {
            int slot = Integer.parseInt(args.args()[0]);

            if (slot < 0 || slot >= inventory.getSize()) {
                sender.sendRichMessage("<red>Invalid slot number!");
                return true;
            }

            ItemStack item = inventory.getItem(slot);
            if (item != null) {
                sender.sendMessage(Component.empty()
                        .append(Component.text("Item Info on slot " + slot + ":", NamedTextColor.GOLD)).appendNewline()
                        .append(Component.text(item.toString(), NamedTextColor.YELLOW))
                );
            } else {
                sender.sendRichMessage("<red>There is no item on this slot!");
            }

        } else {

            if (inventory.isEmpty()) {
                sender.sendRichMessage("<gold>Inventory is empty!");
                return true;
            }

            Component out = Component.empty()
                    .append(Component.text("Inventory content:", NamedTextColor.GOLD));

            for (int slot = 0; slot < inventory.getSize(); slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) continue;

                out = out.appendNewline()
                        .append(Component.text(slot + ".", NamedTextColor.GOLD)).appendSpace()
                        .append(Component.text(item.getType().name(), NamedTextColor.YELLOW)).appendSpace()
                        .append(Component.text(item.getAmount())).appendSpace();

            }

            sender.sendMessage(out);
        }

        return true;
    }

    public static @NotNull List<String> onTabComplete(@NotNull String @NotNull [] args, @Nullable Inventory inventory) {
        if (inventory == null) return List.of();

        if (args.length == 1) {
            List<String> out = new ArrayList<>();
            for (int i = 0; i < inventory.getSize(); i++) {
                out.add(String.valueOf(i));
            }
            return out;
        }

        return List.of();
    }
}
