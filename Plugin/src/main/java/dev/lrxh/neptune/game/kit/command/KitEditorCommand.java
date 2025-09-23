package dev.lrxh.neptune.game.kit.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KitEditorCommand {

    @Command(name = "menu", desc = "")
    public void open(@Sender Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null)
            return;
        if (profile.hasState(ProfileState.IN_LOBBY, ProfileState.IN_PARTY)) {
            new KitEditorMenu().open(player);
        }
    }

    @Command(name = "reset", desc = "", usage = "<kit>")
    public void reset(@Sender Player player, Kit kit) {
        if (player == null)
            return;
        Profile profile = API.getProfile(player);

        profile.getGameData().get(kit).setKitLoadout(kit.getItems());

        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            profile.getGameData().get(profile.getGameData().getKitEditor())
                    .setKitLoadout(Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId(), new Replacement("<kit>", kit.getDisplayName()));
            if (profile.getGameData().getParty() == null) {
                profile.setState(ProfileState.IN_LOBBY);
            } else {
                profile.setState(ProfileState.IN_PARTY);
            }
        }

        MessagesLocale.KIT_EDITOR_RESET.send(player.getUniqueId(), new Replacement("<kit>", kit.getDisplayName()));
    }
}
