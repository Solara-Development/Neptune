package dev.lrxh.api.kit;

import dev.lrxh.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface IKit {
    String getName();

    String getDisplayName();

    List<ItemStack> getItems();

    ItemStack getIcon();

    int getQueue();

    int getPlaying();

    int getSlot();

    int getKitEditorSlot();

    int getLeaderboardSlot();

    double getHealth();

    List<PotionEffect> getPotionEffects();

    double getDamageMultiplier();

    HashMap<IKitRule, Boolean> getRule();

    HashSet<IArena> getAllArenas();

    void giveLoadout(UUID uuid);
}
