package net.jandie1505.bedwars.game.listeners;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.ManagedListener;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class GameMiscListener implements ManagedListener {
    @NotNull private final Game game;

    public GameMiscListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onBlockBreakForBreakingBed(@NotNull BlockBreakEvent event) {

        PlayerData playerData = this.game.getPlayer(event.getPlayer().getUniqueId());
        if (playerData == null) return;

        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Bed bed)) return;

        Block otherHalf;

        if (bed.getPart() == Bed.Part.HEAD) {
            otherHalf = event.getBlock().getRelative(bed.getFacing().getOppositeFace());
        } else {
            otherHalf = event.getBlock().getRelative(bed.getFacing());
        }

        // Protect own bed(s)
        for (Location location : this.game.getTeams().get(playerData.getTeam()).getData().bedLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList()) {

            if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                event.getPlayer().sendMessage("§cYou cannot break your own bed");
                event.setCancelled(true);
                return;
            }

        }

        // Send bed destroyed message
        for (BedwarsTeam team : this.game.getTeams()) {

            for (Location location : team.getData().bedLocations().stream().map(immutableLocation -> WorldUtils.locationWithWorld(immutableLocation, this.game.getWorld())).toList()) {

                if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                    playerData.setBedsBroken(playerData.getBedsBroken() + 1);
                    playerData.setRewardPoints(playerData.getRewardPoints() + this.game.getPlugin().getConfigManager().getConfig().optJSONObject("rewards", new JSONObject()).optInt("bedDestroyed", 0));

                    for (Player player : this.game.getPlugin().getServer().getOnlinePlayers()) {

                        PlayerData pData = this.game.getPlayers().get(player.getUniqueId());

                        BedwarsTeam destroyerTeam = this.game.getTeams().get(playerData.getTeam());

                        if (pData != null && pData.getTeam() == team.getId()) {
                            player.sendMessage("§7Your Bed was destroyed by " + destroyerTeam.getData().chatColor() + event.getPlayer().getName() + "§7!");
                            player.sendTitle("§cBED DESTROYED", "§7You will no longer respawn!", 5, 3*20, 5);
                            player.playSound(player, Sound.ENTITY_WITHER_DEATH, 1, 1);
                        } else {
                            player.sendMessage("§7The Bed of " + team.getData().chatColor() + "Team " + team.getData().chatColor().name() + " §7was destroyed by " + destroyerTeam.getData().chatColor() + event.getPlayer().getName() + "§7!");
                            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                        }

                    }

                }

            }

        }

    }

    // ----- OTHER -----

    @Override
    public @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
