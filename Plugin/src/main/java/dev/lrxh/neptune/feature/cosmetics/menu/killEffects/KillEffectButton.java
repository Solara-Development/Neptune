package dev.lrxh.neptune.feature.cosmetics.menu.killEffects;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.KillEffect;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KillEffectButton extends Button {
    private final KillEffect killEffect;

    public KillEffectButton(int slot, KillEffect killEffect) {
        super(slot);
        this.killEffect = killEffect;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (!player.hasPermission(killEffect.permission())) return;
        API.getProfile(player).getSettingData().setKillEffect(killEffect);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getKillEffect().equals(killEffect);
        List<String> lore;

        if (player.hasPermission(killEffect.permission())) {
            lore = selected ? MenusLocale.KILL_EFFECTS_SELECTED_LORE.getStringList() : MenusLocale.KILL_EFFECTS_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.KILL_EFFECTS_NO_PERMISSION_LORE.getStringList();
        }

        return new ItemBuilder(killEffect.getMaterial())
                .name(selected ? MenusLocale.KILL_EFFECTS_NAME_SELECTED.getString().replace("<displayName>", killEffect.getDisplayName()) : MenusLocale.KILL_EFFECTS_NAME_NOT_SELECTED.getString().replace("<displayName>", killEffect.getDisplayName()))
                .lore(lore, player)

                .build();
    }
}
