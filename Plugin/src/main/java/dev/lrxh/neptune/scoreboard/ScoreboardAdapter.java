package dev.lrxh.neptune.scoreboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import fr.mrmicky.fastboard.FastAdapter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class ScoreboardAdapter implements FastAdapter {
    public Component getTitle(Player player) {
        return CC.returnMessage(player, getAnimatedText());
    }

    public List<Component> getLines(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return new ArrayList<>();

        ProfileState state = profile.getState();
        Match match;
        switch (state) {
            case IN_LOBBY:
                return CC.getComponentsArray(player, ScoreboardLocale.LOBBY.getStringList());
            case IN_KIT_EDITOR:
                return CC.getComponentsArray(player, ScoreboardLocale.KIT_EDITOR.getStringList());
            case IN_PARTY:
                return CC.getComponentsArray(player, ScoreboardLocale.PARTY_LOBBY.getStringList());
            case IN_QUEUE:
                return CC.getComponentsArray(player, ScoreboardLocale.IN_QUEUE.getStringList());
            case IN_GAME:
                match = profile.getMatch();
                return match.getScoreboard(player.getUniqueId());
            case IN_SPECTATOR:
                match = profile.getMatch();
                if (match instanceof SoloFightMatch) {
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR_BEDWARS.getStringList());
                    }
                    return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR.getStringList());
                } else if (match instanceof TeamFightMatch) {
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR_BEDWARS.getStringList());
                    }
                    return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR_TEAM.getStringList());
                } else if (match instanceof FfaFightMatch) {
                    return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR_FFA.getStringList());
                }
                return CC.getComponentsArray(player, ScoreboardLocale.IN_SPECTATOR.getStringList());
            case IN_CUSTOM:
                return CC.getComponentsArray(player, ScoreboardService.get().getScoreboardLines(profile.getCustomState(), profile));
            default:
                break;
        }

        return new ArrayList<>();
    }

    private String getAnimatedText() {
        int index = (int) ((System.currentTimeMillis() / ScoreboardLocale.UPDATE_INTERVAL.getInt())
                % ScoreboardLocale.TITLE.getStringList().size());
        return ScoreboardLocale.TITLE.getStringList().get(index);
    }
}