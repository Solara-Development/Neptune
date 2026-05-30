package dev.lrxh.neptune.feature.customkit.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.menu.CustomKitsMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

public class CustomKitCommand {

    @Command(name = "", desc = "Open the custom kits menu")
    public void open(@Sender Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null || !profile.hasState(ProfileState.IN_LOBBY)) {
            MessagesLocale.CUSTOM_KIT_NOT_LOBBY.send(player.getUniqueId());
            return;
        }
        new CustomKitsMenu().open(player);
    }
}
