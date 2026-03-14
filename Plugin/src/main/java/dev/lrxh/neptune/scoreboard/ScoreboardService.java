package dev.lrxh.neptune.scoreboard;

import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.scoreboard.IScoreboardService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class ScoreboardService implements IScoreboardService {

    private static ScoreboardService instance;
    private final HashMap<String, Function<IProfile, List<String>>> scoreboards = new HashMap<>();

    public static ScoreboardService get() {
        if (instance == null) instance = new ScoreboardService();
        return instance;
    }

    @Override
    public void registerScoreboard(String state, Function<IProfile, List<String>> scoreboardFunction) {
        scoreboards.put(state, scoreboardFunction);
    }

    @Override
    public List<String> getScoreboardLines(String state, IProfile profile) {
        Function<IProfile, List<String>> scoreboardFunction = scoreboards.get(state);
        if (scoreboardFunction != null) {
            List<String> lines = scoreboardFunction.apply(profile);
            return new ArrayList<>(lines);
        }
        return new ArrayList<>();
    }
}