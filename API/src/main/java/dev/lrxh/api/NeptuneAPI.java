package dev.lrxh.api;

import dev.lrxh.api.arena.IArenaService;
import dev.lrxh.api.features.cosmetics.ICosmeticService;
import dev.lrxh.api.features.IDivisionService;
import dev.lrxh.api.features.IItemBrowserService;
import dev.lrxh.api.kit.IKitService;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.api.scoreboard.IScoreboardService;

public interface NeptuneAPI {
    IProfileService getProfileService();

    IMatchService getMatchService();

    IKitService getKitService();

    IScoreboardService getScoreboardService();

    IArenaService getArenaService();

    IDivisionService getDivisionService();

    ICosmeticService getCosmeticService();

    IItemBrowserService getItemBrowserService();
}