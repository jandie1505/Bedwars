package net.jandie1505.bedwars.game.lobby.commands;

import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.jandie1505.bedwars.game.lobby.LobbyPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyVotemapCommand implements TabCompletingCommandExecutor {
    @NotNull private final Bedwars plugin;

    public LobbyVotemapCommand(@NotNull Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {

        if (!Permissions.hasPermission(sender, Permissions.VOTE_MAP)) {
            sender.sendRichMessage("<red>No permission.");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Lobby lobby)) {
            sender.sendRichMessage("<red>No lobby running.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command.");
            if (sender == Bukkit.getConsoleSender()) sender.sendRichMessage("<red>You can use /bedwars game players value vote to manipulate player's votes.");
            return true;
        }

        if (!lobby.isMapVoting()) {
            sender.sendRichMessage("<red>Map voting has been disabled.");
            return true;
        }

        if (lobby.getSelectedMap() != null) {
            sender.sendRichMessage("<red>Map voting is already over.");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(oldArgs);

        // Get player data

        LobbyPlayerData playerData = lobby.getPlayerData(player);
        if (playerData == null) {
            sender.sendRichMessage("<red>You need to be in lobby to vote.");
            return true;
        }

        // Build map name from args (map names can contain spaces)

        String mapName = "";
        for (int i = 0; i < args.args().length; i++) {
            mapName += args.args()[i] + (i == args.args().length - 1 ? "" : " ");
        }

        // Search map

        String option = args.options().getOrDefault("source", "");

        String mapId;
        switch (option) {
            case "mapid" -> mapId = mapName;
            case "world-name" -> mapId = lobby.findMapByWorldName(mapName);
            default -> mapId = lobby.findMapByName(mapName);
        }

        if (mapId == null) {
            sender.sendRichMessage("<red>This map does not exist.");
            return true;
        }

        MapData map =  lobby.getMap(mapId);
        if (map == null) {
            sender.sendRichMessage("<red>This map does not exist.");
            return true;
        }

        playerData.setVote(mapId);
        sender.sendRichMessage("<green>You successfully voted for " + map.name() + "<green>!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.VOTE_MAP)) return List.of();
        if (!(this.plugin.getGame() instanceof Lobby lobby)) return List.of();
        if (!lobby.isMapVoting()) return List.of();
        if (lobby.getSelectedMap() != null) return List.of();
        if (args.length == 1) return lobby.getMaps().values().stream().map(MapData::name).toList();
        return List.of();
    }
}
