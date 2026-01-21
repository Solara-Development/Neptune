package dev.lrxh.neptune.feature.hotbar.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.hotbar.impl.CustomItem;
import dev.lrxh.neptune.feature.hotbar.impl.Item;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;


public class ItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile.getMatch() != null
                && profile.getMatch().getState().equals(MatchState.IN_ROUND)
                && profile.getState() != ProfileState.IN_SPECTATOR) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        if (profile.getState() == ProfileState.IN_CUSTOM) return;
        event.setCancelled(true);
        if (event.getItem() == null) return;
        if (event.getItem().getType().equals(Material.AIR)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        handleAction(profile, event.getItem());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Profile profile = API.getProfile(player);
        if (profile.getMatch() != null
                && profile.getMatch().getState().equals(MatchState.IN_ROUND)
                && profile.getState() != ProfileState.IN_SPECTATOR) {
            return;
        }
        if (profile.hasState(ProfileState.IN_CUSTOM, ProfileState.IN_KIT_EDITOR)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (event.getClickedInventory().getType() != InventoryType.CRAFTING && event.getClickedInventory() != event.getWhoClicked().getInventory()) return;
        event.setCancelled(true);
        if (event.getCurrentItem() != null && event.getCursor() != null && event.getCurrentItem().getType() == Material.AIR && event.getCursor().getType() == Material.AIR)
            return;
        ItemStack item = event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ? event.getCursor() : event.getCurrentItem();
        if (item == null) return;
        if (item.getType().equals(Material.AIR)) return;

        handleAction(profile, item);
    }

    @EventHandler
    public void swapOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile.getMatch() != null
                && profile.getMatch().getState().equals(MatchState.IN_ROUND)
                && profile.getState() != ProfileState.IN_SPECTATOR) {
            return;
        }
        if (profile.hasState(ProfileState.IN_CUSTOM, ProfileState.IN_KIT_EDITOR)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
        if (event.getOffHandItem() == null) return;
        if (event.getOffHandItem().getType().equals(Material.AIR)) return;

        handleAction(profile, event.getOffHandItem());
    }

    private void handleAction(Profile profile, ItemStack item) {
        Item clickedItem = Item.getByItemStack(profile.getState(), item, profile.getPlayerUUID());
        if (clickedItem == null) return;

        if (!profile.hasCooldownEnded("hotbar")) return;


        if (clickedItem instanceof CustomItem customItem) {
            String command = customItem.getCommand();
            if (!command.equalsIgnoreCase("none")) {
                profile.getPlayer().performCommand(customItem.getCommand());
            }
        } else {
            clickedItem.getAction().execute(profile.getPlayer());
        }

        profile.addCooldown("hotbar", 200);
    }
}
