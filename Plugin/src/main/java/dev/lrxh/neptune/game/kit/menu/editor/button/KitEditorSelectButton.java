package dev.lrxh.neptune.game.kit.menu.editor.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitEditorSelectButton extends Button {
    private final Kit kit;

    public KitEditorSelectButton(int slot, Kit kit) {
        super(slot);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);

        MessagesLocale.KIT_EDITOR_START.send(player.getUniqueId(), Placeholder.parsed("kit", kit.getDisplayName()));
        player.closeInventory();

        profile.getGameData().setKitEditor(kit);
        profile.setState(ProfileState.IN_KIT_EDITOR);

        kit.giveLoadout(player.getUniqueId(), false);

        player.updateInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.KIT_EDITOR_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .componentLore(ItemUtils.getLore(MenusLocale.KIT_EDITOR_SELECT_LORE.getStringList()), Placeholder.parsed("kit", kit.getDisplayName()), player)
                .build();
    }
}