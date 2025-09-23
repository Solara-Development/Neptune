package dev.lrxh.neptune.main;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GithubUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand {

    @Command(name = "", desc = "")
    @Require("neptune.admin")
    public void help(@Sender Player player) {
        new MainMenu().open(player);
    }

    @Command(name = "setspawn", desc = "")
    @Require("neptune.admin")
    public void setspawn(@Sender Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        player.sendMessage(CC.color("&aSuccessfully set spawn!"));
    }

    @Command(name = "info", desc = "")
    @Require("neptune.admin")
    public void info(@Sender Player player) {
        player.sendMessage(CC.color("&eThis server is running Neptune version: "
                + Neptune.get().getDescription().getVersion()));
        player.sendMessage(CC.color("&eCommit: &f" + GithubUtils.getCommitId()));
        player.sendMessage(CC.color("&eMessage: &f" + GithubUtils.getCommitMessage()));
    }

    @Command(name = "reload", desc = "")
    @Require("neptune.admin")
    public void reload(@Sender CommandSender sender) {
        ConfigService.get().load();
        CosmeticService.get().load();
        HotbarService.get().load();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Profile profile = API.getProfile(p);
            if (profile.getState().equals(ProfileState.IN_GAME)
                    || profile.getState().equals(ProfileState.IN_KIT_EDITOR))
                return;
            HotbarService.get().giveItems(p);
        }

        sender.sendMessage(CC.color("&aSuccessfully reloaded configs!"));
    }

    @Command(name = "stop", desc = "")
    public void stop(@Sender Player player) {
        Neptune.get().setAllowMatches(false);

        for (Match match : MatchService.get().matches) {
            match.resetArena();
        }

        Bukkit.getServer().shutdown();
    }
}