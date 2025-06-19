package net.jandie1505.bedwars.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface Permissions {
    String PREFIX = "bedwars.";

    String ADMIN = PREFIX + "admin";
    String CHAT_FORMATTING = PREFIX + "chat_formatting";
    String START = PREFIX + "start";

    /**
     * Returns if the specified sender has at least one of the specified permissions.<br/>
     * This means, if the sender has one of the specified permissions (or more), this will return true.
     * @param sender sender
     * @param permission permissions
     * @return true = has permission
     */
    static boolean hasPermission(@NotNull CommandSender sender, @NotNull String... permission) {
        if (sender == Bukkit.getConsoleSender() || sender.hasPermission(ADMIN)) return true;

        for (String s : permission) {
            if (sender.hasPermission(s)) return true;
        }

        return false;
    }

}
