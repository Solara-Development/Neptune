package dev.lrxh.neptune.feature.cosmetics.custom;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

/**
 * Stores a player's custom per-piece armor trim selections.
 * Each armor slot (helmet, chestplate, leggings, boots) can have a different
 * trim pattern and material, completely independent of the pre-built ArmorTrimPackages.
 */
@Getter
@Setter
public class CustomArmorTrimData {

    /** Trim data for each slot. Null = no trim for that slot. */
    private TrimEntry helmetTrim;
    private TrimEntry chestplateTrim;
    private TrimEntry leggingsTrim;
    private TrimEntry bootsTrim;

    /** Whether per-piece custom trim is active (overrides ArmorTrimPackage) */
    private boolean active = false;

    public CustomArmorTrimData() {}

    public TrimEntry getTrimForSlot(ArmorSlot slot) {
        return switch (slot) {
            case HELMET -> helmetTrim;
            case CHESTPLATE -> chestplateTrim;
            case LEGGINGS -> leggingsTrim;
            case BOOTS -> bootsTrim;
        };
    }

    public void setTrimForSlot(ArmorSlot slot, TrimEntry entry) {
        switch (slot) {
            case HELMET -> helmetTrim = entry;
            case CHESTPLATE -> chestplateTrim = entry;
            case LEGGINGS -> leggingsTrim = entry;
            case BOOTS -> bootsTrim = entry;
        }
    }

    public void clearSlot(ArmorSlot slot) {
        setTrimForSlot(slot, null);
    }

    public void clearAll() {
        helmetTrim = null;
        chestplateTrim = null;
        leggingsTrim = null;
        bootsTrim = null;
        active = false;
    }

    public boolean hasAnyTrim() {
        return helmetTrim != null || chestplateTrim != null
                || leggingsTrim != null || bootsTrim != null;
    }

    // Serialization helpers 

    public String serialize() {
        StringBuilder sb = new StringBuilder(active ? "1" : "0").append(";");
        sb.append(serializeEntry(helmetTrim)).append(";");
        sb.append(serializeEntry(chestplateTrim)).append(";");
        sb.append(serializeEntry(leggingsTrim)).append(";");
        sb.append(serializeEntry(bootsTrim));
        return sb.toString();
    }

    private String serializeEntry(TrimEntry e) {
        if (e == null) return "-";
        return e.getPattern() + ":" + e.getMaterial();
    }

    public static CustomArmorTrimData deserialize(String data) {
        CustomArmorTrimData result = new CustomArmorTrimData();
        if (data == null || data.isEmpty() || data.equals("null")) return result;
        try {
            String[] parts = data.split(";");
            result.active = parts[0].equals("1");
            if (parts.length > 1) result.helmetTrim = deserializeEntry(parts[1]);
            if (parts.length > 2) result.chestplateTrim = deserializeEntry(parts[2]);
            if (parts.length > 3) result.leggingsTrim = deserializeEntry(parts[3]);
            if (parts.length > 4) result.bootsTrim = deserializeEntry(parts[4]);
        } catch (Exception ignored) {}
        return result;
    }

    private static TrimEntry deserializeEntry(String s) {
        if (s == null || s.equals("-")) return null;
        String[] parts = s.split(":");
        if (parts.length < 2) return null;
        return new TrimEntry(parts[0], parts[1]);
    }

    // Inner classes

    public enum ArmorSlot {
        HELMET("Helmet", "NETHERITE_HELMET"),
        CHESTPLATE("Chestplate", "NETHERITE_CHESTPLATE"),
        LEGGINGS("Leggings", "NETHERITE_LEGGINGS"),
        BOOTS("Boots", "NETHERITE_BOOTS");

        public final String displayName;
        public final String defaultMaterial;

        ArmorSlot(String displayName, String defaultMaterial) {
            this.displayName = displayName;
            this.defaultMaterial = defaultMaterial;
        }
    }

    @Getter
    @Setter
    public static class TrimEntry {
        private final String pattern;
        private final String material;

        public TrimEntry(String pattern, String material) {
            this.pattern = pattern;
            this.material = material;
        }

        /** Build a Bukkit ArmorTrim from registry keys. Returns null if keys are invalid. */
        public ArmorTrim toArmorTrim() {
            try {
                TrimPattern p = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(pattern));
                TrimMaterial m = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(material));
                if (p == null || m == null) return null;
                return new ArmorTrim(m, p);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
