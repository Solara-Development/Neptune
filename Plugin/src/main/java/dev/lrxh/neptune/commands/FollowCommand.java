package dev.lrxh.neptune.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.entity.Player;

public class FollowCommand {

    @Command(name = "", desc = "", usage = "<player>")
    public void follow(@Sender Player player, Player target) {
        SettingData followingSettingData = API.getProfile(target.getUniqueId()).getSettingData();
        if (target == player)
            return;
        Profile taProfile = API.getProfile(target);
        Profile profile = API.getProfile(player);

        if (taProfile.getGameData().getParty() != null
                && profile.getGameData().getParty() != null
                && taProfile.getGameData().getParty().equals(profile.getGameData().getParty())) {
            return;
        }

        if (followingSettingData.getFollowings().contains(player.getUniqueId())) {
            followingSettingData.removeFollower(player.getUniqueId());
            MessagesLocale.STOP_FOLLOWING.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }

        API.getProfile(target.getUniqueId()).getSettingData().addFollower(player.getUniqueId());
        MessagesLocale.START_FOLLOW.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
    }
}