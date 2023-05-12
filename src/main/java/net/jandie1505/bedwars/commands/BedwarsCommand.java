package net.jandie1505.bedwars.commands;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.lobby.Lobby;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

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

        if (this.plugin.getGame() != null) {
            sender.sendMessage("§cGame already running");
            return;
        }

        this.plugin.startGame();
        sender.sendMessage("§aStarted game");

    }

    public boolean hasAdminPermission(CommandSender sender) {
        return sender == this.plugin.getServer().getConsoleSender() || (sender instanceof Player && sender.hasPermission("bedwars.admin"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return null;
    }
}
