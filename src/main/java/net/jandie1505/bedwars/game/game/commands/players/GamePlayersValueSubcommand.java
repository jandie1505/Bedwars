package net.jandie1505.bedwars.game.game.commands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
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

public class GamePlayersValueSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GamePlayersValueSubcommand(@NotNull Game game) {
        super(game.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));
        this.game = game;

        this.addSubcommand("alive", SubcommandEntry.of(new SingleValueSubcommand<>("alive", PlayerData::isAlive, PlayerData::setAlive, Boolean::parseBoolean)));
        this.addSubcommand("respawncountdown", SubcommandEntry.of(new SingleValueSubcommand<>("respawncountdown", PlayerData::getRespawnCountdown, PlayerData::setRespawnCountdown, Integer::parseInt)));
        this.addSubcommand("team", SubcommandEntry.of(new SingleValueSubcommand<>("team", PlayerData::getTeam, PlayerData::setTeam, Integer::parseInt)));
        this.addSubcommand("kills", SubcommandEntry.of(new SingleValueSubcommand<>("kills", PlayerData::getKills, PlayerData::setKills, Integer::parseInt)));
        this.addSubcommand("deaths", SubcommandEntry.of(new SingleValueSubcommand<>("deaths", PlayerData::getDeaths, PlayerData::setDeaths, Integer::parseInt)));
        this.addSubcommand("bedsbroken", SubcommandEntry.of(new SingleValueSubcommand<>("bedsbroken", PlayerData::getBedsBroken, PlayerData::setBedsBroken, Integer::parseInt)));
        this.addSubcommand("rewardpoints", SubcommandEntry.of(new SingleValueSubcommand<>("rewardpoints", PlayerData::getRewardPoints, PlayerData::setRewardPoints, Integer::parseInt)));

        this.addSubcommand("upgrades", SubcommandEntry.of(new MultiValueSubcommand<>("upgrades", PlayerData::getUpgrades, PlayerData::setUpgrade, Integer::parseInt)));
        this.addSubcommand("timers", SubcommandEntry.of(new MultiValueSubcommand<>("timers", PlayerData::getTimers, PlayerData::setTimer, Integer::parseInt)));
    }

    private class SingleValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final ValueGetter<TYPE> getter;
        @NotNull private final ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public SingleValueSubcommand(@NotNull String name, @NotNull ValueGetter<TYPE> getter, @NotNull ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a player!");
                return true;
            }

            UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
            if (playerId == null) {
                sender.sendRichMessage("<red>Player not found!");
                return true;
            }

            PlayerData playerData = GamePlayersValueSubcommand.this.game.getPlayerData(playerId);
            if (playerData == null) {
                sender.sendRichMessage("<red>Player not ingame!");
                return true;
            }

            try {

                if (args.length > 1) {

                    String value = "";
                    for (int i = 1; i < args.length; i++) {
                        value += args[i] + (i == args.length - 1 ? "" : " ");
                    }

                    this.setter.setValue(playerData, this.converter.convert(value));
                    sender.sendRichMessage("<green>Updated " + this.name + " to " + value);
                } else {
                    sender.sendRichMessage("<gray>" + this.name + ": " + this.getter.getValue(playerData));
                }

            } catch (Exception e) {
                sender.sendRichMessage("<gray>Invalid argument: " + e.getMessage());
            }

            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (args.length == 1) return GamePlayersValueSubcommand.this.game.getOnlinePlayers().stream().map(Player::getName).toList();
            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @Nullable TYPE getValue(@NotNull PlayerData playerData);
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull PlayerData playerData, @NotNull TYPE value);
        }

    }

    /**
     * A subcommand that can modify map values in PlayerData.
     */
    private class MultiValueSubcommand<TYPE> implements TabCompletingCommandExecutor {
        @NotNull private final String name;
        @NotNull private final ValueGetter<TYPE> getter;
        @NotNull private final ValueSetter<TYPE> setter;
        @NotNull private final Converter<TYPE> converter;

        public MultiValueSubcommand(@NotNull String name, @NotNull ValueGetter<TYPE> getter, @NotNull ValueSetter<TYPE> setter, @NotNull Converter<TYPE> converter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a player!");
                return true;
            }

            UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
            if (playerId == null) {
                sender.sendRichMessage("<red>Player not found!");
                return true;
            }

            PlayerData playerData = GamePlayersValueSubcommand.this.game.getPlayerData(playerId);
            if (playerData == null) {
                sender.sendRichMessage("<red>Player not ingame!");
                return true;
            }

            try {

                if (args.length > 1) {

                    String key = args[1];

                    if (args.length > 2) {

                        String value = "";
                        for (int i = 2; i < args.length; i++) {
                            value += args[i] + (i == args.length - 1 ? "" : " ");
                        }

                        this.setter.setValue(playerData, key, this.converter.convert(value));
                        sender.sendRichMessage("<green>Updated " + key + " (" + this.name + ") to " + value);

                    } else {
                        TYPE value = this.getter.getValues(playerData).get(key);
                        sender.sendRichMessage("<gray>" + key + ": " + value);
                    }

                } else {

                    Map<String, TYPE> values = this.getter.getValues(playerData);

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

            if (args.length == 1) return GamePlayersValueSubcommand.this.game.getOnlinePlayers().stream().map(Player::getName).toList();

            if (args.length == 2) {

                UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
                if (playerId == null) return List.of();

                PlayerData playerData = GamePlayersValueSubcommand.this.game.getPlayerData(playerId);
                if (playerData == null) return List.of();

                return this.getter.getValues(playerData).keySet().stream().toList();
            }

            return List.of();
        }

        public interface ValueGetter<TYPE> {
            @NotNull Map<String, TYPE> getValues(@NotNull PlayerData playerData);
        }

        public interface ValueSetter<TYPE> {
            void setValue(@NotNull PlayerData playerData, @NotNull String key, @NotNull TYPE value);
        }

    }

    private interface Converter<TYPE> {
        TYPE convert(@NotNull String value);
    }

}
