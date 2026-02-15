package dev.lrxh.neptune.providers.listeners;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
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
    public void onCraftItem(CraftItemEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING) {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            Profile profile = API.getProfile(player);
            if (isPlayerNotInMatch(profile) && profile.getState() != ProfileState.IN_CUSTOM) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target) || event.getHand() != EquipmentSlot.HAND)
            return;
        Player sender = event.getPlayer();
        if (sender.isSneaking()) return;
        Profile senderProfile = API.getProfile(sender);
        Party party = senderProfile.getGameData().getParty();
        if (party == null || !party.isLeader(sender.getUniqueId()))
            return;
        if (senderProfile.getPartyInviteTarget() == target) {
            senderProfile.setPartyInviteTarget(null);
            sender.chat("/party invite " + target.getName());
        } else {
            senderProfile.setPartyInviteTarget(target);
            MessagesLocale.PARTY_INVITE_CONFIRM.send(sender, Placeholder.unparsed("player", target.getName()));
            TaskScheduler.get().startTaskLater(new NeptuneRunnable() {
                @Override
                public void run() {
                    if (senderProfile.getPartyInviteTarget() == target) {
                        senderProfile.setPartyInviteTarget(null);
                    }
                }
            }, 200L);
        }
    }

    @EventHandler
    public void onShiftRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (API.getProfile(player).getMatch() != null) return;
        if (!player.isSneaking()) return;
        if (event.getRightClicked() instanceof Player clicked && event.getHand() == EquipmentSlot.HAND) {
            player.chat("/duel " + clicked.getName());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (profile.getState() != ProfileState.IN_LOBBY) return;
        Location spawn = Neptune.get().getCache().getSpawn();
        if (spawn == null) return;
        if (spawn.getWorld() != player.getWorld()) return;
        if (player.getLocation().getY() <= SettingsLocale.VOID_Y_LOCATION.getInt()) {
            player.teleport(spawn);
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
        if (profile == null) return;
        if (profile.getState().equals(ProfileState.IN_CUSTOM)) return;
        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) event.setCancelled(true);
        if (isPlayerNotInMatch(profile)) {
            event.setCancelled(true);
            MessagesLocale.CANT_DO_THIS_NOW.send(player);
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
            MessagesLocale.CANT_DO_THIS_NOW.send(player);
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
        if (!(event.getEntity() instanceof Player player)) return;
        Profile profile = API.getProfile(player);

        if (profile == null) {
            return;
        }

        if (profile.getState().equals(ProfileState.IN_CUSTOM)) {
            return;
        }

        if (isPlayerNotInMatch(profile)) event.setCancelled(true);
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