package com.jonahseguin.drink.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.utils.CC;

import javax.annotation.Nonnull;

@Getter
@Setter
public class DrinkAuthorizer {

    public boolean isAuthorized(@Nonnull CommandSender sender, @Nonnull DrinkCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(CC.color(MessagesLocale.NO_PERMISSION.getString()));
                return false;
            }
        }
        return true;
    }

}
