package net.jandie1505.bedwars.game.listeners;

import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.ManagedListener;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.entities.entities.SnowDefender;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SpecialItemListeners implements ManagedListener {
    @NotNull private final Game game;

    public SpecialItemListeners(@NotNull Game game) {
        this.game = game;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteractForIronGolem(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!itemCondition(event, this.game.getItemShop().getIronGolemSpawnEgg())) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayers().get(event.getPlayer().getUniqueId());
        if (playerData == null) return;

        if (playerData.getIronGolemCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getIronGolemCooldown() / 20.0) + " to place an iron golem again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());
        if (team == null) return;

        Location location = event.getInteractionPoint();
        if (location == null) return;
        location = location.clone();

        new BaseDefender(this.game, location, team.getId());

        playerData.setIronGolemCooldown(15*20);
    }

    @EventHandler
    public void onPlayerInteractForSnowDefender(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!itemCondition(event, this.game.getItemShop().getSnowDefenderSpawnEgg())) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayers().get(event.getPlayer().getUniqueId());

        if (playerData == null) {
            return;
        }

        if (playerData.getIronGolemCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getIronGolemCooldown() / 20.0) + " to place an snow golem again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());

        if (team == null) {
            return;
        }

        Location location = event.getInteractionPoint();
        if (location == null) return;
        location = location.clone();

        new SnowDefender(this.game, location, team.getId());

        playerData.setIronGolemCooldown(15*20);
    }

    @EventHandler
    public void onPlayerInteractForZapper(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!itemCondition(event, this.game.getItemShop().getZapper())) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayers().get(event.getPlayer().getUniqueId());

        if(playerData == null) {
            return;
        }

        if(playerData.getZapperCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou need to wait " + ((double) playerData.getZapperCooldown() / 20.0) + " to use the Zapper again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());
        if(team == null) return;

        playerData.setZapperCooldown(50*20);

        List<BedwarsTeam> teams = new ArrayList<>(this.game.getTeams());
        teams.remove(team);

        Random random = new Random();
        BedwarsTeam teamToZapp = teams.get(random.nextInt(teams.size()));

        List<UUID> playersToZapp = new ArrayList<UUID>(teamToZapp.getPlayers());
        UUID playerToZapp = playersToZapp.get(random.nextInt(playersToZapp.size()));
        Location zappLocation = Bukkit.getPlayer(playerToZapp).getLocation().clone();

        event.getPlayer().getWorld().spawnEntity(zappLocation, EntityType.LIGHTNING_BOLT);
    }

    @EventHandler
    public void onPlayerInteractForBlackHole(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (!itemCondition(event, this.game.getItemShop().getBlackHole())) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayers().get(event.getPlayer().getUniqueId());
        if(playerData == null) return;

        if(playerData.getBlackHoleCooldown() > 0) {
            event.getPlayer().sendMessage("§cYou have to wait " + ((double) playerData.getBlackHoleCooldown() / 20.0) + " to use a Black Hole again");
            return;
        }

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        playerData.setBlackHoleCooldown(15*20);

        Location center = event.getInteractionPoint();
        if (center == null) return;
        center = center.clone();

        for(int x = center.getBlockX() - 7; x <= center.getBlockX() + 7; x++) {
            for(int y = center.getBlockY() - 7; y <= center.getBlockY() + 7; y++) {
                for(int z = center.getBlockZ() - 7; z <= center.getBlockZ() + 7; z++) {
                    Block block = center.getWorld().getBlockAt(new Location(center.getWorld(), x, y, z));

                    if(block.getType() != Material.AIR) {
                        if(this.game.getPlayerPlacedBlocks().contains(block.getLocation())) {
                            String name = block.getType().name();
                            if(name.contains("WOOL") || name.contains("GLASS")) {
                                block.setType(Material.AIR);
                                this.game.getPlayerPlacedBlocks().remove(block.getLocation());
                            }
                        }
                    }
                }
            }
        }
    }

    // ----- UTILITIES -----

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean itemCondition(@NotNull PlayerInteractEvent event, @Nullable Integer itemId) {
        if (itemId == null) return false;

        ItemStack item = event.getItem();
        if (item == null) return false;

        int id = this.game.getPlugin().getItemStorage().getItemId(item);
        if (id < 0) return false;

        return itemId == id;
    }

    // ----- OTHER -----

    @Override
    public GamePart getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
