package dev.lrxh.neptune.game.kit.procedure;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.game.kit.menu.KitsManagementMenu;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitProcedureListener implements Listener {
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        String input = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (input.equalsIgnoreCase("Cancel") && !profile.getKitProcedure().getType().equals(KitProcedureType.NONE)) {
            event.setCancelled(true);
            player.sendMessage(CC.success("Canceled Procedure"));
            profile.getKitProcedure().setType(KitProcedureType.NONE);
            profile.getKitProcedure().setKit(null);
            return;
        }

        switch (profile.getKitProcedure().getType()) {
            case CREATE -> {
                event.setCancelled(true);
                profile.getKitProcedure().setType(KitProcedureType.NONE);
                Kit kit = new Kit(input, player);

                if (KitService.get().add(kit)) {
                    player.sendMessage(CC.error("Kit already exists"));
                    return;
                }

                player.sendMessage(CC.success("Created kit"));
                Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                    PlayerUtil.reset(player);
                });
                HotbarService.get().giveItems(player);
                new KitsManagementMenu().open(player);
            }
            case RENAME -> {
                event.setCancelled(true);
                profile.getKitProcedure().setType(KitProcedureType.NONE);
                profile.getKitProcedure().getKit().setDisplayName(input);
                player.sendMessage(CC.success("Renamed kit"));
                new KitManagementMenu(profile.getKitProcedure().getKit()).open(player);
            }
            case SET_INV -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                Neptune.get().setAllowJoin(false);

                Kit kit = profile.getKitProcedure().getKit();

                profile.getKitProcedure().setType(KitProcedureType.NONE);
                kit.setItems(Arrays.stream(player.getInventory().getContents()).toList());

                List<PotionEffect> potionEffects = new ArrayList<>();

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    int currentDuration = effect.getDuration();
                    int maxDuration = PlayerUtil.getMaxDuration(player, effect.getType());

                    potionEffects.add(new PotionEffect(effect.getType(), Math.min(currentDuration, maxDuration), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
                }

                kit.setPotionEffects(potionEffects);

                for (Profile p : ProfileService.get().profiles.values()) {
                    p.getGameData().get(kit).setKitLoadout(kit.getItems());
                }

                //IDatabase#getAll was removed due to performence
                // DatabaseService.get().getDatabase().getAll().thenAccept(dataDocuments -> {
                //     for (DataDocument document : dataDocuments) {
                //         DataDocument kitStatistics = document.getDataDocument("kitData");
                //         DataDocument kitDocument = kitStatistics.getDataDocument(profile.getKitProcedure().getKit().getName());

                //         kitDocument.put("kit", "");

                //         kitStatistics.put("kitData", kitDocument);

                //         DatabaseService.get().getDatabase().replace(document.getString("uuid"), document);
                //     }
                // });

                player.sendMessage(CC.success("Set new inv"));
                new KitManagementMenu(profile.getKitProcedure().getKit()).open(player);
                Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                    PlayerUtil.reset(player);
                });
                HotbarService.get().giveItems(player);
                Neptune.get().setAllowJoin(true);
            }
            case SET_ICON -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                Material material = player.getInventory().getItemInMainHand().getType();
                if (!material.equals(Material.AIR)) {
                    profile.getKitProcedure().setType(KitProcedureType.NONE);
                    profile.getKitProcedure().getKit().setIcon(player.getInventory().getItemInMainHand().clone());
                    player.sendMessage(CC.success("Set new icon"));
                    new KitManagementMenu(profile.getKitProcedure().getKit()).open(player);
                } else {
                    player.sendMessage(CC.error("You must be holding an item to set the icon, please try again"));
                    return;
                }
            }
            case SET_DAMAGE_MULTIPLIER -> {
                event.setCancelled(true);
                try {
                    double damageMultiplier = Double.parseDouble(input);
                    profile.getKitProcedure().setType(KitProcedureType.NONE);
                    profile.getKitProcedure().getKit().setDamageMultiplier(damageMultiplier);
                    player.sendMessage(CC.success("Set damage multiplier"));
                    new KitManagementMenu(profile.getKitProcedure().getKit()).open(player);
                } catch (NumberFormatException e) {
                    player.sendMessage(CC.error("Invalid number."));
                    return;
                }
            }

        }
        profile.getKitProcedure().setKit(null);
        KitService.get().save();
    }
}
