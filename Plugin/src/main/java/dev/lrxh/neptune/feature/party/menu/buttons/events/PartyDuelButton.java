package dev.lrxh.neptune.feature.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.game.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyDuelButton extends Button {
    private final Party targetParty;

    public PartyDuelButton(int slot, Party targetParty) {
        super(slot);
        this.targetParty = targetParty;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitSelectMenu(targetParty.getLeaderPlayer(), true).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = PlayerUtil.getPlayerHead(targetParty.getLeader());
        List<String> names = new ArrayList<>();
        for (UUID userUUID : targetParty.getUsers()) {
            Player playerInParty = Bukkit.getPlayer(userUUID);
            if (playerInParty != null) {
                names.add("&f" + playerInParty.getName());
            }
        }
        List<String> lines = MenusLocale.PARTY_DUEL_PARTY_LORE.getStringList();
        List<String> lore = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("<members>")) {
                for (String name : names) {
                    lore.add(line.replaceAll("<members>", name));
                }
            }
            else lore.add(line);
        }
        return new ItemBuilder(itemStack)
                .name(MenusLocale.PARTY_DUEL_PARTY_TITLE.getString().replaceAll("<leader>", targetParty.getLeaderName()))
                .componentLore(ItemUtils.getLore(lore), player)
                .build();
    }
}
