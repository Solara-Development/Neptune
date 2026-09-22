package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

public class MatchEndRunnable extends NeptuneRunnable {
    private final Match match;
    private int endTimer = 3;

    public MatchEndRunnable(Match match) {
        this.match = match;

        match.getTime().setStop(true);
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match)) {
            stop();
            return;
        }
        if (endTimer == 0) {
            MatchService.get().stopMatch(match);
            stop();
        }
        endTimer--;
    }
}
