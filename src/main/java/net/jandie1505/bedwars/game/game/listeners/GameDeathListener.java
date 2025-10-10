package net.jandie1505.bedwars.game.game.listeners;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.constants.GameConfigKeys;
import net.jandie1505.bedwars.game.game.entities.entities.BaseDefender;
import net.jandie1505.bedwars.game.game.entities.entities.EndgameWither;
import net.jandie1505.bedwars.game.game.entities.base.ManagedEntity;
import net.jandie1505.bedwars.game.game.generators.Generator;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class GameDeathListener implements ManagedListener {
    @NotNull private final Game game;

    public GameDeathListener(@NotNull Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        PlayerData playerData = this.game.getPlayerData(event.getEntity());
        if (playerData == null) return;

        // Set dead
        playerData.setAlive(false);

        // Death Message
        event.setDeathMessage(this.getDeathMessage(event));

        // Increase deaths count
        playerData.setDeaths(playerData.getDeaths() + 1);

        // Get killer
        @Nullable Player killer = event.getEntity().getKiller();
        @Nullable PlayerData killerData = killer != null ? this.game.getPlayerData(killer) : null;

        // Item drops
        this.handleItemDrops(event.getDrops(), killer);

        // Give items + increase kill count or drop items
        this.rewardKiller(killerData);

        // Decrease upgrades
        this.decreaseUpgrades(playerData);

        // Immediate respawn
        this.respawnPlayerImmediately(event.getPlayer());
    }

    // ----- UTILITIES -----

    private void handleItemDrops(@NotNull List<ItemStack> drops, @Nullable Player killer) {

        // Clears all items that should be not dropped from the item drops
        for (ItemStack item : List.copyOf(drops)) {

            boolean canItemDrop = false;
            for (Generator generator : this.game.getGenerators()) {

                if (generator.getItem().isSimilar(item)) {
                    canItemDrop = true;
                    break;
                }

            }

            if (!canItemDrop) {
                drops.remove(item);
            }

        }

        // Give items to the killer when they exist, else drop the items
        if (killer != null) {

            for (ItemStack item : drops) {
                killer.getInventory().addItem(item);
                killer.sendMessage("§7+ " + item.getAmount() + " " + item.getType());
            }

            drops.clear(); // Clear item drops since they are now given to the killer

        }

    }

    private void rewardKiller(@Nullable PlayerData killerData) {
        if (killerData == null) return;
        killerData.setKills(killerData.getKills() + 1);
        killerData.setRewardPoints(killerData.getRewardPoints() + this.game.getConfig().optInt(GameConfigKeys.REWARD_PLAYER_KILL, 0));
    }

    private void decreaseUpgrades(@NotNull PlayerData playerData) {

        if (playerData.getPickaxeUpgrade() > 1) {
            playerData.setPickaxeUpgrade(playerData.getPickaxeUpgrade() - 1);
        }

        if (playerData.getShearsUpgrade() > 1) {
            playerData.setShearsUpgrade(playerData.getShearsUpgrade() - 1);
        }

    }

    private void respawnPlayerImmediately(@NotNull Player player) {
        if (Boolean.TRUE.equals(this.getGame().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) return;
        this.getGame().getTaskScheduler().runTaskLater(() -> player.spigot().respawn(), 1);
    }

    // ----- DEATH MESSAGES -----

    private String getDeathMessage(PlayerDeathEvent event) {

        PlayerData playerData = this.game.getPlayers().get(event.getEntity().getUniqueId());

        if (playerData == null) {
            return "";
        }

        BedwarsTeam team = this.game.getTeam(playerData.getTeam());

        if (team == null) {
            return "";
        }

        String deathMessage = Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(team.getChatColor()), ChatColor.BLACK) + event.getEntity().getDisplayName() + "§7 ";

        if (event.getEntity().getLastDamageCause() == null) {
            return deathMessage + "died";
        }

        if (event.getEntity().getKiller() != null) {

            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player) {
                    deathMessage = deathMessage + "lost in close combat against";
                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Projectile) {

                    if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Arrow || ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof SpectralArrow) {
                        deathMessage = deathMessage + "was defeated in bow fight by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Fireball) {
                        deathMessage = deathMessage + "was blown into a thousand pieces by a fireball, thrown by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof ThrownPotion) {
                        deathMessage = deathMessage + "was killed by magic by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Trident) {
                        deathMessage = deathMessage + "was impaled on a trident by";
                    } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Snowball) {
                        deathMessage = deathMessage + "lost the snowball fight against";
                    } else {
                        deathMessage = deathMessage + "was killed by";
                    }

                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof TNTPrimed) {
                    deathMessage = deathMessage + "was blown away by";
                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof IronGolem) {

                    deathMessage = deathMessage + getIronGolemDeathMessage((IronGolem) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager(), deathMessage);
                    deathMessage = deathMessage + " §7while running away from";

                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Wither) {

                    deathMessage = deathMessage + getEndgameWitherDeathMessage((Wither) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager(), deathMessage);
                    deathMessage = deathMessage + " §7while running away from";

                } else {
                    deathMessage = deathMessage + "died in combat with";
                }

            } else {

                switch (event.getEntity().getLastDamageCause().getCause()) {
                    case DROWNING:
                        deathMessage = deathMessage + "would have been better not to hide in the water from";
                        break;
                    case FALL:
                        deathMessage = deathMessage + "was knocked off a hill by";
                        break;
                    case FREEZE:
                        deathMessage = deathMessage + "wanted a cool down in fight with";
                        break;
                    case LAVA:
                        deathMessage = deathMessage + "took a hot bath, gifted by";
                        break;
                    case VOID:
                        deathMessage = deathMessage + "was knocked into the void by";
                        break;
                    case SUFFOCATION:
                        deathMessage = deathMessage + "was strangled by";
                        break;
                    case FIRE_TICK:
                        deathMessage = deathMessage + "burned to death while in combat with";
                        break;
                    case FIRE:
                        deathMessage = deathMessage + "was burned to death by";
                        break;
                    case ENTITY_EXPLOSION:
                        deathMessage = deathMessage + "exploded in combat with";
                        break;
                    default:
                        deathMessage = deathMessage + "died in combat with";
                        break;
                }

            }

            PlayerData killerData = this.game.getPlayers().get(event.getEntity().getKiller().getUniqueId());

            if (killerData == null) {
                return deathMessage;
            }

            BedwarsTeam killerTeam = this.game.getTeam(killerData.getTeam());

            if (killerTeam == null) {
                return deathMessage;
            }

            deathMessage = deathMessage + " " + Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(killerTeam.getChatColor()), ChatColor.BLACK) + event.getEntity().getKiller().getDisplayName();

            return deathMessage;
        } else {

            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof IronGolem) {

                    deathMessage = deathMessage + getIronGolemDeathMessage((IronGolem) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager(), deathMessage);

                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Wither) {

                    deathMessage = deathMessage + getEndgameWitherDeathMessage((Wither) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager(), deathMessage);

                } else {

                    deathMessage = deathMessage + "died";

                }

            } else {

                switch (event.getEntity().getLastDamageCause().getCause()) {
                    case DROWNING:
                        deathMessage = deathMessage + "could not surface in time before running out of air";
                    case ENTITY_EXPLOSION:
                        deathMessage = deathMessage + "became a suicide bomber";
                        break;
                    case FIRE_TICK:
                        deathMessage = deathMessage + "has not found any water to extinguish";
                        break;
                    case SUFFOCATION:
                        deathMessage = deathMessage + "had to make the experience that you need air to breathe";
                        break;
                    case MAGIC:
                        deathMessage = deathMessage + "has felt the painful side of magic";
                        break;
                    case LAVA:
                        deathMessage = deathMessage + "wanted to take a hot bath";
                        break;
                    case FIRE:
                        deathMessage = deathMessage + "has learned that fire is hot";
                        break;
                    case FALL:
                        deathMessage = deathMessage + "should have looked down better before jumping";
                        break;
                    case VOID:
                        deathMessage = deathMessage + "has found a hole";
                        break;
                    default:
                        deathMessage = deathMessage + "died";
                        break;
                }

            }

            return deathMessage;
        }
    }

    private String getIronGolemDeathMessage(IronGolem ironGolem, String deathMessage) {
        ManagedEntity<?> managedEntity = this.game.getManagedEntityByEntity(ironGolem);
        if (!(managedEntity instanceof BaseDefender baseDefender)) return "died";

        BedwarsTeam baseDefenderTeam = this.game.getTeam(baseDefender.getTeamId());

        if (baseDefenderTeam != null) {
            return "has experienced the BaseDefender of " + Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(baseDefenderTeam.getChatColor()), ChatColor.BLACK) + "Team " + baseDefenderTeam.getName();
        } else {
            return  "was killed by a BaseDefender";
        }
    }

    private String getEndgameWitherDeathMessage(Wither wither, String deathMessage) {
        ManagedEntity<?> managedEntity = this.game.getManagedEntityByEntity(wither);
        if (!(managedEntity instanceof EndgameWither endgameWither)) return "died";

        BedwarsTeam baseDefenderTeam = this.game.getTeam(endgameWither.getTeamId());

        if (baseDefenderTeam != null) {
            return "has experienced the Endgame Wither of " + Objects.requireNonNullElse(ChatCompatibilityUtils.getChatColorFromTextColor(baseDefenderTeam.getChatColor()), ChatColor.BLACK) + "Team " + baseDefenderTeam.getName();
        } else {
            return  "was killed by a Endgame Wither";
        }
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    public @NotNull Game getGame() {
        return this.game;
    }

}
