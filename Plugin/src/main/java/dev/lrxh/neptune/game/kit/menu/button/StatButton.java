package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatButton extends Button {
        private final Kit kit;
        private final Player target;

        public StatButton(int slot, Kit kit, Player target) {
                super(slot);
                this.kit = kit;
                this.target = target;
        }

    @Override
    public ItemStack getItemStack(Player player) {
        KitData kitData = API.getProfile(target).getGameData().get(kit);
        Component lore = CC.returnMessage(player, String.join("\n", MenusLocale.STAT_LORE.getStringList()), TagResolver.resolver(
                Placeholder.parsed("kit", String.valueOf(kit.getDisplayName())),
                Placeholder.parsed("division",
                        kitData.getDivision() != null
                                ? String.valueOf(kitData.getDivision().getDisplayName())
                                : "None"),
                Placeholder.unparsed("wins", String.valueOf(kitData.getWins())),
                Placeholder.unparsed("losses", String.valueOf(kitData.getLosses())),
                Placeholder.unparsed("elo", String.valueOf(kitData.getElo())),
                Placeholder.unparsed("played", String.valueOf(kitData.getWins() + kitData.getLosses())),
                Placeholder.unparsed("current-streak", String.valueOf(kitData.getCurrentStreak())),
                Placeholder.unparsed("best-streak", String.valueOf(kitData.getBestStreak())),
                Placeholder.unparsed("kills", String.valueOf(kitData.getKills())),
                Placeholder.unparsed("deaths", String.valueOf(kitData.getDeaths())),
                Placeholder.unparsed("kdr", String.valueOf(kitData.getKdr()))
        ));
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .componentLore(lore, player)
                .build();
    }
}