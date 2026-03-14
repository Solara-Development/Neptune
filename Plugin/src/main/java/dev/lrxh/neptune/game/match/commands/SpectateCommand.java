package dev.lrxh.neptune.game.match.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Flag;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.menu.MatchListMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.entity.Player;

public class SpectateCommand {
    @Command(name = "menu", desc = "Open the spectator menu")
    public void spectate(@Sender Player player) {
        Profile profile = API.getProfile(player);
        if (!profile.hasState(ProfileState.IN_LOBBY)) {
            MessagesLocale.CANT_DO_THIS_NOW.send(player);
            return;
        }
        new MatchListMenu().open(player);
    }
    @Command(name = "", desc = "", usage = "<player> [-s: silent]")
    public void spectate(@Sender Player player, Player target, @Flag('s') boolean silent) {
        if (silent && !player.hasPermission("neptune.silent-spectate")) {
            MessagesLocale.SPECTATE_PERMISSION_FLAG.send(player.getUniqueId());
            return;
        }
        if (player.getName().equalsIgnoreCase(target.getName())) {
            MessagesLocale.SPECTATE_SELF.send(player.getUniqueId());
            return;
        }

        Profile profile = API.getProfile(player);

        if (profile.getMatch() != null) {
            MessagesLocale.SPECTATE_IN_MATCH.send(player.getUniqueId());
            return;
        }

        Profile targetProfile = API.getProfile(target);
        if (targetProfile.getMatch() == null) {
            MessagesLocale.SPECTATE_TARGET_NOT_IN_MATCH.send(player.getUniqueId());
            return;
        }

        if (!targetProfile.getSettingData().isAllowSpectators()) {
            MessagesLocale.SPECTATE_NOT_ALLOWED.send(player.getUniqueId(), Placeholder.unparsed("player", target.getName()));
            return;
        }

        if (silent) {
            MessagesLocale.SPECTATE_STARTED_SILENT.send(player.getUniqueId(), Placeholder.unparsed("player", target.getName()));
        } else {
            MessagesLocale.SPECTATE_STARTED.send(player.getUniqueId(), Placeholder.unparsed("player", target.getName()));
        }

        targetProfile.getMatch().addSpectator(player, target, !silent, true);
    }

    @Command(name = "leave", aliases = "quit", desc = "")
    public void leave(@Sender Player player) {
        Profile profile = API.getProfile(player);

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            API.getProfile(player).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }
}
