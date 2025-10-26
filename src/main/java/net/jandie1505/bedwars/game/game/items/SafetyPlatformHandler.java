package net.jandie1505.bedwars.game.game.items;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.CustomItemValues;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SafetyPlatformHandler implements ManagedListener {
    @NotNull private final Game game;

    public SafetyPlatformHandler(@NotNull Game game) {
        this.game = game;
        this.game.registerListener(this);
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onPlayerInteractForSafetyPlatform(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!CustomItemValues.isCustomItem(event.getItem(), CustomItemValues.SAFETY_PLATTFORM)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if(playerData == null) return;

        event.getItem().setAmount(event.getItem().getAmount() - 1);

        this.createSafetyPlatform(event.getPlayer(), playerData);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;
        if (!CustomItemValues.isCustomItem(event.getMainHandItem(), CustomItemValues.SAFETY_PLATTFORM)) return;

        event.setCancelled(true);

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        event.getMainHandItem().setAmount(event.getMainHandItem().getAmount() - 1);

        this.createSafetyPlatform(event.getPlayer(), playerData);
    }

    // ----- SPAWN PLATFORM -----

    private void createSafetyPlatform(Player player, PlayerData playerData) {

        @Nullable BedwarsTeam team = this.game.getTeams().get(playerData.getTeam());
        if (team == null) return;

        @Nullable String colorString = Bedwars.getBlockColorString(team.getChatColor());
        if (colorString == null) return;

        @Nullable Material material = Material.getMaterial(colorString + "_STAINED_GLASS");
        if (material == null) return;

        this.spawnSafetyPlatform(player, material);
    }

    private void spawnSafetyPlatform(@NotNull Player player, @NotNull Material material) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 3*20, 0, true, true));

        this.buildPlatform(this.getCenterLocation(player), material);

        this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "safety_platform", 40, Component.text("\uD83E\uDE82 Safety Platform deployed!", NamedTextColor.GREEN));
        player.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_GOLD, SoundCategory.PLAYERS, 1f, 0.75f);

    }

    // ----- PLATFORM BUILDER -----

    private void buildPlatform(@NotNull Location center, @NotNull Material material) {

        for (int x = center.getBlockX() - 1; x <= center.getBlockX() + 1; x++) {
            for (int z = center.getBlockZ() - 1; z <= center.getBlockZ() + 1; z++) {
                Block block = center.getWorld().getBlockAt(new Location(center.getWorld(), x, center.getBlockY(), z));

                if (block.getType() == Material.AIR) {
                    block.setType(material);
                    this.game.getBlockProtectionSystem().getPlayerPlacedBlocks().add(block.getLocation().toVector());
                }

            }
        }

    }

    // ----- TOOLS -----

    private @NotNull Location getCenterLocation(@NotNull Player player) {
        Location center = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        center.add(0, -2, 0);
        return center;
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
