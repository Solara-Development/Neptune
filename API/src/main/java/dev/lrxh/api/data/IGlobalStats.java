package dev.lrxh.api.data;

public interface IGlobalStats {
    int getKills();

    void setKills(int amount);

    int getDeaths();

    void setDeaths(int amount);

    int getWins();

    void setWins(int amount);

    int getLosses();

    void setLosses(int amount);

    int getCurrentStreak();

    void setCurrentStreak(int amount);

    int getBestStreak();

    void setBestStreak(int amount);

    int getElo();

    void setElo(int elo);

    IDivision getDivision();

    void setDivision(IDivision division);

    double getWinRatio();

    double getKdr();
}
