package net.jandie1505.bedwars.game.game.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.shop.GameShopGiveSubcommand;
import net.jandie1505.bedwars.game.game.commands.shop.GameShopOpenSubcommand;
import org.jetbrains.annotations.NotNull;

public class GameShopSubcommand extends SubcommandCommand {
    @NotNull private final Game game;

    public GameShopSubcommand(@NotNull Game game) {
        super(game.getPlugin(), sender -> Permissions.hasPermission(sender, Permissions.ADMIN));
        this.game = game;

        this.addSubcommand("give", SubcommandEntry.of(new GameShopGiveSubcommand(game)));
        this.addSubcommand("open", SubcommandEntry.of(new GameShopOpenSubcommand(game)));
    }

}
