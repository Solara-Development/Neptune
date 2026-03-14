package dev.lrxh.neptune.feature.party.menu.buttons.settings;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class PartyAdvertisementButton extends Button {
    private final Party party;

    public PartyAdvertisementButton(int slot, Party party) {
        super(slot);
        this.party = party;
    }


    @Override
    public void onClick(ClickType type, Player player) {
        if (!player.hasPermission("neptune.party.advertise")) {
            MessagesLocale.PARTY_NO_PERMISSION.send(player);
            return;
        }
        party.advertise();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);

        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_TITLE.getString())
                .componentLore(profile.hasCooldownEnded("party_advertise") ? ItemUtils.getLore(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_LORE_NO_COOLDOWN.getStringList()) : ItemUtils.getLore(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_LORE_COOLDOWN.getStringList(), Placeholder.unparsed("cooldown", profile.getCooldowns().get("party_advertise").formatMinutesSeconds())), player)
                .build();
    }
}
