package net.jandie1505.bedwars.commands;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.generators.Generator;
import net.jandie1505.bedwars.game.generators.PublicGenerator;
import net.jandie1505.bedwars.game.generators.TeamGenerator;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.timeactions.*;
import net.jandie1505.bedwars.lobby.Lobby;
import net.jandie1505.bedwars.lobby.LobbyPlayerData;
import net.jandie1505.bedwars.lobby.MapData;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                sender.sendMessage("§7Usage: /bedwars stop/status/start/force-stop/players/bypass/mapteleport/gameinfo/getgamevalue/setgamevalue/maps/forcemap/votemap/getlobbyvalue/setlobbyvalue/reload/leave/pause");
            } else {
                sender.sendMessage("§7Usage: /bedwars leave/maps/votemap/");
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
            case "maptp":
            case "mapteleport":
                this.mapTeleportSubcommand(sender);
                break;
            case "world":
            case "worlds":
                this.worldsSubcommand(sender, args);
                break;
            case "gameinfo":
                this.gameInfoSubcommand(sender);
                break;
            case "getgamevalue":
                this.getGameValueSubcommand(sender, args);
                break;
            case "setgamevalue":
                this.setGameValue(sender, args);
                break;
            case "map":
            case "maps":
                this.mapsSubcommand(sender);
                break;
            case "forcemap":
                this.forcemapSubcommand(sender, args);
                break;
            case "votemap":
                this.votemapCommand(sender, args);
                break;
            case "getlobbyvalue":
                this.getLobbyValueSubcommand(sender, args);
                break;
            case "setlobbyvalue":
                this.setLobbyValueSubcommand(sender, args);
                break;
            case "reload":
                this.reloadSubcommand(sender);
                break;
            case "leave":
                this.leaveSubcommand(sender);
                break;
            case "pause":
                this.pauseSubcommand(sender, args);
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
        this.forceStopSubcommand(sender);
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

                switch (args[1]) {
                    case "add": {
                        if (args.length < 3) {
                            sender.sendMessage("§c/bedwars players add <player>");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        if (((Lobby) this.plugin.getGame()).addPlayer(playerId)) {
                            sender.sendMessage("§aPlayer successfully added");
                        } else {
                            sender.sendMessage("§cCould not add player");
                        }

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

                        ((Lobby) this.plugin.getGame()).removePlayer(playerId);
                        sender.sendMessage("§aPlayer removed");

                        break;
                    }
                    case "get": {
                        String returnString = "§7Lobby Players:";

                        for (UUID playerId : ((Lobby) this.plugin.getGame()).getPlayers().keySet()) {

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

                        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayers().get(playerId);

                        if (playerData == null) {
                            sender.sendMessage("§cPlayer not in lobby");
                            return;
                        }

                        sender.sendMessage("§7Player Info:");
                        sender.sendMessage("§7Vote: " + playerData.getVote());
                        sender.sendMessage("§7Team: " + playerData.getTeam());

                        break;
                    }
                    case "getvalue": {
                        if (args.length < 4) {
                            sender.sendMessage("§cInvalid arguments");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayers().get(playerId);

                        if (playerData == null) {
                            sender.sendMessage("§cPlayer not in lobby");
                            return;
                        }

                        switch (args[3]) {
                            case "vote":
                                sender.sendMessage("§7Vote: " + playerData.getVote());
                                break;
                            case "team":
                                sender.sendMessage("§7Team: " + playerData.getTeam());
                                break;
                            default:
                                sender.sendMessage("§cUnknown value");
                                break;
                        }

                        break;
                    }
                    case "setvalue": {
                        if (args.length < 5) {
                            sender.sendMessage("§cInvalid arguments");
                            return;
                        }

                        UUID playerId = this.getPlayerUUID(args[2]);

                        if (playerId == null) {
                            sender.sendMessage("§cPlayer does not exist");
                            return;
                        }

                        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayers().get(playerId);

                        if (playerData == null) {
                            sender.sendMessage("§cPlayer not in lobby");
                            return;
                        }

                        switch (args[3]) {
                            case "vote":
                                if (args[4].equalsIgnoreCase("null")) {
                                    playerData.setVote(null);
                                    sender.sendMessage("§aCleared map vote");
                                } else {

                                    String mapName = args[2];

                                    for (int i = 3; i < args.length; i++) {

                                        mapName = mapName + " " + args[i];

                                    }

                                    MapData mapData = null;

                                    for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {

                                        if (mapName.startsWith("w:")) {

                                            if (map.getWorld().equals(mapName.substring(2))) {
                                                mapData = map;
                                            }

                                        } else {

                                            if (map.getName().equals(mapName)) {
                                                mapData = map;
                                            }

                                        }

                                    }

                                    if (mapData == null) {
                                        sender.sendMessage("§cMap does not exist (Set map to null if you want to clear vote)");
                                        return;
                                    }

                                    playerData.setVote(mapData);
                                    sender.sendMessage("§aMap vote set");

                                }
                                break;
                            case "team":
                                playerData.setTeam(Integer.parseInt(args[4]));
                                sender.sendMessage("§aTeam set");
                                break;
                            default:
                                sender.sendMessage("§cUnknown value");
                                break;
                        }

                        break;
                    }
                    default:
                        sender.sendMessage("§cUnknown subcommand");
                        break;
                }

            } else if (this.plugin.getGame() instanceof Game) {

                switch (args[1]) {
                    case "add": {
                        if (args.length < 4) {
                            sender.sendMessage("§c/bedwars players add <player> <team>");
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
                        infoString = infoString + "\nKills: " + playerData.getKills();
                        infoString = infoString + "\nDeaths: " + playerData.getDeaths();
                        infoString = infoString + "\nBeds Broken: " + playerData.getBedsBroken();
                        infoString = infoString + "\nArmor Upgrade: " + playerData.getArmorUpgrade();
                        infoString = infoString + "\nPickaxe Upgrade: " + playerData.getPickaxeUpgrade();
                        infoString = infoString + "\nShears Upgrade: " + playerData.getShearsUpgrade();
                        infoString = infoString + "\nFireball Cooldown: " + playerData.getFireballCooldown();
                        infoString = infoString + "\nTrap Cooldown: " + playerData.getTrapCooldown();

                        sender.sendMessage(infoString);

                        break;
                    }
                    case "getvalue": {
                        if (args.length < 4) {
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

                        switch (args[3]) {
                            case "team":
                                sender.sendMessage("§7Team: " + playerData.getTeam());
                                break;
                            case "alive":
                                sender.sendMessage("§7Alive: " + playerData.isAlive());
                                break;
                            case "respawncountdown":
                                sender.sendMessage("§7Respawn Countdown: " + playerData.getRespawnCountdown());
                                break;
                            case "kills":
                                sender.sendMessage("§7Kills: " + playerData.getKills());
                                break;
                            case "deaths":
                                sender.sendMessage("§7Deaths: " + playerData.getDeaths());
                                break;
                            case "bedsbroken":
                                sender.sendMessage("§7Beds broken: " + playerData.getBedsBroken());
                                break;
                            case "armor":
                                sender.sendMessage("§7Armor Upgrade: " + playerData.getArmorUpgrade());
                                break;
                            case "pickaxe":
                                sender.sendMessage("§7Pickaxe Upgrade: " + playerData.getPickaxeUpgrade());
                                break;
                            case "shears":
                                sender.sendMessage("§7Shears Upgrade: " + playerData.getShearsUpgrade());
                                break;
                            case "fireballcooldown":
                                sender.sendMessage("§7Fireball Cooldown: " + playerData.getFireballCooldown());
                                break;
                            case "trapcooldown":
                                sender.sendMessage("§7Trap Cooldown: " + playerData.getTrapCooldown());
                                break;
                            default:
                                sender.sendMessage("§cUsage: /bedwars players getvalue <player> team/alive/respawncountdown/kills/deaths/bedsbroken/armor/pickaxe/shears/fireballcooldown/trapcooldown");
                                break;
                        }

                        break;
                    }
                    case "setvalue": {
                        if (args.length < 5) {
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

                        switch (args[3]) {
                            case "team":
                                playerData.setTeam(Integer.parseInt(args[4]));
                                sender.sendMessage("§aTeam set");
                                break;
                            case "alive":
                                playerData.setAlive(Boolean.parseBoolean(args[4]));
                                sender.sendMessage("§aAlive status set");
                                break;
                            case "respawncountdown":
                                playerData.setRespawnCountdown(Integer.parseInt(args[4]));
                                sender.sendMessage("§aRespawn countdown set");
                                break;
                            case "kills":
                                playerData.setKills(Integer.parseInt(args[4]));
                                sender.sendMessage("§aKills set");
                                break;
                            case "deaths":
                                playerData.setDeaths(Integer.parseInt(args[4]));
                                sender.sendMessage("§aDeaths set");
                                break;
                            case "bedsbroken":
                                playerData.setBedsBroken(Integer.parseInt(args[4]));
                                sender.sendMessage("§aBeds broken set");
                                break;
                            case "armor":
                                playerData.setArmorUpgrade(Integer.parseInt(args[4]));
                                sender.sendMessage("§aArmor Upgrade set");
                                break;
                            case "pickaxe":
                                playerData.setPickaxeUpgrade(Integer.parseInt(args[4]));
                                sender.sendMessage("§aPickaxe Upgrade set");
                                break;
                            case "shears":
                                playerData.setShearsUpgrade(Integer.parseInt(args[4]));
                                sender.sendMessage("§aShears Upgrade set");
                                break;
                            case "fireballcooldown":
                                playerData.setFireballCooldown(Integer.parseInt(args[4]));
                                sender.sendMessage("§aFireball cooldown set");
                                break;
                            case "trapcooldown":
                                playerData.setTrapCooldown(Integer.parseInt(args[4]));
                                sender.sendMessage("§aTrap cooldown set");
                                break;
                            default:
                                sender.sendMessage("§cUsage: /bedwars players getvalue <player> team/alive/respawncountdown/kills/deaths/bedsbroken/armor/pickaxe/shears");
                                break;
                        }

                        break;
                    }
                    case "enderchest":
                        if (args.length < 3) {
                            sender.sendMessage("§cUsage: /bedwars players enderchest <Name> [clear/remove]");
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

                        if (args.length == 3) {

                            if (sender instanceof Player) {
                                ((Player) sender).openInventory(playerData.getEnderchest());
                            } else {
                                sender.sendMessage("Ender Chest Contents:");

                                for (ItemStack item : playerData.getEnderchest().getContents()) {
                                    sender.sendMessage("§7" + item.toString());
                                }
                            }

                        } else {

                            switch (args[3]) {
                                case "clear":
                                    playerData.getEnderchest().clear();
                                    sender.sendMessage("§aCleared enderchest");
                                    break;
                                case "remove":

                                    if (args.length != 5) {
                                        sender.sendMessage("§cUsage: /bedwars players enderchest <Name> remove slot/material");
                                        return;
                                    }

                                    Material material = Material.getMaterial(args[4]);

                                    if (material != null) {
                                        playerData.getEnderchest().remove(material);
                                        sender.sendMessage("§aRemoved material " + material.name());
                                        return;
                                    }

                                    playerData.getEnderchest().setItem(Integer.parseInt(args[4]), new ItemStack(Material.AIR));
                                    sender.sendMessage("§aRemoved item on slot " + Integer.parseInt(args[4]));

                                    break;
                                default:
                                    sender.sendMessage("§cInvalid subcommand");
                                    break;
                            }

                        }

                        break;
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

        sender.sendMessage("§4The usage of this command is NOT SUPPORTED. It is used to manually load, unload and enter worlds. It has not been tested for functionality with the rest of the plugin. Please use this command only if you absolutely have to and if no game is running. No responsibility is taken for broken, unreset worlds or other errors.");

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars world list/load/unload/teleport");
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

                if (world == this.plugin.getServer().getWorlds().get(0)) {
                    sender.sendMessage("§cYou cannot unload the default world");
                    return;
                }

                for (Player player : List.copyOf(world.getPlayers())) {
                    player.teleport(new Location(this.plugin.getServer().getWorlds().get(0), 0.5, 0, 0.5));
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

    public void gameInfoSubcommand(CommandSender sender) {

        if (!(this.hasAdminPermission(sender))) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        sender.sendMessage("§7Game Information:");
        sender.sendMessage("§7World: [" + this.plugin.getServer().getWorlds().indexOf(((Game) this.plugin.getGame()).getWorld()) + "] " + ((Game) this.plugin.getGame()).getWorld().getName() + " (" + ((Game) this.plugin.getGame()).getWorld().getUID() + ")");
        sender.sendMessage("§7Teams: " + ((Game) this.plugin.getGame()).getTeams().size() + " (Use teams command)");
        sender.sendMessage("§7Players: " + ((Game) this.plugin.getGame()).getPlayers().size() + " (Use players command)");
        sender.sendMessage("§7Generators: " + ((Game) this.plugin.getGame()).getGenerators().size());
        sender.sendMessage("§7Time Actions: " + ((Game) this.plugin.getGame()).getTimeActions().size());
        sender.sendMessage("§7Respawn Cooldown: " + ((Game) this.plugin.getGame()).getRespawnCountdown());
        sender.sendMessage("§7Player Placed Blocks: " + ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().size());
        sender.sendMessage("§7Max time: " + ((Game) this.plugin.getGame()).getMaxTime());
        sender.sendMessage("§7Spawn Protection (radius): " + ((Game) this.plugin.getGame()).getSpawnBlockPlaceProtection());
        sender.sendMessage("§7Villager Protection (radius): " + ((Game) this.plugin.getGame()).getVillagerBlockPlaceProtection());
        sender.sendMessage("§7Map Center: " + ((Game) this.plugin.getGame()).getCenterLocation().getX() + " " + ((Game) this.plugin.getGame()).getCenterLocation().getY() + " " + ((Game) this.plugin.getGame()).getCenterLocation().getZ());
        sender.sendMessage("§7Map Radius: " + ((Game) this.plugin.getGame()).getMapRadius());
        sender.sendMessage("§7Time: " + ((Game) this.plugin.getGame()).getTime());
        sender.sendMessage("§7Emerald Generator Level: " + ((Game) this.plugin.getGame()).getPublicEmeraldGeneratorLevel());
        sender.sendMessage("§7Diamond Generator Level: " + ((Game) this.plugin.getGame()).getPublicDiamondGeneratorLevel());

    }

    public void getGameValueSubcommand(CommandSender sender, String[] args) {

        if (!(this.hasAdminPermission(sender))) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars getgamevalue world/generators/timeactions/respawncooldown/playerblocks/maxtime/spawnprotection/villagerprotection/center/radius/time/emeraldlevel/diamondlevel/armorconfig/teamupgradesconfig/itemshop");
            return;
        }

        switch (args[1]) {
            case "world":
                sender.sendMessage("§7World: [" + this.plugin.getServer().getWorlds().indexOf(((Game) this.plugin.getGame()).getWorld()) + "] " + ((Game) this.plugin.getGame()).getWorld().getName() + " (" + ((Game) this.plugin.getGame()).getWorld().getUID() + ")");
                break;
            case "teams":
                sender.sendMessage("§cUse teams command for that");
                break;
            case "players":
                sender.sendMessage("§cIse players command for that");
                break;
            case "generators":
                sender.sendMessage("§7Generators:");

                int generatorIndex = 0;
                for (Generator generator : ((Game) this.plugin.getGame()).getGenerators()) {

                    String out = "[" + generatorIndex + "] " + generator.getItem() + " " + generator.getLevel() + " " + generator.getSpeed() + " " + generator.isEnabled() + " " + generator.getGeneratorTimer() + " " + generator.getLocation().getX() + " " + generator.getLocation().getY() + " " + generator.getLocation().getZ();

                    if (generator instanceof PublicGenerator) {
                        out = out + " PUBLIC";
                    } else if (generator instanceof TeamGenerator) {
                        out = out + " TEAM " + ((TeamGenerator) generator).getTeam().getId();
                    } else {
                        out = out + " UNKNOWN TYPE";
                    }

                    out = out + ";";

                    sender.sendMessage("§7" + out);

                    generatorIndex++;
                }

                break;
            case "timeactions":
                sender.sendMessage("§7Time Actions:");

                int timeActionIndex = 0;
                for (TimeAction timeAction : ((Game) this.plugin.getGame()).getTimeActions()) {

                    String out = "[" + timeActionIndex + "] " + timeAction.getTime() + " " + timeAction.isCompleted();

                    if (timeAction instanceof EmeraldGeneratorUpgradeAction) {
                        out = out + " EMERALD_UPGRADE (GENERATOR_UPGRADE TYPE 2) " + ((EmeraldGeneratorUpgradeAction) timeAction).getUpgradeLevel();
                    } else if (timeAction instanceof DiamondGeneratorUpgradeAction) {
                        out = out + " DIAMOND_UPGRADE (GENERATOR_UPGRADE TYPE 1) " + ((DiamondGeneratorUpgradeAction) timeAction).getUpgradeLevel();
                    } else if (timeAction instanceof DestroyBedsAction) {
                        out = out + " DESTROY_BEDS " + ((DestroyBedsAction) timeAction).isDisableBeds();
                    } else if (timeAction instanceof WorldborderChangeTimeAction) {
                        out = out + " WORLDBORDER_CHANGE " + ((WorldborderChangeTimeAction) timeAction).getRadius();
                    } else {
                        out = out + " UNKNOWN";
                    }

                    out = out + ";";
                    sender.sendMessage("§7" + out);

                    timeActionIndex++;
                }

                break;
            case "respawncooldown":
                sender.sendMessage("§7Respawn Cooldown: " + ((Game) this.plugin.getGame()).getRespawnCountdown());
                break;
            case "playerblocks":
                sender.sendMessage("§7Player Placed Blocks:");

                int index = 0;
                for (Location location : ((Game) this.plugin.getGame()).getPlayerPlacedBlocks()) {

                    if (location.getWorld() == null) {
                        sender.sendMessage("§cWORLD NULL");
                        continue;
                    }

                    sender.sendMessage("§7[" + index + "] " + location.getWorld().getUID() +  location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " " + location.getBlock().getType().toString());

                    index++;
                }

                break;
            case "maxtime":
                sender.sendMessage("§7Max time: " + ((Game) this.plugin.getGame()).getMaxTime());
                break;
            case "spawnprotection":
                sender.sendMessage("§7Spawn Protection (radius): " + ((Game) this.plugin.getGame()).getSpawnBlockPlaceProtection());
                break;
            case "villagerprotection":
                sender.sendMessage("§7Villager Protection (radius): " + ((Game) this.plugin.getGame()).getVillagerBlockPlaceProtection());
                break;
            case "center":
                sender.sendMessage("§7Map Center: " + ((Game) this.plugin.getGame()).getCenterLocation().getX() + " " + ((Game) this.plugin.getGame()).getCenterLocation().getY() + " " + ((Game) this.plugin.getGame()).getCenterLocation().getZ());
                break;
            case "radius":
                sender.sendMessage("§7Map Radius: " + ((Game) this.plugin.getGame()).getMapRadius());
                break;
            case "time":
                sender.sendMessage("§7Time: " + ((Game) this.plugin.getGame()).getTime());
                break;
            case "emeraldlevel":
                sender.sendMessage("§7Emerald Generator Level: " + ((Game) this.plugin.getGame()).getPublicEmeraldGeneratorLevel());
                break;
            case "diamondlevel":
                sender.sendMessage("§7Diamond Generator Level: " + ((Game) this.plugin.getGame()).getPublicDiamondGeneratorLevel());
                break;
            case "armorconfig":
                sender.sendMessage("§7Armor Config:");
                sender.sendMessage("§7Item ids: HEAD CHEST LEGS BOOTS " + ((Game) this.plugin.getGame()).getArmorConfig().getDefaultHelmet() + " " + ((Game) this.plugin.getGame()).getArmorConfig().getDefaultChestplate() + " " + ((Game) this.plugin.getGame()).getArmorConfig().getDefaultLeggings() + " " + ((Game) this.plugin.getGame()).getArmorConfig().getDefaultBoots());
                sender.sendMessage("§7Copy boots: HEAD CHEST LEGS " + ((Game) this.plugin.getGame()).getArmorConfig().isCopyHelmet() + " " + ((Game) this.plugin.getGame()).getArmorConfig().isCopyChestplate() + " " + ((Game) this.plugin.getGame()).getArmorConfig().isCopyLeggings());
                break;
            case "teamupgradesconfig":
                sender.sendMessage("§cCurrently not supported");
                break;
            default:
                sender.sendMessage("§cInvalid value");
                break;
        }

    }

    public void setGameValue(CommandSender sender, String[] args) {

        if (!(this.hasAdminPermission(sender))) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /bedwars setgamevalue time/emeraldlevel/diamondlevel <value>");
            return;
        }

        switch (args[1]) {
            case "time":

                try {
                    int value = Integer.parseInt(args[2]);

                    if (value < 0) {
                        sender.sendMessage("§cPlease specify a value higher or equal than 0");
                        return;
                    }

                    ((Game) this.plugin.getGame()).setTime(value);
                    sender.sendMessage("§aTime successfully updated to " + value);

                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cPlease specify a valid int value");
                }

                break;
            case "emeraldlevel":

                try {
                    int value = Integer.parseInt(args[2]);

                    if (value < 0) {
                        sender.sendMessage("§cPlease specify a value higher or equal than 0");
                        return;
                    }

                    ((Game) this.plugin.getGame()).setPublicEmeraldGeneratorLevel(value);
                    sender.sendMessage("§aEmerald Generator Level successfully updated to " + value);

                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cPlease specify a valid int value");
                }

                break;
            case "diamondlevel":

                try {
                    int value = Integer.parseInt(args[2]);

                    if (value < 0) {
                        sender.sendMessage("§cPlease specify a value higher or equal than 0");
                        return;
                    }

                    ((Game) this.plugin.getGame()).setPublicDiamondGeneratorLevel(value);
                    sender.sendMessage("§aDiamond Generator Level successfully updated to " + value);

                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cPlease specify a valid int value");
                }

                break;
            default:
                sender.sendMessage("§cInvalid value");
                break;
        }

    }

    public void mapsSubcommand(CommandSender sender) {

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        MapData mapData = ((Lobby) this.plugin.getGame()).getSelectedMap();

        if (mapData == null) {
            sender.sendMessage("§7No map selected");
        } else {
            sender.sendMessage("§7Selected map: " + mapData.getName() + " (" + mapData.getWorld() + ")");
        }

        sender.sendMessage("§7Available Maps:");

        for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {
            sender.sendMessage("§7" + map.getName() + " (" + map.getWorld() + ")");
        }

    }

    public void forcemapSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /combattest forcemap <mapName/w:worldName>");
            return;
        }

        if (args[1].equalsIgnoreCase("null") || args[1].equalsIgnoreCase("none")) {
            ((Lobby) this.plugin.getGame()).selectMap(null);
            sender.sendMessage("§aSelected map cleared");
            return;
        }

        String mapName = args[1];

        for (int i = 2; i < args.length; i++) {

            mapName = mapName + " " + args[i];

        }

        MapData mapData = null;

        for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {

            if (mapName.startsWith("w:")) {

                if (map.getWorld().equals(mapName.substring(2))) {
                    mapData = map;
                }

            } else {

                if (map.getName().equals(mapName)) {
                    mapData = map;
                }

            }

        }

        if (mapData == null) {
            sender.sendMessage("§cMap does not exist");
            return;
        }

        ((Lobby) this.plugin.getGame()).selectMap(mapData);
        sender.sendMessage("§aMap successfully selected");

    }

    public void votemapCommand(CommandSender sender, String[] args) {

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThe command needs to be executed by a player");
            return;
        }

        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayers().get(((Player) sender).getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cYou are not in the lobby");
            return;
        }

        if (!((Lobby) this.plugin.getGame()).isMapVoting() || ((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
            sender.sendMessage("§cMap voting is already over");
            return;
        }

        if (args.length < 2) {
            playerData.setVote(null);
            sender.sendMessage("§aYou successfully removed your vote");
            return;
        }

        String mapName = args[1];

        for (int i = 2; i < args.length; i++) {

            mapName = mapName + " " + args[i];

        }

        MapData mapData = null;

        for (MapData map : ((Lobby) this.plugin.getGame()).getMaps()) {

            if (map.getName().equals(mapName)) {
                mapData = map;
                break;
            }

        }

        if (mapData == null) {
            sender.sendMessage("§cMap does not exist");
            return;
        }

        playerData.setVote(mapData);
        sender.sendMessage("§aYou voted for " + mapData.getName());

    }

    public void getLobbyValueSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars getlobbyvalue time/map/voting/requiredplayers/paused");
            return;
        }

        switch (args[2]) {
            case "time":
                sender.sendMessage("§7Time: " + ((Lobby) this.plugin.getGame()).getTime());
                break;
            case "map":
                if (((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                    sender.sendMessage("§7Selected Map:" + ((Lobby) this.plugin.getGame()).getSelectedMap().getName() + " (" + ((Lobby) this.plugin.getGame()).getSelectedMap().getWorld() + ")");
                } else {
                    sender.sendMessage("§7Selected Map: ---");
                }
                break;
            case "voting":
                sender.sendMessage("§7Map Voting: " + ((Lobby) this.plugin.getGame()).isMapVoting());
                break;
            case "requiredplayers":
                sender.sendMessage("§7Required Players: " + ((Lobby) this.plugin.getGame()).getRequiredPlayers());
                break;
            case "paused":
                sender.sendMessage("§7Timer Paused: " + ((Lobby) this.plugin.getGame()).isTimerPaused());
                break;
            default:
                sender.sendMessage("§cUnknown value");
                break;
        }

    }

    public void setLobbyValueSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /bedwars setlobbyvalue time/voting/requiredplayers/paused");
            return;
        }

        try {

            switch (args[2]) {
                case "time":
                    ((Lobby) this.plugin.getGame()).setTime(Integer.parseInt(args[3]));
                    sender.sendMessage("§aTime set");
                    break;
                case "map":
                    sender.sendMessage("§7Use /bedwars forcemap <mapName> instead");
                    break;
                case "voting":
                    ((Lobby) this.plugin.getGame()).setMapVoting(Boolean.parseBoolean(args[3]));
                    sender.sendMessage("§aMap voting set");
                    break;
                case "requiredplayers":
                    ((Lobby) this.plugin.getGame()).setRequiredPlayers(Integer.parseInt(args[3]));
                    sender.sendMessage("§aRequired Players set");
                    break;
                case "paused":
                    ((Lobby) this.plugin.getGame()).setTimerPaused(Boolean.parseBoolean(args[3]));
                    sender.sendMessage("§7Timer paused set");
                    break;
                default:
                    sender.sendMessage("§cUnknown value");
                    break;
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid argument");
        }

    }

    public void reloadSubcommand(CommandSender sender) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        sender.sendMessage("§bReloading plugin...");
        this.plugin.reloadPlugin();
        sender.sendMessage("§aPlugin successfully reloaded");

        if (this.plugin.getGame() != null) {
            sender.sendMessage("§aSome changes do not take effect until the next round");
        }

    }

    public void leaveSubcommand(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player");
            return;
        }

        if (this.plugin.getGame() instanceof Game) {
            if (((Game) this.plugin.getGame()).removePlayer(((Player) sender).getUniqueId())) {
                sender.sendMessage("§aLeft game successfully");
            } else {
                sender.sendMessage("§cYou are not ingame");
            }
        } else {
            sender.sendMessage("§cYou need to be ingame to use this command");
        }

    }

    public void pauseSubcommand(CommandSender sender, String[] args) {

        if (!this.hasAdminPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§7Game pause status: " + this.plugin.isPaused());
            return;
        }

        this.plugin.setPaused(Boolean.parseBoolean(args[1]));

        if (this.plugin.isPaused()) {
            sender.sendMessage("§aGame pause enabled");
        } else {
            sender.sendMessage("§aGame pause disabled");
        }

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
        List<String> tabComplete = new ArrayList<>();

        if (args.length == 1) {

            tabComplete.add("maps");
            tabComplete.add("votemap");
            tabComplete.add("leave");

            if (this.hasAdminPermission(sender)) {
                tabComplete.add("status");
                tabComplete.add("stop");
                tabComplete.add("start");
                tabComplete.add("force-stop");
                tabComplete.add("players");
                tabComplete.add("mapteleport");
                tabComplete.add("gameinfo");
                tabComplete.add("getgamevalue");
                tabComplete.add("setgamevalue");
                tabComplete.add("forcemap");
                tabComplete.add("getlobbyvalue");
                tabComplete.add("setlobbyvalue");
                tabComplete.add("reload");
                tabComplete.add("pause");
            }

        }


        return List.copyOf(tabComplete);
    }
}
