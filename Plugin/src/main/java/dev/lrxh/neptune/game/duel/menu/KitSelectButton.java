package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSelectButton extends Button {
    private final Kit kit;
    private final Player receiver;
    private final boolean party;

    public KitSelectButton(int slot, Kit kit, Player receiver, boolean party) {
        super(slot);
        this.kit = kit;
        this.receiver = receiver;
        this.party = party;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.DUEL_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.DUEL_LORE.getStringList(), player)

                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (party) {
            Profile profile = API.getProfile(receiver);
            if (profile == null) return;
            kit.getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    player.sendMessage(CC.error("No arena found, please contact and admin"));
                    return;
                }
                DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, arena, true, 1);
                profile.sendDuel(duelRequest);
                Bukkit.getScheduler().runTask(Neptune.get(), () -> player.closeInventory());
            });
        } else {
            String[] roundsList = MenusLocale.ROUNDS_LIST.getString().replace(" ", "").split(",");
            if (roundsList.length == 1)
                new RoundSelectButton(0, kit, receiver, Integer.parseInt(roundsList[0])).onClick(ClickType.LEFT, player);
            else new RoundsSelectMenu(kit, receiver).open(player);
        }
    }
}