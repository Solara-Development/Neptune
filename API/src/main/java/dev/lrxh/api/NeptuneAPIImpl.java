package dev.lrxh.api;

import dev.lrxh.api.arena.IArenaService;
import dev.lrxh.api.features.cosmetics.ICosmeticService;
import dev.lrxh.api.features.IDivisionService;
import dev.lrxh.api.features.IItemBrowserService;
import dev.lrxh.api.kit.IKitService;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.api.scoreboard.IScoreboardService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NeptuneAPIImpl implements NeptuneAPI {
    private final IProfileService profileService;
    private final IMatchService matchService;
    private final IKitService kitService;
    private final IScoreboardService scoreboardService;
    private final IArenaService arenaService;
    private final IDivisionService divisionService;
    private final ICosmeticService cosmeticService;
    private final IItemBrowserService itemBrowserService;
}
