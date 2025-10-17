package net.jandie1505.bedwars.game.lobby.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LobbyValueSubcommand extends SubcommandCommand {
    @NotNull private final Lobby lobby;

    public LobbyValueSubcommand(@NotNull Lobby lobby) {
        super(lobby.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));
        this.lobby = lobby;

        this.addSubcommand("time", SubcommandEntry.of(new SingleValueSubcommand<>("time", lobby::getTime, time -> lobby.setTime(Objects.requireNonNullElse(time, 0)), s -> s != null ? Integer.parseInt(s) : 0)));
        this.addSubcommand("voting", SubcommandEntry.of(new SingleValueSubcommand<>("map", lobby::isMapVoting, mapVoting -> lobby.setMapVoting(Objects.requireNonNullElse(mapVoting, false)), Boolean::parseBoolean)));
        this.addSubcommand("requiredplayers", SubcommandEntry.of(new SingleValueSubcommand<>("requiredplayers", lobby::getRequiredPlayers, requiredPlayers -> lobby.setRequiredPlayers(Objects.requireNonNullElse(requiredPlayers, 0)), s -> s != null ? Integer.parseInt(s) : 0)));
        this.addSubcommand("map", SubcommandEntry.of(new SingleValueSubcommand<>("map", lobby::getSelectedMap, lobby::selectMap, value -> value)));
    }

    private class SingleValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final LobbyValueSubcommand.SingleValueSubcommand.ValueGetter<TYPE> getter;
        @Nullable
        private final LobbyValueSubcommand.SingleValueSubcommand.ValueSetter<TYPE> setter;
        @NotNull private final LobbyValueSubcommand.Converter<TYPE> converter;

        public SingleValueSubcommand(@NotNull String name, @NotNull LobbyValueSubcommand.SingleValueSubcommand.ValueGetter<TYPE> getter, @Nullable LobbyValueSubcommand.SingleValueSubcommand.ValueSetter<TYPE> setter, @NotNull LobbyValueSubcommand.Converter<TYPE> converter) {
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

                    if (args[0].equalsIgnoreCase("null")) {
                        this.setter.setValue(null);
                        sender.sendRichMessage("<green>Value set to null.");
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
            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @Nullable TYPE getValue();
        }

        public interface ValueSetter<TYPE> {
            void setValue(@Nullable TYPE value);
        }

    }

    private class MultiValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final LobbyValueSubcommand.MultiValueSubcommand.ValueGetter<TYPE> getter;
        @Nullable private final LobbyValueSubcommand.MultiValueSubcommand.ValueSetter<TYPE> setter;
        @NotNull private final LobbyValueSubcommand.Converter<TYPE> converter;

        public MultiValueSubcommand(@NotNull String name, @NotNull LobbyValueSubcommand.MultiValueSubcommand.ValueGetter<TYPE> getter, @Nullable LobbyValueSubcommand.MultiValueSubcommand.ValueSetter<TYPE> setter, @NotNull LobbyValueSubcommand.Converter<TYPE> converter) {
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

                        if (args[1].equalsIgnoreCase("null")) {
                            this.setter.setValue(key, null);
                            sender.sendRichMessage("<green>Value set to null.");
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
            void setValue(@NotNull String key, @Nullable TYPE value);
        }

    }

    private interface Converter<TYPE> {
        TYPE convert(@Nullable String value);
    }

}
