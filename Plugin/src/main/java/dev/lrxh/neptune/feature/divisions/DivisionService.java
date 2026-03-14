package dev.lrxh.neptune.feature.divisions;

import dev.lrxh.api.data.IDivision;
import dev.lrxh.api.features.IDivisionService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class DivisionService extends IService implements IDivisionService {
    private static DivisionService instance;
    public LinkedHashSet<Division> divisions;

    public DivisionService() {
        divisions = new LinkedHashSet<>();
    }

    public static DivisionService get() {
        if (instance == null) instance = new DivisionService();

        return instance;
    }

    public LinkedHashSet<IDivision> getDivisions() {
        return divisions.stream().collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getDivisionsConfig().getConfiguration();
        if (config.contains("DIVISIONS")) {
            for (String divisionName : getKeys("DIVISIONS")) {
                String path = "DIVISIONS." + divisionName + ".";
                String displayName = config.getString(path + "DISPLAY-NAME");
                int eloRequired = config.getInt(path + "ELO-REQUIRED", 0);
                Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));
                int slot = config.getInt(path + "SLOT", divisions.size());

                divisions.add(new Division(divisionName, displayName, eloRequired, material, slot));
            }
        }

        divisions = divisions.stream()
                .sorted(Comparator.comparingInt(Division::getEloRequired).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void registerDivision(IDivision division) {
        divisions.add((Division) division);
        divisions = divisions.stream()
                .sorted(Comparator.comparingInt(Division::getEloRequired).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void save() {

    }

    public Division getDivisionByElo(int elo) {
        if (divisions.isEmpty()) return null;
        for (Division division : divisions) {
            if (elo >= division.getEloRequired()) {
                return division;
            }
        }
        return divisions.iterator().next();
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getDivisionsConfig();
    }
}
