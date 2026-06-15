package dev.lrxh.neptune.profile.data;

import dev.lrxh.api.data.IKitData;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class KitData implements IKitData {
    private static final int K_FLOOR = 16;

    private int getKFactor() {
        if (elo < 500) return 64;
        if (elo < 1000) return 48;
        if (elo < 1500) return 32;
        if (elo < 2000) return 24;
        return K_FLOOR;
    }
    private int kills = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int bestStreak = 0;
    private int currentStreak = 0;
    private List<ItemStack> kitLoadout = new ArrayList<>();
    private Division division = DivisionService.get().getDivisionByElo(SettingsLocale.STARTING_ELO.getInt());
    private int elo = SettingsLocale.STARTING_ELO.getInt();
    private HashMap<String, Object> customData = new HashMap<>();
    private HashMap<String, Object> persistentData = new HashMap<>();

    public double getKdr() {
        if (deaths == 0) return kills;
        double kd = (double) kills / deaths;
        BigDecimal bd = new BigDecimal(kd);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void setCustomData(String key, Object value) {
        customData.put(key, value);
    }

    @Override
    public Object getCustomData(String key) {
        return customData.get(key);
    }

    @Override
    public void setPersistentData(String key, Object value) {
        persistentData.put(key, value);
    }

    @Override
    public Object getPersistentData(String key) {
        return persistentData.get(key) != null ? persistentData.get(key) : null;
    }

    /**
     * Updates ELO rating using the Arpad Elo system.
     *
     * @param won         true if the player won, false if they lost
     * @param opponentElo the opponent's ELO rating before this match
     * @return true if the player ranked up to a new division
     */
    public boolean updateElo(boolean won, int opponentElo) {
        double expected = 1.0 / (1.0 + Math.pow(10.0, (opponentElo - elo) / 400.0));
        double actual = won ? 1.0 : 0.0;
        int change = (int) Math.round(getKFactor() * (actual - expected));

        elo += change;
        if (elo < 0) elo = 0;

        return updateDivision();
    }

    public boolean updateDivision() {
        if (DivisionService.get().divisions.isEmpty()) return false;
        Division previous = this.division;
        Division updated = DivisionService.get().getDivisionByElo(elo);
        if (updated == null) {
            updated = DivisionService.get().getDivisionByElo(0);
        }
        this.division = updated;

        if (previous == null) return true;

        int previousDivision = previous.getEloRequired();
        int newDivision = updated.getEloRequired();

        return newDivision > previousDivision;
    }
}

