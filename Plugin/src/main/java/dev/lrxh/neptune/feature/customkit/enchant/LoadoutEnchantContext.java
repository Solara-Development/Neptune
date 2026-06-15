package dev.lrxh.neptune.feature.customkit.enchant;

import dev.lrxh.neptune.feature.rankedloadout.EditableLoadout;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class LoadoutEnchantContext {
    private final EditableLoadout loadout;
    private final int index;
    private final Supplier<Menu> returnMenu;
    private final Runnable onSave;
}
