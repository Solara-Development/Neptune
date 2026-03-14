package dev.lrxh.neptune.game.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MatchSpectateButton extends Button {
    private final SoloFightMatch match;

    public MatchSpectateButton(int slot, SoloFightMatch match) {
        super(slot);
        this.match = match;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.chat("/spec " + match.getParticipantA().getNameUnColored());
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(match.getKit().getIcon())
                .name(MenusLocale.MATCH_LIST_ITEM_NAME.getString()
                        .replace("<red-name>", match.getParticipantA().getNameUnColored())
                        .replace("<blue-name>", match.getParticipantB().getNameUnColored()))
                .componentLore(ItemUtils.getLore(MenusLocale.MATCH_LIST_ITEM_LORE.getStringList(), TagResolver.resolver(
                        Placeholder.parsed("arena", match.getArena().getDisplayName()),
                        Placeholder.parsed("kit", match.getKit().getDisplayName()))), player)
                .build();
    }
}
