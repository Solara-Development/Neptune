package dev.lrxh.neptune.configs.impl.handler;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface IDataAccessor {

    Neptune plugin = Neptune.get();

    default String getString() {
        return getConfigFile().getConfiguration().getString(getPath());
    }

    default List<String> getStringList() {
        return getConfigFile().getConfiguration().getStringList(getPath());
    }

    default int getInt() {
        return getConfigFile().getConfiguration().getInt(getPath());
    }

    default <T> List<T> getList(Class<T> type) {
        return Objects.requireNonNull(getConfigFile().getConfiguration().getList(getPath()))
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    default boolean getBoolean() {
        return getConfigFile().getConfiguration().getBoolean(getPath());
    }

    default boolean resetUnknown() {
        return true;
    }

    String getHeader();

    String getPath();

    String getComment();

    List<String> getDefaultValue();

    DataType getDataType();

    ConfigFile getConfigFile();

    default void applyHeader() {
        String header = getHeader();
        if (!header.isEmpty()) {
            getConfigFile().getConfiguration().options().setHeader(List.of(header));
        }
    }

    default void setValue(String path, List<?> rawValue, DataType type) {
        switch (type) {
            case LIST -> getConfigFile().getConfiguration().set(path, rawValue);
            case STRING_LIST -> getConfigFile().getConfiguration().set(path, rawValue.stream().map(String::valueOf).toList());
            case STRING -> getConfigFile().getConfiguration().set(path, String.valueOf(rawValue.getFirst()));
            case INT -> getConfigFile().getConfiguration().set(path, Integer.parseInt(String.valueOf(rawValue.getFirst())));
            case BOOLEAN -> getConfigFile().getConfiguration().set(path, Boolean.parseBoolean(String.valueOf(rawValue.getFirst())));
        }
    }

    default void set(Object value) {
        getConfigFile().getConfiguration().set(getPath(), value);
    }

    default void comment(String path, String comment) {
        if (comment != null) {
            getConfigFile().getConfiguration()
                    .setInlineComments(path, Collections.singletonList(comment));
            getConfigFile().save();
        }
    }

    default void load() {
        applyHeader();
        var cfgFile = getConfigFile();
        if (cfgFile == null) return;

        var root = cfgFile.getConfiguration();
        var accessors = List.of(this.getClass().getEnumConstants());

        for (var a : accessors) {
            if (root.get(a.getPath()) == null) {
                setValue(a.getPath(), a.getDefaultValue(), a.getDataType());
                comment(a.getPath(), a.getComment());
            }
        }

        if (resetUnknown()) {
            var valid = accessors.stream()
                    .map(IDataAccessor::getPath)
                    .collect(Collectors.toSet());
            cleanupSection(root, "", valid);
        }

        cfgFile.save();
    }


    private void cleanupSection(ConfigurationSection section,
                                String parentPath,
                                Set<String> validPaths) {
        for (String key : section.getKeys(false)) {
            String fullPath = parentPath.isEmpty() ? key : (parentPath + "." + key);

            if (section.isConfigurationSection(key)) {
                cleanupSection(section.getConfigurationSection(key), fullPath, validPaths);

                ConfigurationSection nested = section.getConfigurationSection(key);
                boolean hasValidChild = validPaths.stream().anyMatch(p -> p.startsWith(fullPath + "."));
                if (nested.getKeys(false).isEmpty() && !hasValidChild) {
                    section.set(key, null);
                }

            } else {
                if (!validPaths.contains(fullPath)) {
                    section.set(key, null);
                }
            }
        }
    }

    public void update();
}
