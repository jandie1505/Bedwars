package net.jandie1505.bedwars.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public interface Permissions {

    static boolean admin(@Nullable CommandSender sender) {
        if (sender == null) return false;
        return sender == Bukkit.getConsoleSender() || sender.hasPermission("bedwars.admin");
    }

}
