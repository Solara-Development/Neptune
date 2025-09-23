package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public void reset(Player player) {
        player.setSaturation(20.0F);
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setFlying(false);

        Profile profile = API.getProfile(player);
        if (profile.getState().equals(ProfileState.IN_LOBBY)
                || profile.getState().equals(ProfileState.IN_KIT_EDITOR)
                || profile.getState().equals(ProfileState.IN_PARTY)
                || profile.getState().equals(ProfileState.IN_QUEUE)) {
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }

        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
        player.resetTitle();
        player.setMaxHealth(20.0f);
        player.setHealth(20.0D);
        player.setCooldown(Material.ENDER_PEARL, 0);
        resetActionbar(player);
    }

    public void resetActionbar(Player player) {
        player.sendActionBar(" ");
    }

    public void teleportToSpawn(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        Location spawn = Neptune.get().getCache().getSpawn();
        if (spawn == null) {
            player.sendMessage(CC.error("Make sure to set spawn location using /neptune setspawn!"));
            return;
        }
        player.teleport(spawn);
        for (Player spawnPlayer : spawn.getWorld().getPlayers()) {
            if (spawnPlayer.equals(player)) {
                continue;
            }
            spawnPlayer.showPlayer(Neptune.get(), player);
            player.showPlayer(Neptune.get(), spawnPlayer);
        }
    }

    public int getPing(Player player) {
        return player.getPing();
    }

    public int getPing(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            throw new RuntimeException("Player UUID isn't valid");
        return player.getPing();
    }

    public ItemStack getPlayerHead(UUID playerUUID) {
        return getPlayerHead(playerUUID, 1);
    }

    public ItemStack getPlayerHead(UUID playerUUID, int amount) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(Bukkit.getPlayer(playerUUID));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void sendMessage(UUID playerUUID, Component message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        player.sendMessage(message);
    }

    public void sendMessage(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        player.sendMessage(CC.color(message));
    }

    public void sendTitle(Player player, TextComponent header, TextComponent footer, int duration) {
        if (header.content().equalsIgnoreCase("NONE") && footer.content().equalsIgnoreCase("NONE"))
            return;
        player.showTitle(Title.title(header, footer,
                Title.Times.times(Duration.ofMillis(1000), Duration.ofMillis(duration * 50L), Duration.ofMillis(500))));
    }

    public int getMaxDuration(Player player, PotionEffectType type) {
        if (player.hasMetadata("max_duration_" + type.getName())) {
            return player.getMetadata("max_duration_" + type.getName()).get(0).asInt();
        }
        return 0;
    }

    public void doVelocityChange(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        player.setVelocity(player.getVelocity().add(new Vector(0, 0.25, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(player.getVelocity().add(new Vector(0, 0.15, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
    }
}
