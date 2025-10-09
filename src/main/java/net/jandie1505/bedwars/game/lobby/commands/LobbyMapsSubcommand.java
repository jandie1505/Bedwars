package net.jandie1505.bedwars.game.lobby.commands;

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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LobbyMapsSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyMapsSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        Map<String, MapData> maps = this.lobby.getMaps();
        if (maps.isEmpty()) {
            sender.sendRichMessage("<red>No maps available!");
            return true;
        }

        Component out = Component.empty()
                .append(Component.text("Available maps:", NamedTextColor.GOLD))
                .appendNewline();

        Iterator<Map.Entry<String, MapData>> i = maps.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, MapData> entry = i.next();
            String mapId = entry.getKey();
            MapData mapData = entry.getValue();

            if (Permissions.hasPermission(sender, Permissions.ADMIN)) {
                out = out.append(Component.text(" - " + mapId + ": " + mapData.name() + " (" + mapData.world() + ")", NamedTextColor.YELLOW));
            } else {
                out = out.append(Component.text(" - " + mapData.name(), NamedTextColor.YELLOW));
            }

            String selectedMap = this.lobby.getSelectedMap();
            if (selectedMap != null && selectedMap.equals(mapId)) out = out.append(Component.text(" <-- Selected", NamedTextColor.AQUA));

            if (i.hasNext()) out = out.appendNewline();
        }

        sender.sendMessage(out);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
