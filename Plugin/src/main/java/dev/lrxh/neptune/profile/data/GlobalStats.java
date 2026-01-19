package dev.lrxh.neptune.profile.data;

import dev.lrxh.api.data.IDivision;
import dev.lrxh.api.data.IGlobalStats;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalStats implements IGlobalStats {
    private final Profile profile;
    private int kills = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;
    private int elo = 0;
    private Division division = DivisionService.get().getDivisionByElo(0);

    public GlobalStats(Profile profile) {
        this.profile = profile;
    }

    public void setDivision(IDivision division) {
        this.division = (Division) division;
    }

    public void update() {
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.elo = 0;

        for (KitData kitData : profile.getGameData().getKitDataInternal().values()) {
            this.wins += kitData.getWins();
            this.losses += kitData.getLosses();
            this.kills += kitData.getKills();
            this.deaths += kitData.getDeaths();
            this.currentStreak += kitData.getCurrentStreak();
            this.bestStreak = Math.max(this.bestStreak, kitData.getBestStreak());
            this.elo += kitData.getElo();
        }
        int kitData = profile.getGameData().getKitDataInternal().size();
        if (kitData != 0) this.elo = this.elo / kitData;

        this.division = DivisionService.get().getDivisionByElo(elo);
    }

    @Override
    public double getWinRatio() {
        int totalGames = wins + losses;
        if (totalGames == 0) return 0.0;
        return Math.round(((double) wins / totalGames) * 100);
    }
}
