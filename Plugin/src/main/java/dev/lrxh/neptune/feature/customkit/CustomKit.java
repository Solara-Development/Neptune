package dev.lrxh.neptune.feature.customkit;

import com.google.gson.Gson;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PotionEffectUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
@Setter
public class CustomKit {
    private static final Gson GSON = new Gson();

    private final UUID owner;
    private String name;
    private String displayName;
    private ItemStack icon;
    private List<ItemStack> items;
    private HashMap<KitRule, Boolean> rules;
    private List<String> arenaNames;
    private List<Material> whitelistedBlocks;
    private double health;
    private List<PotionEffect> potionEffects;

    public CustomKit(UUID owner, String name) {
        this(owner, name, name);
    }

    public CustomKit(UUID owner, String name, String displayName) {
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.icon = new ItemStack(Material.DIAMOND_SWORD);
        this.items = defaultTemplate();
        this.rules = defaultRules();
        this.arenaNames = new ArrayList<>();
        this.whitelistedBlocks = new ArrayList<>();
        this.health = 20;
        this.potionEffects = new ArrayList<>();
    }

    private CustomKit(UUID owner, Data d) {
        this.owner = owner;
        this.name = d.name;
        this.displayName = d.displayName;
        this.icon = d.icon == null ? new ItemStack(Material.DIAMOND_SWORD) : ItemUtils.deserializeItem(d.icon);
        this.items = d.items == null ? new ArrayList<>() : ItemUtils.deserialize(d.items);
        this.rules = defaultRules();
        if (d.rules != null) d.rules.forEach((k, v) -> {
            try {
                rules.put(KitRule.valueOf(k), v);
            } catch (IllegalArgumentException ignored) {
            }
        });
        this.arenaNames = d.arenas == null ? new ArrayList<>() : new ArrayList<>(d.arenas);
        this.whitelistedBlocks = new ArrayList<>();
        if (d.whitelist != null) for (String m : d.whitelist) {
            Material mat = Material.matchMaterial(m);
            if (mat != null) whitelistedBlocks.add(mat);
        }
        this.health = d.health <= 0 ? 20 : d.health;
        this.potionEffects = new ArrayList<>();
        if (d.effects != null) for (String e : d.effects) {
            PotionEffect effect = PotionEffectUtils.deserialize(e);
            if (effect != null) potionEffects.add(effect);
        }
    }

    private static HashMap<KitRule, Boolean> defaultRules() {
        HashMap<KitRule, Boolean> r = new HashMap<>();
        for (KitRule rule : KitRule.values()) r.put(rule, rule == KitRule.DAMAGE);
        return r;
    }

    public static final int CONTENTS_SIZE = 41;

    private static List<ItemStack> defaultTemplate() {
        ItemStack[] c = new ItemStack[CONTENTS_SIZE];
        c[0] = new ItemStack(Material.DIAMOND_SWORD);
        c[36] = new ItemStack(Material.DIAMOND_BOOTS);
        c[37] = new ItemStack(Material.DIAMOND_LEGGINGS);
        c[38] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        c[39] = new ItemStack(Material.DIAMOND_HELMET);
        c[40] = new ItemStack(Material.SHIELD);
        return new ArrayList<>(Arrays.asList(c));
    }

    public ItemStack itemAt(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : null;
    }

    public void setItemAt(int index, ItemStack item) {
        ItemStack[] c = new ItemStack[Math.max(CONTENTS_SIZE, items.size())];
        for (int i = 0; i < items.size(); i++) c[i] = items.get(i);
        c[index] = item;
        items = new ArrayList<>(Arrays.asList(c));
    }

    public boolean is(KitRule rule) {
        return rules.getOrDefault(rule, false);
    }

    public void toggle(KitRule rule) {
        rules.put(rule, !is(rule));
    }

    public void toggleArena(String arenaName) {
        if (!arenaNames.remove(arenaName)) arenaNames.add(arenaName);
    }

    public Kit toTransientKit() {
        HashSet<Arena> arenas = new HashSet<>();
        for (String n : arenaNames) {
            Arena a = ArenaService.get().getArenaByName(n);
            if (a != null) arenas.add(a);
        }
        HashMap<KitRule, Boolean> r = new HashMap<>(rules);
        r.put(KitRule.HIDDEN, true);
        return new Kit(name, displayName, new ArrayList<>(items), arenas, icon, r,
                0, 0, 0, 0, health, new ArrayList<>(potionEffects), 1.0);
    }

    public String serialize() {
        Data d = new Data();
        d.name = name;
        d.displayName = displayName;
        d.icon = ItemUtils.serialize(icon);
        d.items = ItemUtils.serialize(items);
        d.rules = new HashMap<>();
        rules.forEach((k, v) -> d.rules.put(k.name(), v));
        d.arenas = new ArrayList<>(arenaNames);
        d.whitelist = new ArrayList<>();
        for (Material m : whitelistedBlocks) d.whitelist.add(m.name());
        d.health = health;
        d.effects = new ArrayList<>();
        for (PotionEffect e : potionEffects) d.effects.add(PotionEffectUtils.serialize(e));
        return GSON.toJson(d);
    }

    public static CustomKit deserialize(UUID owner, String json) {
        return new CustomKit(owner, GSON.fromJson(json, Data.class));
    }

    private static class Data {
        String name, displayName, icon, items;
        HashMap<String, Boolean> rules;
        List<String> arenas, whitelist, effects;
        double health;
    }
}
