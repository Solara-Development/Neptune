package dev.lrxh.neptune.providers.listeners;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;

public class GlobalListener implements Listener {

    private boolean isPlayerNotInMatch(Profile profile) {
        if (profile == null) return true;
        ProfileState state = profile.getState();
        return !state.equals(ProfileState.IN_GAME) && !state.equals(ProfileState.IN_SPECTATOR) || profile.getMatch() == null;
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) || event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUCKET)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onShiftRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Player clicked) {
            if (!player.isSneaking()) return;

            player.chat("/duel " + clicked.getName());
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMoistureChange(MoistureChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSoilChange(PlayerInteractEvent event) {
        Profile profile = API.getProfile(event.getPlayer());
        if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
        if (event.getAction() == Action.PHYSICAL && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.FARMLAND)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (isPlayerNotInMatch(profile) && profile.getState() != ProfileState.IN_CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Profile profile = API.getProfile(player);
        if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) event.setCancelled(true);
        if (isPlayerNotInMatch(profile)) {
            event.setCancelled(true);
            if (profile != null) {
                ProfileState state = profile.getState();
                if (state.equals(ProfileState.IN_KIT_EDITOR)) {
                    player.sendMessage(CC.color("&cYou can't break blocks in the kit editor!"));
                } else if (state.equals(ProfileState.IN_QUEUE)) {
                    player.sendMessage(CC.color("&cYou can't break blocks while in queue!"));
                } else {
                    player.sendMessage(CC.color("&cYou can't break blocks here!"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
        if (isPlayerNotInMatch(profile)) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou can't place liquids here!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItemDrop().getItemStack().getType().equals(Material.GLASS_BOTTLE)) {
            event.getItemDrop().remove();
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
        if (isPlayerNotInMatch(profile)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            Profile profile = API.getProfile(player);
            if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (isPlayerNotInMatch(profile)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            Profile attackerProfile = API.getProfile(attacker);
            Profile victimProfile = API.getProfile(victim);
            if (attackerProfile != null && attackerProfile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (victimProfile != null && victimProfile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (isPlayerNotInMatch(attackerProfile) || isPlayerNotInMatch(victimProfile)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            if (profile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (isPlayerNotInMatch(profile)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);
            if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (isPlayerNotInMatch(profile) && profile.getState() != ProfileState.IN_CUSTOM) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            Profile profile = API.getProfile(player);
            if (profile != null && profile.getState().equals(ProfileState.IN_CUSTOM)) return;
            if (isPlayerNotInMatch(profile)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (API.getProfile(player) != null && API.getProfile(player).getState().equals(ProfileState.IN_CUSTOM)) return;
        if (event.getAction() == EntityPotionEffectEvent.Action.ADDED) {
            PotionEffect newEffect = event.getNewEffect();
            if (newEffect != null) {
                player.setMetadata("max_duration_" + newEffect.getType().getName(), new FixedMetadataValue(Neptune.get(), newEffect.getDuration()));
            }
        }
        if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED ||
                event.getAction() == EntityPotionEffectEvent.Action.CLEARED) {
            PotionEffect oldEffect = event.getOldEffect();
            if (oldEffect != null) {
                player.removeMetadata("max_duration_" + oldEffect.getType().getName(), Neptune.get());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        event.setRespawnLocation(player.getLocation());
    }
}