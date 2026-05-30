package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PotionEffectUtils {
    public String serialize(PotionEffect effect) {
        return effect.getType().getKey().getKey() + ":" +
                effect.getDuration() + ":" +
                effect.getAmplifier() + ":" +
                effect.isAmbient() + ":" +
                effect.hasParticles() + ":" +
                effect.hasIcon();
    }

    public PotionEffect deserialize(String data) {
        String[] parts = data.split(":");
        if (parts.length < 6) return null;
        PotionEffectType type = Registry.EFFECT.get(NamespacedKey.minecraft(parts[0].toLowerCase()));
        if (type == null) return null;
        return new PotionEffect(type,
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Boolean.parseBoolean(parts[3]),
                Boolean.parseBoolean(parts[4]),
                Boolean.parseBoolean(parts[5]));
    }
}
