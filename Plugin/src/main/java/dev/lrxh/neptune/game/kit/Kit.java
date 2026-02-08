package dev.lrxh.neptune.game.kit;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.kit.IKitRule;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.VirtualArena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.PotionEffectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@AllArgsConstructor
public class Kit implements IKit {
    private String name;
    private String displayName;
    private List<ItemStack> items;
    private HashSet<Arena> arenas;
    private ItemStack icon;
    private HashMap<KitRule, Boolean> rules;
    private int queue, playing, slot, kitEditorSlot;
    private double health;
    private List<PotionEffect> potionEffects;
    private double damageMultiplier;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon,
               HashMap<KitRule, Boolean> rules, int slot, double health, int kitEditorSlot,
               List<PotionEffect> potionEffects, double damageMultiplier) {
        this.name = name;
        this.displayName = displayName;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.rules = rules;
        this.queue = 0;
        this.playing = 0;
        this.slot = slot;
        this.health = health;
        this.kitEditorSlot = kitEditorSlot;
        this.potionEffects = potionEffects;
        this.damageMultiplier = damageMultiplier;

        addToProfiles();
    }

    public Kit(String name, Player player) {
        this.name = name;
        this.displayName = "&7" + name;
        this.items = Arrays.stream(player.getInventory().getContents()).toList();
        this.arenas = new HashSet<>();
        this.icon = new ItemStack(Material.DIAMOND_SWORD);
        this.rules = rules();
        this.queue = 0;
        this.playing = 0;
        this.slot = KitService.get().kits.size() + 1;
        this.health = 20;
        this.kitEditorSlot = slot;
        this.damageMultiplier = 1.0;

        this.potionEffects = new ArrayList<>();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            int currentDuration = effect.getDuration();
            int maxDuration = PlayerUtil.getMaxDuration(player, effect.getType());

            potionEffects.add(new PotionEffect(effect.getType(), Math.min(currentDuration, maxDuration),
                    effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
        }

        addToProfiles();
    }

    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this.name = name;
        this.displayName = name;
        this.items = items;
        this.arenas = new HashSet<>();
        this.rules = rules();
        this.icon = icon.getType().equals(Material.AIR) ? new ItemStack(Material.BARRIER) : new ItemStack(icon);
        this.queue = 0;
        this.playing = 0;
        this.slot = KitService.get().kits.size() + 1;
        this.health = 20;
        this.kitEditorSlot = slot;
        this.potionEffects = new ArrayList<>();
        this.damageMultiplier = 1.0;

        addToProfiles();
    }

    private HashMap<KitRule, Boolean> rules() {
        HashMap<KitRule, Boolean> rules = new HashMap<>();
        for (KitRule kitRule : KitRule.values()) {
            rules.put(kitRule, false);
        }

        return rules;
    }

    public void toggleArena(Arena arena) {
        if (arenas.contains(arena)) {
            arenas.remove(arena);
            return;
        }
        arenas.add(arena);
    }

    public boolean isArenaAdded(Arena arena) {
        return arenas.contains(arena);
    }

    private void addToProfiles() {
        for (Profile profile : ProfileService.get().profiles.values()) {
            profile.getGameData().getKitDataInternal().put(this, new KitData());
        }
    }

    public List<String> getArenasAsString() {
        List<String> arenasString = new ArrayList<>();
        if (!arenas.isEmpty()) {
            for (Arena arena : arenas) {
                if (arena == null)
                    continue;
                arenasString.add(arena.getName());
            }
        }
        return arenasString;
    }

    public List<String> getPotionsAsString() {
        List<String> potions = new ArrayList<>();
        if (!potionEffects.isEmpty()) {
            for (PotionEffect effect : potionEffects) {
                if (effect == null)
                    continue;
                potions.add(PotionEffectUtils.serialize(effect));
            }
        }
        return potions;
    }

    public boolean is(KitRule kitRule) {
        return rules.get(kitRule);
    }

    public void toggle(KitRule kitRule) {
        rules.put(kitRule, !rules.get(kitRule));
    }

    public void removeQueue() {
        if (!(queue == 0)) {
            queue--;
        }
    }

    public void addQueue() {
        queue++;
    }

    public void removePlaying() {
        if (!(playing == 0)) {
            playing--;
        }
    }

    @Override
    public HashMap<IKitRule, Boolean> getRule() {
        return rules.entrySet().stream().collect(HashMap::new,
                (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    public CompletableFuture<VirtualArena> getRandomArena() {
        List<Arena> arenas1 = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isEnabled())
                continue;
            if (!arena.isSetup() || !arena.isDoneLoading())
                continue;
            arenas1.add(arena);
        }

        if (arenas1.isEmpty())
            return CompletableFuture.completedFuture(null);

        Arena selected = arenas1.get(ThreadLocalRandom.current().nextInt(arenas1.size()));

        return selected.createDuplicate().thenApply(arena -> arena);
    }

    @Override
    public void giveLoadout(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        Profile profile = API.getProfile(playerUUID);
        GameData gameData = profile.getGameData();
        if (gameData.getKitData() == null || gameData.get(this) == null ||
                gameData.get(this).getKitLoadout().isEmpty()) {
            player.getInventory().setContents(items.toArray(new ItemStack[0]));
        } else {
            player.getInventory().setContents(gameData.get(this).getKitLoadout().toArray(new ItemStack[0]));
        }

        player.addPotionEffects(potionEffects);

        player.updateInventory();
    }

    public void giveLoadout(Participant participant) {
        Player player = participant.getPlayer();
        if (player == null)
            return;
        Profile profile = API.getProfile(player);
        GameData gameData = profile.getGameData();
        if (gameData.getKitData() == null || gameData.get(this) == null ||
                gameData.get(this).getKitLoadout().isEmpty()) {
            player.getInventory().setContents(
                    ItemUtils.color(items.toArray(new ItemStack[0]), participant.getColor().getContentColor()));
        } else {
            player.getInventory()
                    .setContents(ItemUtils.color(gameData.get(this).getKitLoadout().toArray(new ItemStack[0]),
                            participant.getColor().getContentColor()));
        }

        player.addPotionEffects(potionEffects);

        player.updateInventory();
    }

    public void addPlaying() {
        playing++;
    }

    public void delete() {
        KitService.get().kits.remove(this);
        KitService.get().save();
    }

    public HashSet<IArena> getAllArenas() {
        return arenas.stream().collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Kit kit) {
            return kit.getName().equals(name);
        }

        return false;
    }
}
