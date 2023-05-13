package net.jandie1505.bedwars.commands;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedwarsCommand implements CommandExecutor, TabCompleter {
    private Bedwars plugin;

    public BedwarsCommand(Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length < 1) {

            if (this.hasAdminPermission(sender)) {
                sender.sendMessage("§7Usage: /bedwars stop/status/start/force-stop");
            } else {
                sender.sendMessage("§cCurrently no commands for you :(");
            }

            return true;
        }

        switch (args[0]) {
            case "force-stop":
                this.forceStopSubcommand(sender);
                break;
            case "stop":
                this.stopSubcommand(sender);
                break;
            case "status":
                this.statusSubcommand(sender);
                break;
            case "start":
                this.startSubcommand(sender);
                break;
            case "player":
            case "players":
                this.playersSubcommand(sender, args);
                break;
            case "bypass":
                this.bypassSubcommand(sender, args);
                break;
            case "mapteleport":
                this.mapTeleportSubcommand(sender);
                break;
            default:
                sender.sendMessage("§cUnknown command. Run /bedwars without arguments for help.");
                break;
        }

        return true;
    }

    private void forceStopSubcommand(CommandSender sender) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        this.plugin.stopGame();
        sender.sendMessage("§aGame was force-stopped");

    }

    private void stopSubcommand(CommandSender sender) {
        sender.sendMessage("§cCurrently not supported, use force-stop instead");
    }

    private void statusSubcommand(CommandSender sender) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        GamePart gamePart = this.plugin.getGame();

        if (gamePart instanceof Lobby) {
            sender.sendMessage("§7Current game status: LOBBY");
        } else if (gamePart instanceof Game) {
            sender.sendMessage("§7Current game status: INGAME");
        } else if (gamePart == null){
            sender.sendMessage("§7Current game status: ---");
        } else {
            sender.sendMessage("§cUnknown game status. Use /bedwars force-stop to kill.");
        }

    }

    public void startSubcommand(CommandSender sender) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (this.plugin.getGame() == null) {
            this.plugin.startGame();
            sender.sendMessage("§aStarted game");
        } else if (this.plugin.getGame() instanceof Lobby) {
            ((Lobby) this.plugin.getGame()).forcestart();
            sender.sendMessage("§aForce-started game");
        } else {
            sender.sendMessage("§cGame already running");
        }

    }

    public void playersSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars players add/remove/get/info [player]");
            return;
        }

        try {

            if (this.plugin.getGame() instanceof Lobby) {

                sender.sendMessage("§cLobby currently not supported");

            } else if (this.plugin.getGame() instanceof Game) {

                switch (args[1]) {
                    case "add": {
                        if (args.length < 4) {
                            sender.sendMessage("§cInvalid arguments");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        ((Game) this.plugin.getGame()).addPlayer(playerId, Integer.parseInt(args[3]));
                        sender.sendMessage("§aPlayer added");

                        break;
                    }
                    case "remove": {
                        if (args.length < 3) {
                            sender.sendMessage("§cInvalid arguments");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        ((Game) this.plugin.getGame()).removePlayer(playerId);
                        sender.sendMessage("§aPlayer added");

                        break;
                    }
                    case "get": {
                        String returnString = "§7Ingame Players:";

                        for (UUID playerId : ((Game) this.plugin.getGame()).getPlayers().keySet()) {

                            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(playerId);

                            if (offlinePlayer == null) {
                                returnString = returnString + "\n§7" + playerId + " (UNKNOWN);";
                                continue;
                            }

                            returnString = returnString + "\n§7" + offlinePlayer.getName() + " (" + offlinePlayer.isOnline() + ");";

                        }

                        sender.sendMessage(returnString);

                        break;
                    }
                    case "info": {
                        if (args.length < 3) {
                            sender.sendMessage("§cInvalid arguments");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(playerId);

                        if (playerData == null) {
                            sender.sendMessage("§cPlayer not ingame");
                            return;
                        }

                        String infoString = "§7Player info of ";

                        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerId);

                        if (player != null) {
                            infoString = infoString + player.getName() + ":";
                        }

                        infoString = infoString + "\nTeam: " + playerData.getTeam();
                        infoString = infoString + "\nAlive: " + playerData.isAlive();
                        infoString = infoString + "\nRespawn Countdown: " + playerData.getRespawnCountdown();

                        sender.sendMessage(infoString);

                        break;
                    }
                    default:
                        sender.sendMessage("§cUnknown subcommand");
                        break;
                }

            } else {
                sender.sendMessage("§cUnknown game status");
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cIllegal argument");
            return;
        }

    }

    public void bypassSubcommand(CommandSender sender, String[] args) {

        if (args.length == 1 && sender instanceof Player) {

            sender.sendMessage("§7Your bypass status: " + this.plugin.isPlayerBypassing(((Player) sender).getUniqueId()));

        } else {

            if (!this.hasAdminPermission(sender)) {
                sender.sendMessage("§cNo permission");
                return;
            }

            if (args.length == 2) {

                if (sender instanceof Player && (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true"))) {

                    this.plugin.addBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode enabled");

                } else if (sender instanceof Player && (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))) {

                    this.plugin.removeBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode disabled");

                } else {

                    UUID playerId;

                    try {
                        playerId = UUID.fromString(args[1]);
                    } catch (IllegalArgumentException e) {
                        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(args[1]);

                        if (player == null) {
                            sender.sendMessage("§cUnknown player");
                            return;
                        }

                        playerId = player.getUniqueId();
                    }

                    sender.sendMessage("§7Bypassing status of the player: " + this.plugin.isPlayerBypassing(playerId));

                }

            } else if (args.length == 3) {

                UUID playerId;

                try {
                    playerId = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(args[1]);

                    if (player == null) {
                        sender.sendMessage("§cUnknown player");
                        return;
                    }

                    playerId = player.getUniqueId();
                }

                if (args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true")) {

                    this.plugin.addBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player enabled");

                } else if (args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false")) {

                    this.plugin.removeBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player disabled");

                } else {
                    sender.sendMessage("§cUsage: /combattest bypass <player> on/off");
                    return;
                }

            } else {
                sender.sendMessage("§cUnknown command usage");
            }

        }

    }

    public void mapTeleportSubcommand(CommandSender sender) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou need to be a player to run this command");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        ((Player) sender).teleport(new Location(((Game) this.plugin.getGame()).getMapData().getWorld(), 0, 0, 0, 0, 0));
        sender.sendMessage("§aTeleporting to map...");

    }

    public boolean hasAdminPermission(CommandSender sender) {
        return sender == this.plugin.getServer().getConsoleSender() || (sender instanceof Player && sender.hasPermission("bedwars.admin"));
    }

    public UUID getPlayerUUID(String input) {

        try {

            return UUID.fromString(input);

        } catch (IllegalArgumentException e) {

            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(input);

            if (offlinePlayer == null) {
                return null;
            }

            return offlinePlayer.getUniqueId();

        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return List.of();
    }
}
