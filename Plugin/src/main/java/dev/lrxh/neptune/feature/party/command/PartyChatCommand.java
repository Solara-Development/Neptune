package dev.lrxh.neptune.feature.party.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

public class PartyChatCommand {

    @Command(name = "", desc = "Send a message to your party chat", usage = "<message>")
    public void chat(@Sender Player player, @OptArg @Text String message) {
        Profile profile = API.getProfile(player);
        Party party = profile.getGameData().getParty();

        if (party == null) {
            MessagesLocale.PARTY_CHAT_NOT_IN_PARTY.send(player.getUniqueId());
            return;
        }

        if (message == null || message.isEmpty()) {
            boolean enabled = !profile.getGameData().isPartyChatEnabled();
            profile.getGameData().setPartyChatEnabled(enabled);
            if (enabled) {
                MessagesLocale.PARTY_CHAT_TOGGLE_ON.send(player.getUniqueId());
            } else {
                MessagesLocale.PARTY_CHAT_TOGGLE_OFF.send(player.getUniqueId());
            }
            return;
        }

        party.chat(player.getUniqueId(), player.getName(), message);
    }
}
