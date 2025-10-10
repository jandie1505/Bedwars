package net.jandie1505.bedwars.game.game.commands.generators;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.generators.Generator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameGeneratorsListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameGeneratorsListSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        List<Generator> generators = this.game.getGenerators();

        if (generators.isEmpty()) {
            sender.sendRichMessage("<red>There are no generators.");
            return true;
        }

        Component out = Component.empty()
                .append(Component.text("Available generators:", NamedTextColor.GOLD))
                .appendNewline();

        for (int i = 0; i < generators.size(); i++) {
            Generator gen = generators.get(i);

            Location l = gen.getLocation();

            out = out.append(Component.text(i + ".", NamedTextColor.YELLOW)).appendSpace()
                    .append(Component.text(gen.getClass().getSimpleName(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(l.getX() + " " + l.getY() + " " + l.getZ(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(gen.getSpawnRate() + " " + gen.getAmount(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(gen.getMaxNearbyItems(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(gen.getGeneratorTimer(), NamedTextColor.DARK_AQUA)).appendSpace()
                    .append(Component.text(gen.getItem().getType().toString(), NamedTextColor.DARK_AQUA));

            if (i < generators.size() - 1) out = out.appendNewline();
        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
