package net.jandie1505.bedwars.game.lobby.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.jandie1505.bedwars.game.lobby.commands.players.LobbyPlayersListSubcommand;
import net.jandie1505.bedwars.game.lobby.commands.players.LobbyPlayersRemoveSubcommand;
import net.jandie1505.bedwars.game.lobby.commands.players.LobbyPlayersValueSubcommand;
import org.jetbrains.annotations.NotNull;

public class LobbyPlayersSubcommand extends SubcommandCommand {

    public LobbyPlayersSubcommand(@NotNull Lobby lobby) {
        super(lobby.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));

        this.addSubcommand("remove", SubcommandEntry.of(new LobbyPlayersRemoveSubcommand(lobby)));
        this.addSubcommand("list", SubcommandEntry.of(new LobbyPlayersListSubcommand(lobby)));
        this.addSubcommand("value", SubcommandEntry.of(new LobbyPlayersValueSubcommand(lobby)));
    }

}
