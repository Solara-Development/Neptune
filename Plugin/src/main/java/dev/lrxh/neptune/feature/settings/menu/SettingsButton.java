package dev.lrxh.neptune.feature.settings.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.settings.Setting;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SettingsButton extends Button {
    private final Setting setting;

    public SettingsButton(int slot, Setting setting) {
        super(slot);
        this.setting = setting;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);

        return new ItemBuilder(setting.getMaterial(), player.getUniqueId())
                .name(setting.getDisplayName())
                .componentLore(ItemUtils.getLore(setting.toggled(player) ? setting.getEnabledLore() : setting.getDisabledLore(), TagResolver.resolver(
                        Placeholder.unparsed("ping", String.valueOf(profile.getSettingData().getMaxPing())),
                        Placeholder.parsed("kill-effect", profile.getSettingData().getKillEffect().getDisplayName()),
                        Placeholder.parsed("kill-message", profile.getSettingData().getKillMessagePackage().getDisplayName()),
                        Placeholder.parsed("armor-trim", profile.getSettingData().getArmorTrimPackage().getDisplayName()))), player)
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        setting.execute(player, type);
    }
}
