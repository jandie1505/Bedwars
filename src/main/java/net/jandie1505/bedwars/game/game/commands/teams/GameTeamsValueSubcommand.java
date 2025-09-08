package net.jandie1505.bedwars.game.game.commands.teams;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.GameTeamsSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GameTeamsValueSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameTeamsValueSubcommand(@NotNull Game game) {
        super(game.getPlugin());
        this.game = game;

        this.addSubcommand("disablebed", SubcommandEntry.of(new SingleValueSubcommand<>("disablebed", BedwarsTeam::isBedDisabled, BedwarsTeam::setBedDisabled, Boolean::parseBoolean)));

        this.addSubcommand("upgrades", SubcommandEntry.of(new MultiValueSubcommand<>("upgrades", BedwarsTeam::getUpgrades, BedwarsTeam::setUpgrade, Integer::parseInt)));
    }

    private class SingleValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final SingleValueSubcommand.ValueGetter<TYPE> getter;
        @NotNull private final SingleValueSubcommand.ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public SingleValueSubcommand(@NotNull String name, @NotNull SingleValueSubcommand.ValueGetter<TYPE> getter, @NotNull SingleValueSubcommand.ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a team!");
                return true;
            }

            try {

                BedwarsTeam team = GameTeamsValueSubcommand.this.game.getTeam(Integer.parseInt(args[0]));
                if (team == null) {
                    sender.sendRichMessage("<red>Team does not exist!");
                    return true;
                }

                if (args.length > 1) {

                    String value = "";
                    for (int i = 1; i < args.length; i++) {
                        value += args[i] + (i == args.length - 1 ? "" : " ");
                    }

                    this.setter.setValue(team, this.converter.convert(value));
                    sender.sendRichMessage("<green>Updated " + this.name + " to " + value);
                } else {
                    sender.sendRichMessage("<gray>" + this.name + ": " + this.getter.getValue(team));
                }

            } catch (Exception e) {
                sender.sendRichMessage("<gray>Invalid argument: " + e.getMessage());
            }

            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (args.length == 1) return GameTeamsSubcommand.completeTeamIds(GameTeamsValueSubcommand.this.game.getTeams().size());
            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @Nullable TYPE getValue(@NotNull BedwarsTeam team);
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull BedwarsTeam team, @NotNull TYPE value);
        }

    }

    /**
     * A subcommand that can modify map values in PlayerData.
     */
    private class MultiValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final MultiValueSubcommand.ValueGetter<TYPE> getter;
        @NotNull private final MultiValueSubcommand.ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public MultiValueSubcommand(@NotNull String name, @NotNull MultiValueSubcommand.ValueGetter<TYPE> getter, @NotNull MultiValueSubcommand.ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a team!");
                return true;
            }

            try {

                BedwarsTeam team = GameTeamsValueSubcommand.this.game.getTeam(Integer.parseInt(args[0]));
                if (team == null) {
                    sender.sendRichMessage("<red>Team does not exist!");
                    return true;
                }

                if (args.length > 1) {

                    String key = args[1];

                    if (args.length > 2) {

                        String value = "";
                        for (int i = 2; i < args.length; i++) {
                            value += args[i] + (i == args.length - 1 ? "" : " ");
                        }

                        this.setter.setValue(team, key, this.converter.convert(value));
                        sender.sendRichMessage("<green>Updated " + key + " (" + this.name + ") to " + value);

                    } else {
                        TYPE value = this.getter.getValues(team).get(key);
                        sender.sendRichMessage("<gray>" + key + ": " + value);
                    }

                } else {

                    Map<String, TYPE> values = this.getter.getValues(team);

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

            if (args.length == 1) return GameTeamsSubcommand.completeTeamIds(GameTeamsValueSubcommand.this.game.getTeams().size());

            if (args.length == 2) {

                BedwarsTeam team = GameTeamsValueSubcommand.this.game.getTeam(Integer.parseInt(args[0]));
                if (team == null) return List.of();

                return this.getter.getValues(team).keySet().stream().toList();
            }

            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @NotNull Map<String, TYPE> getValues(@NotNull BedwarsTeam team);
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull BedwarsTeam team, @NotNull String key, @NotNull TYPE value);
        }

    }

    private interface Converter<TYPE> {
        TYPE convert(@NotNull String value);
    }
    
}
