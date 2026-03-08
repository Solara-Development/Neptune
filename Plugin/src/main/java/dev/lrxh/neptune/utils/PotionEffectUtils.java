package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PotionEffectUtils {
    public String serialize(PotionEffect effect) {
        return effect.getType().getName() + ":" +
                effect.getDuration() + ":" +
                effect.getAmplifier() + ":" +
                effect.isAmbient() + ":" +
                effect.hasParticles() + ":" +
                effect.hasIcon();
    }

    public PotionEffect deserialize(String data) {
        String[] parts = data.split(":");
        PotionEffectType type = PotionEffectType.getByName(parts[0]);
        int duration = Integer.parseInt(parts[1]);
        int amplifier = Integer.parseInt(parts[2]);
        boolean ambient = Boolean.parseBoolean(parts[3]);
        boolean particles = Boolean.parseBoolean(parts[4]);
        boolean icon = Boolean.parseBoolean(parts[5]);

        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }
}
