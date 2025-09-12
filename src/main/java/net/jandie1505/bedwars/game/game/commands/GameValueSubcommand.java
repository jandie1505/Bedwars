package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameValueSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameValueSubcommand(@NotNull Game game) {
        super(game.getPlugin());
        this.game = game;

        this.addSubcommand("time", SubcommandEntry.of(new SingleValueSubcommand<>("time", game::getTime, game::setTime, Integer::parseInt)));
    }

    private class SingleValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final ValueGetter<TYPE> getter;
        @Nullable private final ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public SingleValueSubcommand(@NotNull String name, @NotNull ValueGetter<TYPE> getter, @Nullable ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            try {

                if (args.length > 0) {

                    if (this.setter == null) {
                        sender.sendRichMessage("<red>This value cannot be set!");
                        return true;
                    }

                    String value = "";
                    for (int i = 0; i < args.length; i++) {
                        value += args[i] + (i == args.length - 1 ? "" : " ");
                    }

                    this.setter.setValue(this.converter.convert(value));
                    sender.sendRichMessage("<green>Updated " + this.name + " to " + value);
                } else {
                    sender.sendRichMessage("<gray>" + this.name + ": " + this.getter.getValue());
                }

            } catch (Exception e) {
                sender.sendRichMessage("<gray>Invalid argument: " + e.getMessage());
            }

            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (args.length == 1) return GameValueSubcommand.this.game.getOnlinePlayers().stream().map(Player::getName).toList();
            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @Nullable TYPE getValue();
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull TYPE value);
        }

    }

    /**
     * A subcommand that can modify map values in PlayerData.
     */
    private class MultiValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final ValueGetter<TYPE> getter;
        @Nullable private final ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public MultiValueSubcommand(@NotNull String name, @NotNull ValueGetter<TYPE> getter, @Nullable ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            try {

                if (args.length > 0) {

                    String key = args[0];

                    if (args.length > 1) {

                        if (this.setter == null) {
                            sender.sendRichMessage("<red>This value cannot be set!");
                            return true;
                        }

                        String value = "";
                        for (int i = 1; i < args.length; i++) {
                            value += args[i] + (i == args.length - 1 ? "" : " ");
                        }

                        this.setter.setValue(key, this.converter.convert(value));
                        sender.sendRichMessage("<green>Updated " + key + " (" + this.name + ") to " + value);

                    } else {
                        TYPE value = this.getter.getValues().get(key);
                        sender.sendRichMessage("<gray>" + key + ": " + value);
                    }

                } else {

                    Map<String, TYPE> values = this.getter.getValues();

                    if (values.isEmpty()) {
                        sender.sendRichMessage("<red>Values of " + this.name + " are empty.");
                        return true;
                    }

                    Component out = getValueListComponent(values);

                    sender.sendMessage(out);
                }

            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("<red>Invalid argument: " + e.getMessage());
            }

            return true;
        }

        private @NotNull Component getValueListComponent(Map<String, TYPE> values) {
            Component out = Component.empty()
                    .append(Component.text("Values of " + this.name + ":", NamedTextColor.GRAY));

            for (Map.Entry<String, TYPE> e : values.entrySet()) {
                out = out.appendNewline()
                        .append(Component.text(e.getKey(), NamedTextColor.GRAY))
                        .append(Component.text(": ", NamedTextColor.GRAY))
                        .append(Component.text(e.getValue().toString(), NamedTextColor.GRAY));
            }
            return out;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length == 1) {
                return this.getter.getValues().keySet().stream().toList();
            }

            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @NotNull Map<String, TYPE> getValues();
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull String key, @NotNull TYPE value);
        }

    }

    private interface Converter<TYPE> {
        TYPE convert(@NotNull String value);
    }

}
