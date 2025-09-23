package dev.lrxh.neptune.game.match.listener;

import dev.lrxh.api.events.MatchParticipantDeathEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.EntityUtils;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.WorldUtils;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MatchListener implements Listener {
    private final NamespacedKey explosiveOwnerKey;

    public MatchListener() {
        this.explosiveOwnerKey = new NamespacedKey(Neptune.get(), "neptune_explosive_owner");
    }

    private boolean isPlayerInMatch(Profile profile) {
        if (profile == null)
            return false;
        return profile.getState().equals(ProfileState.IN_GAME) && profile.getMatch() != null;
    }

    private Optional<Profile> getProfile(Player player) {
        Profile profile = API.getProfile(player);
        return isPlayerInMatch(profile) ? Optional.of(profile) : Optional.empty();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof EnderCrystal crystal) {
            Player player = getNearbyPlayer(crystal.getLocation());
            if (player == null)
                return;

            getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(crystal));
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        Profile profile = API.getProfile(player);
        if (profile == null)
            return;

        if (profile.getState() == ProfileState.IN_CUSTOM)
            return;

        // Cancel if not in match
        if (!isPlayerInMatch(profile)) {
            event.setCancelled(true);
            return;
        }

        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();

        if (match != null && match.getKit().is(KitRule.BUILD)) {
            if (match.getState().equals(MatchState.STARTING)) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou can't place blocks yet!"));
                return;
            }

            Arena arena = match.getArena();

            // Check height limit
            if (blockLocation.getY() >= arena.getBuildLimit()) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou have reached build limit!"));
                return;
            }

            // Check arena boundaries
            if (!LocationUtil.isInside(blockLocation, arena.getMin(), arena.getMax())) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou can't build outside the arena!"));
                return;
            }

            if (blockLocation.equals(match.getArena().getRedSpawn())
                    || blockLocation.equals(match.getArena().getRedSpawn().clone().add(0, 1, 0))) {
                event.setCancelled(true);
                return;
            }

            if (blockLocation.equals(match.getArena().getBlueSpawn())
                    || blockLocation.equals(match.getArena().getBlueSpawn().clone().add(0, 1, 0))) {
                event.setCancelled(true);
                return;
            }

            if (event.getBlock().getType() == Material.TNT &&
                    match.getKit().getRules().get(KitRule.AUTO_IGNITE)) {
                event.setCancelled(true);

                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem.getType() == Material.TNT) {
                    handItem.setAmount(handItem.getAmount() - 1);
                }

                TNTPrimed tnt = (TNTPrimed) event.getPlayer().getWorld().spawnEntity(
                        event.getBlockPlaced().getLocation().add(0.5, 0.5, 0.5),
                        EntityType.TNT);
                tnt.setFuseTicks(20);
                tnt.getPersistentDataContainer().set(
                        explosiveOwnerKey,
                        PersistentDataType.STRING,
                        event.getPlayer().getUniqueId().toString());

                match.getEntities().add(tnt);
            }

            match.getPlacedBlocks().add(blockLocation);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreeperSpawn(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getItem() == null || event.getItem().getType() != Material.CREEPER_SPAWN_EGG)
            return;
        Optional<Profile> profileOpt = getProfile(event.getPlayer());
        if (profileOpt.isEmpty())
            return;
        if (!profileOpt.get().getMatch().getKit().getRules().get(KitRule.AUTO_IGNITE))
            return;
        Location spawnLocation = event.getInteractionPoint();
        Creeper creeper = (Creeper) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.CREEPER,
                CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        creeper.ignite();
        creeper.getPersistentDataContainer().set(
                explosiveOwnerKey,
                PersistentDataType.STRING,
                event.getPlayer().getUniqueId().toString());
        profileOpt.get().getMatch().getEntities().add(creeper);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        if (API.getProfile(event.getDamager().getUniqueId()).getState().equals(ProfileState.IN_CUSTOM))
            return;
        if (event.getEntity() instanceof EnderCrystal crystal && event.getDamager() instanceof Player player) {
            if (!getProfile(player).isPresent()) {
                event.setCancelled(true);
                return;
            }

            crystal.getPersistentDataContainer().set(
                    explosiveOwnerKey,
                    PersistentDataType.STRING,
                    player.getUniqueId().toString());
        }

        if (event.getEntity() instanceof Creeper creeper && event.getDamager() instanceof Player player) {
            if (!getProfile(player).isPresent()) {
                event.setCancelled(true);
                return;
            }
            creeper.getPersistentDataContainer().set(
                    explosiveOwnerKey,
                    PersistentDataType.STRING,
                    player.getUniqueId().toString());
        }
        if (event.getEntity() instanceof TNTPrimed tnt && event.getDamager() instanceof Player player) {
            if (!getProfile(player).isPresent()) {
                event.setCancelled(true);
                return;
            }
            tnt.getPersistentDataContainer().set(
                    explosiveOwnerKey,
                    PersistentDataType.STRING,
                    player.getUniqueId().toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EnderCrystal) && !(entity instanceof Creeper) && !(entity instanceof TNTPrimed))
            return;
        String uuid = entity.getPersistentDataContainer().get(
                explosiveOwnerKey,
                PersistentDataType.STRING);

        if (uuid == null || uuid.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        Player player;
        try {
            player = Bukkit.getPlayer(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            Arena arena = match.getArena();
            List<Block> originalBlocks = new ArrayList<>(event.blockList());
            List<Block> allowedBlocks = originalBlocks.stream()
                    .filter(block -> arena.getWhitelistedBlocks().contains(block.getType()))
                    .toList();
            event.blockList().clear();
            event.blockList().addAll(allowedBlocks);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFriendlyFire(EntityDamageByEntityEvent event) {
        if (SettingsLocale.FRIENDLY_FIRE.getBoolean())
            return;
        if (!(event.getEntity() instanceof Player victim))
            return;
        Player owner = getResponsiblePlayer(event);

        if (owner == null)
            return;

        Optional<Match> ownerMatchOptional = getMatchForPlayer(owner);
        Optional<Match> victimMatchOptional = getMatchForPlayer(victim);

        if (ownerMatchOptional.isEmpty() || victimMatchOptional.isEmpty()) {
            return;
        }

        Match match = ownerMatchOptional.get();
        if (!match.getUuid().equals(victimMatchOptional.get().getUuid()))
            return;

        if (!(match instanceof TeamFightMatch teamMatch)
                || !(teamMatch.onSameTeam(owner.getUniqueId(), victim.getUniqueId())))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEnderPearlUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().isRightClick())
            return;
        if (event.getHand() == null)
            return;
        if (player.getInventory().getItem(event.getHand()).getType() != Material.ENDER_PEARL)
            return;

        Optional<Profile> profileOpt = getProfile(player);
        if (ProfileService.get().getByUUID(player.getUniqueId()).getState().equals(ProfileState.IN_CUSTOM)) {
            return;
        }
        if (!profileOpt.isPresent()) {
            event.setCancelled(true);
            return;
        }

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();

        if (match.getState().equals(MatchState.STARTING)) {
            event.setCancelled(true);
        }

        if (!match.getKit().is(KitRule.ENDERPEARL_COOLDOWN))
            return;
        Participant participant = match.getParticipant(player);

        if (player.hasCooldown(Material.ENDER_PEARL)) {
            int ticksLeft = player.getCooldown(Material.ENDER_PEARL);
            if (ticksLeft > 0) {
                double secondsLeft = ticksLeft / 20.0;
                participant.sendMessage(MessagesLocale.MATCH_ENDERPEARL_COOLDOWN_ON_GOING,
                        new Replacement("<time>", String.valueOf(secondsLeft)));
            }
        } else {
            new NeptuneRunnable() {
                @Override
                public void run() {
                    player.setCooldown(Material.ENDER_PEARL, 15 * 20);
                }
            }.startLater(1L);
        }
    }

    private boolean isSpectator(Player player) {
        Profile profile = API.getProfile(player);
        return profile != null && profile.getState() == ProfileState.IN_SPECTATOR;
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player && isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player player && isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPush(EntityPushedByEntityAttackEvent event) {
        if (!(event.getPushedBy() instanceof WindCharge wc))
            return;

        Entity pushed = event.getEntity();

        if (!(wc.getShooter() instanceof Player shooter)) {
            event.setCancelled(true);
            return;
        }
        if (!(pushed instanceof Player target)) {
            return;
        }

        // Check if both players are in matches
        if (!getProfile(shooter).isPresent() || !getProfile(target).isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (!target.canSee(shooter) || !shooter.canSee(target)) {
            event.setCancelled(true);
        }

        if (event.getPushedBy() instanceof Player attacker) {
            ItemStack item = attacker.getInventory().getItemInMainHand();
            if (item.getType() == Material.MACE) {
                if (!(event.getEntity() instanceof Player victim))
                    return;
                if (!getProfile(attacker).isPresent() || !getProfile(victim).isPresent()) {
                    event.setCancelled(true);
                    return;
                }

                if (!victim.canSee(attacker) || !attacker.canSee(victim)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.deathMessage(null);
        event.getDrops().clear();

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent())
            return;

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();
        Participant participant = match.getParticipant(player.getUniqueId());
        if (participant == null)
            return;

        participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
        MatchParticipantDeathEvent deathEvent = new MatchParticipantDeathEvent(match, participant,
                participant.getDeathCause().getMessage().getString());
        Bukkit.getPluginManager().callEvent(deathEvent);
        match.onDeath(participant);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player) {
            Profile profile = API.getProfile(attacker);
            if (profile == null)
                return;

            if (profile.getState().equals(ProfileState.IN_CUSTOM)) {
                return;
            }

            // Check if attacker is in match
            if (!isPlayerInMatch(profile)) {
                event.setCancelled(true);
                return;
            }

            Match match = profile.getMatch();

            if (match != null) {
                if (match.getKit().is(KitRule.PARKOUR)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.PHYSICAL) {
            Material blockType = event.getClickedBlock() != null ? event.getClickedBlock().getType() : null;
            if (blockType == null)
                return;

            Optional<Profile> profileOpt = getProfile(player);
            if (!profileOpt.isPresent())
                return;

            Profile profile = profileOpt.get();
            Match match = profile.getMatch();
            if (!match.getKit().is(KitRule.PARKOUR))
                return;
            if (!match.getState().equals(MatchState.IN_ROUND))
                return;
            Participant participant = match.getParticipant(player);
            if (participant == null)
                return;

            if (blockType.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                if (participant.setCurrentCheckPoint(event.getClickedBlock().getLocation().clone().add(0, 1, 0))) {
                    match.broadcast(MessagesLocale.PARKOUR_CHECKPOINT,
                            new Replacement("<player>", participant.getNameColored()),
                            new Replacement("<checkpoint>", String.valueOf(participant.getCheckPoint())),
                            new Replacement("<time>", participant.getTime().formatSecondsMillis()));
                }
            } else if (blockType.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                match.win(participant);
                match.broadcast(MessagesLocale.PARKOUR_END,
                        new Replacement("<player>", participant.getNameColored()),
                        new Replacement("<time>", participant.getTime().formatSecondsMillis()));
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE))
                return;
            Profile profile = API.getProfile(player);
            if (profile == null)
                return;

            if (!isPlayerInMatch(profile)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent()) {
            event.setCancelled(true);
            return;
        }
        Profile profile = profileOpt.get();

        if (profile != null && profile.getState() != null && profile.getState().equals(ProfileState.IN_CUSTOM))
            return;

        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();

        if (match != null && match.getKit().is(KitRule.BUILD)) {
            if (match.getState().equals(MatchState.STARTING)) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou can't place blocks yet!"));
                return;
            }

            Arena arena = match.getArena();

            if (blockLocation.getY() >= arena.getBuildLimit()) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou have reached build limit!"));
                return;
            }

            if (!LocationUtil.isInside(blockLocation, arena.getMin(), arena.getMax())) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou can't place water outside the arena!"));
                return;
            }

            match.getPlacedBlocks().add(blockLocation);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player player) {
            Profile attackerProfile = API.getProfile(attacker.getUniqueId());
            Profile profile = API.getProfile(player);
            if (profile == null)
                return;

            if (profile.getState().equals(ProfileState.IN_CUSTOM)) {
                return;
            }

            // Cancel if either player is not in match
            if (!isPlayerInMatch(profile) || !isPlayerInMatch(attackerProfile)) {
                event.setCancelled(true);
                return;
            }

            Match match = profile.getMatch();

            if (!attackerProfile.getMatch().getUuid().equals(match.getUuid())) {
                event.setCancelled(true);
                return;
            }

            if (match.getParticipant(attacker).isDead()) {
                event.setCancelled(true);
            }

            if (match instanceof TeamFightMatch teamFightMatch) {
                if (teamFightMatch.onSameTeam(player.getUniqueId(), attacker.getUniqueId())) {
                    event.setCancelled(true);
                }
            }

            if (!match.getState().equals(MatchState.IN_ROUND)) {
                event.setCancelled(true);
            }

            match.getParticipant(player.getUniqueId()).setLastAttacker(match.getParticipant(attacker.getUniqueId()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHitEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target && event.getDamager() instanceof Player damager) {
            Profile targetProfile = API.getProfile(target);
            Profile playerProfile = API.getProfile(damager.getUniqueId());

            if (playerProfile.getState().equals(ProfileState.IN_CUSTOM)) {
                return;
            }

            if (!isPlayerInMatch(targetProfile) || !isPlayerInMatch(playerProfile)) {
                event.setCancelled(true);
                return;
            }

            if (damager.getAttackCooldown() >= 0.2) {
                Match match = targetProfile.getMatch();
                Participant opponent = match.getParticipant(target.getUniqueId());

                if (damager.getAttackCooldown() > 0.7)
                    match.getParticipant(damager.getUniqueId()).handleHit(opponent);
                opponent.resetCombo();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Optional<Profile> profileOpt = getProfile(player);

        if (!profileOpt.isPresent())
            return;

        if (!(event.getFinalDamage() >= player.getHealth()))
            return;
        if (player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ||
                player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))
            return;

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();
        Participant participant = match.getParticipant(player.getUniqueId());
        participant.setDeathCause(DeathCause.DIED);
        match.onDeath(participant);
        player.setHealth(20.0f);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!event.hasChangedPosition())
            return;
        Player player = event.getPlayer();

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent())
            return;

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();
        Arena arena = match.getArena();

        Participant participant = match.getParticipant(player.getUniqueId());
        if (participant == null)
            return;
        if (participant.isFrozen()) {
            Location to = event.getTo();
            Location from = event.getFrom();
            if ((to.getX() != from.getX() || to.getZ() != from.getZ())) {
                player.teleport(from);
                return;
            }
        }

        if (player.getLocation().getY() <= arena.getDeathY() && !participant.isDead()) {
            if (match.getKit().is(KitRule.PARKOUR)) {
                player.teleport(participant.getSpawn(match));
            } else {
                participant.setDeathCause(DeathCause.DIED);
                match.onDeath(participant);
            }
            return;
        }
        if (match.getState().equals(MatchState.IN_ROUND)) {
            Location playerLocation = player.getLocation();

            if (match.getKit().is(KitRule.DROPPER)) {
                Block block = playerLocation.getBlock();

                if (block.getType() == Material.WATER) {
                    match.win(participant);
                }

                return;
            }

            if (match.getKit().is(KitRule.SUMO)) {
                Block block = playerLocation.getBlock();

                if (block.getType() == Material.WATER) {
                    participant
                            .setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(participant);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null)
            return;

        Material blockType = clickedBlock.getType();

        if (isStrippable(blockType)) {
            // Only cancel if player is in match
            Player player = event.getPlayer();
            Optional<Profile> profileOpt = getProfile(player);
            if (profileOpt.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isStrippable(Material material) {
        return material == Material.OAK_LOG ||
                material == Material.SPRUCE_LOG ||
                material == Material.BIRCH_LOG ||
                material == Material.JUNGLE_LOG ||
                material == Material.ACACIA_LOG ||
                material == Material.DARK_OAK_LOG ||
                material == Material.MANGROVE_LOG ||
                material == Material.CHERRY_LOG ||
                material == Material.CRIMSON_STEM ||
                material == Material.COPPER_BLOCK ||
                material == Material.WARPED_STEM;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            ProfileService profileService = ProfileService.get();
            Profile profileDirect = profileService.getByUUID(player.getUniqueId());

            if (profileDirect == null) {
                event.setCancelled(true);
                return;
            }

            if (profileDirect.getState() == ProfileState.IN_CUSTOM) {
                return;
            }

            Optional<Profile> profileOpt = getProfile(player);
            if (!profileOpt.isPresent()) {
                event.setCancelled(true);
                return;
            }

            Profile profile = profileOpt.get();
            Match match = profile.getMatch();
            Kit kit = match.getKit();

            if (match.getState() == MatchState.STARTING || match.getState() == MatchState.ENDING) {
                event.setCancelled(true);
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (!kit.is(KitRule.FALL_DAMAGE)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (!kit.is(KitRule.DAMAGE)) {
                event.setDamage(0);
                return;
            }

            if (match.getParticipant(player).isDead()) {
                event.setCancelled(true);
                return;
            }

            if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                event.setDamage(event.getDamage() * kit.getDamageMultiplier());
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (API.getProfile(player) == null)
                return;
            if (API.getProfile(player).getState().equals(ProfileState.IN_CUSTOM))
                return;
            Optional<Profile> profileOpt = getProfile(player);
            if (!profileOpt.isPresent()) {
                event.setCancelled(true);
                return;
            }

            Profile profile = profileOpt.get();
            Match match = profile.getMatch();

            if (!match.getKit().is(KitRule.HUNGER)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Optional<Profile> profileOpt = getProfile(player);
            if (!profileOpt.isPresent())
                return;

            Profile profile = profileOpt.get();
            Match match = profile.getMatch();
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                if (!match.getKit().is(KitRule.SATURATION_HEAL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Profile playerProfile = API.getProfile(player);
        if (playerProfile != null && playerProfile.getState().equals(ProfileState.IN_CUSTOM)) return;

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent()) {
            event.setCancelled(true);
            return;
        }

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();
        Arena arena = match.getArena();

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            event.setCancelled(true);
            return;
        }

        Material blockType = event.getBlock().getType();
        Location blockLocation = event.getBlock().getLocation();

        if (blockType == Material.FIRE)
            return;
        if (blockType.name().contains("BED"))
            return;

        boolean cancel = true;

        if (match.getKit().is(KitRule.BUILD) && match.getPlacedBlocks().contains(blockLocation)) {
            cancel = false;
        } else if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
            if (arena.getWhitelistedBlocks().contains(blockType)) {
                cancel = false;
            }
        }

        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresentOrElse(match -> match.getEntities().addAll(event.getItems()),
                () -> event.setCancelled(true));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakBlockEvent event) {
        event.getDrops().clear();
    }

    @EventHandler()
    public void onBedBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Profile playerProfile = API.getProfile(player);
        if (playerProfile != null && playerProfile.getState().equals(ProfileState.IN_CUSTOM)) return;

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent()) {
            event.setCancelled(true);
            return;
        }

        Profile profile = profileOpt.get();
        Match match = profile.getMatch();
        Material blockType = event.getBlock().getType();

        if (!match.getKit().is(KitRule.BED_WARS))
            return;

        if (match.getKit().is(KitRule.BED_WARS)) {
            if (blockType == Material.OAK_PLANKS || blockType == Material.END_STONE) {
                event.setCancelled(false);
            }
        }

        if (match.getKit().is(KitRule.BED_WARS)) {
            if (event.getBlock().getType().toString().contains("BED")) {
                Location bed = event.getBlock().getLocation();

                Participant participant = match.getParticipant(player.getUniqueId());
                if (participant == null)
                    return;
                Location spawn = match.getSpawn(participant);
                Participant opponent = participant.getOpponent();
                Location opponentSpawn = match.getSpawn(opponent);
                if (bed.distanceSquared(spawn) > bed.distanceSquared(opponentSpawn)) {
                    event.setDropItems(false);
                    match.breakBed(opponent, participant);
                    match.sendTitle(opponent, CC.color(MessagesLocale.BED_BREAK_TITLE.getString()),
                            CC.color(MessagesLocale.BED_BREAK_FOOTER.getString()), 20);
                    match.broadcast(
                            opponent.getColor().equals(ParticipantColor.RED) ? MessagesLocale.RED_BED_BROKEN_MESSAGE
                                    : MessagesLocale.BLUE_BED_BROKEN_MESSAGE,
                            new Replacement("<player>", participant.getNameColored()));
                } else {
                    event.setCancelled(true);
                    participant.sendMessage(MessagesLocale.CANT_BREAK_OWN_BED);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (shooter instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE))
                return;

            Optional<Profile> profileOpt = getProfile(player);
            if (!profileOpt.isPresent()) {
                event.setCancelled(true);
            } else {
                getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(projectile));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Player player = getNearbyPlayer(event.getBlock().getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            Arena arena = match.getArena();
            List<Block> originalBlocks = new ArrayList<>(event.blockList());
            List<Block> allowedBlocks = originalBlocks.stream()
                    .filter(block -> arena.getWhitelistedBlocks().contains(block.getType()))
                    .toList();
            event.blockList().clear();
            event.blockList().addAll(allowedBlocks);
        });
    }

    @EventHandler
    public void onItemDrop(BlockBreakBlockEvent event) {
        Player player = getNearbyPlayer(event.getBlock().getLocation());
        if (player == null) {
            event.getDrops().clear();
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
            for (ItemStack item : event.getDrops()) {
                match.getEntities().add(EntityUtils.getEntityByItemStack(player.getWorld(), item));
            }
        }, 1));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Profile> profileOpt = getProfile(player);
        if (!profileOpt.isPresent()) {
            event.setCancelled(true);
        } else {
            Profile profile = profileOpt.get();
            profile.getMatch().getEntities().add(event.getItemDrop());
        }
    }

    private Player getResponsiblePlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Player p)
            return p;
        if (damager instanceof TNTPrimed tnt && tnt.getSource() instanceof Player source)
            return source;
        if (damager instanceof Projectile proj && proj.getShooter() instanceof Player shooter)
            return shooter;
        if (damager instanceof ThrownPotion potion && potion.getShooter() instanceof Player thrower)
            return thrower;
        if (damager instanceof AreaEffectCloud cloud && cloud.getSource() instanceof Player source)
            return source;
        if (damager instanceof EnderCrystal crystal) {
            String uuid = crystal.getPersistentDataContainer().get(
                    explosiveOwnerKey,
                    PersistentDataType.STRING);
            if (uuid != null) {
                return Bukkit.getPlayer(UUID.fromString(uuid));
            }
        }
        return null;
    }

    private Player getNearbyPlayer(Location location) {
        return WorldUtils.getPlayersInRadius(location, 10)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Optional<Match> getMatchForPlayer(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .filter(this::isPlayerInMatch)
                .map(Profile::getMatch);
    }
}