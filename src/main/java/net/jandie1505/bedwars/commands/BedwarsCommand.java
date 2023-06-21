package net.jandie1505.bedwars.commands;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.upgrades.UpgradesMenu;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            case "world":
            case "worlds":
                this.worldsSubcommand(sender, args);
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
                        sender.sendMessage("§aPlayer removed");

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

    public void worldsSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars world list/load/unload");
            return;
        }

        switch (args[1]) {
            case "list": {

                String message = "§7Loaded worlds:\n";

                int i = 0;
                for (World world : List.copyOf(this.plugin.getServer().getWorlds())) {

                    message = message + "§7[" + i + "] " + world.getName() + " (" + world.getUID() + ");\n";
                    i++;

                }

                sender.sendMessage(message);

                return;
            }
            case "load": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name");
                    return;
                }

                if (this.plugin.getServer().getWorld(args[2]) != null) {
                    sender.sendMessage("§cWorld already loaded");
                    return;
                }

                sender.sendMessage("§eLoading/creating world...");
                this.plugin.getServer().createWorld(new WorldCreator(args[2]));
                sender.sendMessage("§aWorld successfully loaded/created");

                return;
            }
            case "unload": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return;
                }

                World world = null;

                try {
                    world = this.plugin.getServer().getWorld(UUID.fromString(args[2]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = this.plugin.getServer().getWorlds().get(Integer.parseInt(args[2]));
                    } catch (IllegalArgumentException e2) {
                        world = this.plugin.getServer().getWorld(args[2]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return;
                }

                boolean save = false;

                if (args.length >= 4) {
                    save = Boolean.parseBoolean(args[3]);
                }

                this.plugin.getServer().unloadWorld(world, save);
                sender.sendMessage("§aUnloaded world (save=" + save + ")");

                return;
            }
            case "teleport": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return;
                }

                World world = null;

                try {
                    world = this.plugin.getServer().getWorld(UUID.fromString(args[2]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = this.plugin.getServer().getWorlds().get(Integer.parseInt(args[2]));
                    } catch (IllegalArgumentException e2) {
                        world = this.plugin.getServer().getWorld(args[2]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return;
                }

                Location location = new Location(world, 0, 0, 0, 0, 0);

                if (args.length >= 4) {

                    Player player = this.getPlayer(args[3]);

                    if (player == null) {
                        sender.sendMessage("§cPlayer not online");
                        return;
                    }

                    player.teleport(location);
                    sender.sendMessage("§aTeleporting " + player.getName() + " to " + world.getName());

                } else {

                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cYou need to be a player to teleport yourself");
                        return;
                    }

                    ((Player) sender).teleport(location);
                    sender.sendMessage("§aTeleporting yourself to " + world.getName());

                }

                return;
            }
            default:
                sender.sendMessage("§cUnknown subcommand");
                return;
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

        ((Player) sender).teleport(new Location(((Game) this.plugin.getGame()).getWorld(), 0, 0, 0, 0, 0));
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

    public Player getPlayer(String input) {

        Player player;

        try {
            player = this.plugin.getServer().getPlayer(UUID.fromString(input));
        } catch (IllegalArgumentException e) {
            player = this.plugin.getServer().getPlayer(input);
        }

        return player;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return List.of();
    }
}
