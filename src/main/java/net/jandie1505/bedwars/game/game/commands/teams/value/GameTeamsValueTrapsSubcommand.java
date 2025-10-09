package net.jandie1505.bedwars.game.game.commands.teams.value;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.commands.GameTeamsSubcommand;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class GameTeamsValueTrapsSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameTeamsValueTrapsSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        try {

            if (args.length < 1) {
                sender.sendRichMessage("<red>You need to specify a team!");
                return true;
            }

            BedwarsTeam team = this.game.getTeam(Integer.parseInt(args[0]));
            if (team == null) {
                sender.sendRichMessage("<red>Team not found!");
                return true;
            }

            if (args.length < 2) {
                sender.sendRichMessage("<red>Usage: /bedwars game teams value traps <teamId> (list|get <slotId>|add <slotId> [firstTrapId] [secondTrapId]|remove <slotId>|reset)");
                return true;
            }

            switch (args[1]) {
                case "list" -> {
                    Component out = Component.empty()
                            .append(Component.text("Traps of team " + team.getId() + ": ", NamedTextColor.GOLD))
                            .appendNewline();

                    Iterator<BedwarsTeam.TrapSlot> i = team.getTraps().iterator();
                    int slotId = 0;
                    while (i.hasNext()) {
                        BedwarsTeam.TrapSlot slot = i.next();

                        String firstSlot = slot.first() != null ? slot.first() : "unset";
                        NamedTextColor firstSlotColor = slot.first() != null ? NamedTextColor.DARK_AQUA : NamedTextColor.RED;

                        String secondSlot = slot.second() != null ? slot.second() : "unset";
                        NamedTextColor secondSlotColor = slot.first() != null ? NamedTextColor.DARK_AQUA : NamedTextColor.RED;

                        out = out.append(
                                Component.text(" - Slot " + slotId + ": ", NamedTextColor.YELLOW)
                                        .append(Component.text("[0] ", NamedTextColor.YELLOW)).append(Component.text(firstSlot, firstSlotColor)).appendSpace()
                                        .append(Component.text("[1] " , NamedTextColor.YELLOW)).append(Component.text(secondSlot, secondSlotColor))
                        );

                        if (i.hasNext()) out = out.appendNewline();
                    }

                    sender.sendMessage(out);
                    return true;
                }
                case "get" -> {

                    if (args.length < 3) {
                        sender.sendRichMessage("<red>You need to specify a trap slot!");
                        return true;
                    }

                    int slotId = Integer.parseInt(args[2]);
                    if (slotId < 0) {
                        sender.sendRichMessage("<red>Invalid trap slot id!");
                        return true;
                    }

                    BedwarsTeam.TrapSlot slot = team.getTrap(slotId);
                    if (slot == null) {
                        sender.sendRichMessage("<red>Trap slot does not exist!");
                        return true;
                    }

                    String firstTrap = slot.first() != null ? slot.first() : "unset";
                    NamedTextColor firstTrapColor = slot.first() != null ? NamedTextColor.DARK_AQUA : NamedTextColor.RED;

                    String secondTrap = slot.second() != null ? slot.second() : "unset";
                    NamedTextColor secondTrapColor = slot.first() != null ? NamedTextColor.DARK_AQUA : NamedTextColor.RED;

                    sender.sendMessage(Component.empty()
                            .append(Component.text("Trap slot " + slotId + " of team " + team.getId() + ": ", NamedTextColor.GOLD)).appendNewline()
                            .append(Component.text(" - First trap: ", NamedTextColor.YELLOW)).append(Component.text(firstTrap, firstTrapColor)).appendNewline()
                            .append(Component.text(" - Second trap: ", NamedTextColor.YELLOW)).append(Component.text(secondTrap, secondTrapColor)).appendNewline()
                    );
                    return true;
                }
                case "add" -> {

                    if (args.length < 3) {
                        sender.sendRichMessage("<red>You need to specify a trap slot!");
                        return true;
                    }

                    int slotId = Integer.parseInt(args[2]);
                    if (slotId < 0) {
                        sender.sendRichMessage("<red>Invalid trap slot id!");
                        return true;
                    }

                    String firstTrap = null;
                    String secondTrap = null;

                    if (args.length > 3) firstTrap = args[3];
                    if (args.length > 4) secondTrap = args[4];

                    if (firstTrap != null && firstTrap.equals("null")) firstTrap = null;
                    if (secondTrap != null && secondTrap.equals("null")) secondTrap = null;

                    BedwarsTeam.TrapSlot slot = new BedwarsTeam.TrapSlot(firstTrap, secondTrap);
                    team.getTraps().add(slot);

                    sender.sendRichMessage("<green>Trap has been added at slot " + slotId + "!");
                    return true;
                }
                case "remove" -> {

                    if (args.length < 3) {
                        sender.sendRichMessage("<red>You need to specify a trap slot!");
                        return true;
                    }

                    int slotId = Integer.parseInt(args[2]);
                    if (slotId < 0) {
                        sender.sendRichMessage("<red>Invalid trap slot id!");
                        return true;
                    }

                    if (slotId >= team.getTraps().size()) {
                        sender.sendRichMessage("<red>Trap slot does not exist!");
                        return true;
                    }

                    team.getTraps().remove(slotId);

                    sender.sendRichMessage("<green>Trap has been removed!");
                    return true;
                }
                case "reset" -> {
                    team.getTraps().clear();
                    sender.sendRichMessage("<green>Traps of team " + team.getId() + " have been reset!");
                    return true;
                }
                default -> {
                    sender.sendRichMessage("<red>Unknown subcommand");
                    return true;
                }
            }

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument: " + e.getMessage());
            return true;
        }

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> GameTeamsSubcommand.completeTeamIds(this.game.getTeams().size());
            case 2 -> List.of("list", "get", "add", "remove", "reset");
            default -> List.of();
        };
    }

}
