package net.jandie1505.bedwars.commands.bypass;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.constants.Permissions;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BypassSubcommand implements TabCompletingCommandExecutor {
    private final Bedwars plugin;

    public BypassSubcommand(Bedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length < 1 && sender instanceof Player player) {

            sender.sendMessage("§7Your bypass status: " + this.plugin.isPlayerBypassing(player) + " " + this.plugin.getBypassingPlayers().contains(player.getUniqueId()));

        } else {

            if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
                sender.sendMessage("§cNo permission");
                return true;
            }

            if (args.length == 1) {

                if (sender instanceof Player && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true"))) {

                    this.plugin.addBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode enabled");

                } else if (sender instanceof Player && (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false"))) {

                    this.plugin.removeBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode disabled");

                } else {

                    UUID playerId;

                    try {
                        playerId = UUID.fromString(args[0]);
                    } catch (IllegalArgumentException e) {
                        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(args[0]);

                        if (player == null) {
                            sender.sendMessage("§cUnknown player");
                            return true;
                        }

                        playerId = player.getUniqueId();
                    }

                    sender.sendMessage("§7Bypassing status of the player: " + this.plugin.isPlayerBypassing(playerId) + " local=" + this.plugin.getBypassingPlayers().contains(playerId));

                }

            } else if (args.length == 2) {

                UUID playerId;

                try {
                    playerId = UUID.fromString(args[0]);
                } catch (IllegalArgumentException e) {
                    OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(args[0]);

                    if (player == null) {
                        sender.sendMessage("§cUnknown player");
                        return true;
                    }

                    playerId = player.getUniqueId();
                }

                if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {

                    this.plugin.addBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player enabled");

                } else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {

                    this.plugin.removeBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player disabled");

                } else {
                    sender.sendMessage("§cUsage: /combattest bypass <player> on/off");
                    return true;
                }

            } else {
                sender.sendMessage("§cUnknown command usage");
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        switch (args.length) {
            case 1 -> {
                return List.of("false", "true");
            }
            case 2 -> {

                if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")) {
                    return List.of();
                }

                return List.of("false", "true");

            }
            default -> {
                return List.of();
            }
        }

    }
}
