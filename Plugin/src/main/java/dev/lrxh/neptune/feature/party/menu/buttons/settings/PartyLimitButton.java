package dev.lrxh.neptune.feature.party.menu.buttons.settings;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class PartyLimitButton extends Button {
    private final Party party;

    public PartyLimitButton(int slot, Party party) {
        super(slot);
        this.party = party;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (type.equals(ClickType.LEFT)) {
            Profile profile = API.getProfile(player);
            if (party.getMaxUsers() + 1 > profile.getPartyLimit()) {
                MessagesLocale.PARTY_MAX_SIZE_SETTING.send(player, new Replacement("<max>", String.valueOf(profile.getPartyLimit())));
                return;
            }
            party.setMaxUsers(party.getMaxUsers() + 1);
        } else if (type.equals(ClickType.RIGHT)) {
            party.setMaxUsers(Math.max(party.getUsers().size(), party.getMaxUsers() - 1));
        }
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_MAX_SIZE_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_MAX_SIZE_TITLE.getString())
                .lore(ItemUtils.getLore(MenusLocale.PARTY_SETTINGS_MAX_SIZE_LORE.getStringList(),
                        new Replacement("<size>", String.valueOf(party.getMaxUsers()))), player)

                .build();
    }
}
