package dev.lrxh.neptune.feature.rankedloadout.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SignsLocale;
import dev.lrxh.neptune.feature.rankedloadout.LoadoutPermissionUtils;
import dev.lrxh.neptune.feature.rankedloadout.RankedLoadout;
import dev.lrxh.neptune.feature.rankedloadout.RankedLoadoutService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RankedLoadoutListMenu extends Menu {
    private final Kit kit;

    public RankedLoadoutListMenu(Kit kit) {
        super(MenusLocale.RANKED_LOADOUT_LIST_TITLE.getString().replace("<kit>", kit.getDisplayName()),
                MenusLocale.RANKED_LOADOUT_LIST_SIZE.getInt(),
                Filter.valueOf(MenusLocale.RANKED_LOADOUT_LIST_FILTER.getString()));
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        Profile profile = API.getProfile(player);
        if (profile == null) return buttons;

        List<RankedLoadout> loadouts = RankedLoadoutService.get().getLoadouts(profile, kit);
        RankedLoadout active = RankedLoadoutService.get().getActiveLoadout(profile, kit);
        String activeId = active != null ? active.getId() : "";

        int slot = 0;
        for (RankedLoadout loadout : loadouts) {
            boolean isActive = loadout.getId().equalsIgnoreCase(activeId);
            buttons.add(new Button(slot++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(kit.getIcon())
                            .name(MenusLocale.RANKED_LOADOUT_ITEM_NAME.getString()
                                    .replace("<loadout>", loadout.getDisplayName()))
                            .lore(isActive ? MenusLocale.RANKED_LOADOUT_ITEM_ACTIVE_LORE.getStringList()
                                    : MenusLocale.RANKED_LOADOUT_ITEM_INACTIVE_LORE.getStringList())
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    Profile prof = API.getProfile(p);
                    if (prof == null) return;

                    if (type == ClickType.SHIFT_RIGHT) {
                        if (RankedLoadoutService.get().deleteLoadout(prof, kit, loadout.getId())) {
                            Profile.save(prof);
                            MessagesLocale.RANKED_LOADOUT_DELETED.send(p.getUniqueId(),
                                    Placeholder.parsed("loadout", loadout.getDisplayName()));
                        }
                        new RankedLoadoutListMenu(kit).open(p);
                        return;
                    }

                    if (type.isRightClick()) {
                        if (isActive) {
                            new RankedLoadoutPreviewMenu(kit, loadout).open(p);
                        } else {
                            RankedLoadoutService.get().setActiveLoadout(prof, kit, loadout.getId());
                            Profile.save(prof);
                            MessagesLocale.RANKED_LOADOUT_SELECTED.send(p.getUniqueId(),
                                    Placeholder.parsed("loadout", loadout.getDisplayName()));
                            new RankedLoadoutListMenu(kit).open(p);
                        }
                        return;
                    }

                    new RankedLoadoutEditorMenu(kit, loadout).open(p);
                }
            });
        }

        buttons.add(new ReturnButton(getSize() - 9, new KitEditorMenu()));

        buttons.add(new Button(MenusLocale.RANKED_LOADOUT_CREATE_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                int max = LoadoutPermissionUtils.getMaxLoadouts(p);
                return new ItemBuilder(MenusLocale.RANKED_LOADOUT_CREATE_MATERIAL.getString())
                        .name(MenusLocale.RANKED_LOADOUT_CREATE_NAME.getString())
                        .lore(MenusLocale.RANKED_LOADOUT_CREATE_LORE.getStringList(), TagResolver.resolver(
                                Placeholder.unparsed("loadouts", String.valueOf(loadouts.size())),
                                Placeholder.unparsed("max", String.valueOf(max == Integer.MAX_VALUE ? "∞" : max))), p)
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                Profile prof = API.getProfile(p);
                if (prof == null) return;
                int max = LoadoutPermissionUtils.getMaxLoadouts(p);
                if (loadouts.size() >= max) {
                    MessagesLocale.RANKED_LOADOUT_MAX.send(p.getUniqueId(),
                            Placeholder.unparsed("max", String.valueOf(max == Integer.MAX_VALUE ? "unlimited" : max)));
                    return;
                }
                p.closeInventory();
                SignInputMenu.open(p, "", SignsLocale.RANKED_LOADOUT_NAME.getStringList(), input -> {
                    RankedLoadout created = RankedLoadoutService.get().createLoadout(prof, kit, p, input);
                    if (created == null) {
                        MessagesLocale.RANKED_LOADOUT_CREATE_FAIL.send(p.getUniqueId());
                        new RankedLoadoutListMenu(kit).open(p);
                        return;
                    }
                    Profile.save(prof);
                    MessagesLocale.RANKED_LOADOUT_CREATED.send(p.getUniqueId(),
                            Placeholder.parsed("loadout", created.getDisplayName()));
                    new RankedLoadoutEditorMenu(kit, created).open(p);
                });
            }
        });

        return buttons;
    }
}
