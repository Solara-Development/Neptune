package dev.lrxh.neptune.feature.cosmetics;

import dev.lrxh.neptune.configs.impl.CosmeticsLocale;
import dev.lrxh.neptune.utils.RandomUtils;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

@Getter
public enum KillEffect {
    NONE(CosmeticsLocale.NONE_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.NONE_MATERIAL.getString()),
            CosmeticsLocale.NONE_SLOT.getInt()) {
    },
    LIGHTNING(CosmeticsLocale.LIGHTNING_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.LIGHTNING_MATERIAL.getString()),
            CosmeticsLocale.LIGHTNING_SLOT.getInt()) {
        @Override
        public void execute(Player player, Player killer) {
            Location location = player.getLocation();
            double x = location.getX();
            double y = location.getY() + 2.0;
            double z = location.getZ();
            location.getWorld().strikeLightningEffect(new Location(location.getWorld(), x, y, z));
        }
    },
    FIREWORKS(CosmeticsLocale.FIREWORKS_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.FIREWORKS_MATERIAL.getString()),
            CosmeticsLocale.FIREWORKS_SLOT.getInt()) {
        @Override
        public void execute(Player player, Player killer) {
            playEffect(Particle.FIREWORK, killer, player.getLocation(), 50, 5);
        }
    },
    ANGRY(CosmeticsLocale.ANGRY_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.ANGRY_MATERIAL.getString()),
            CosmeticsLocale.ANGRY_SLOT.getInt()) {
        @Override
        public void execute(Player player, Player killer) {
            playEffect(Particle.ANGRY_VILLAGER, killer, player.getLocation(), 25, 5);
        }
    },
    HEARTS(CosmeticsLocale.HEARTS_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.HEARTS_MATERIAL.getString()),
            CosmeticsLocale.HEARTS_SLOT.getInt()) {
        @Override
        public void execute(Player player, Player killer) {
            playEffect(Particle.HEART, killer, player.getLocation(), 25, 5);
        }
    },
    LAVA(CosmeticsLocale.LAVA_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.LAVA_MATERIAL.getString()),
            CosmeticsLocale.LAVA_SLOT.getInt()) {
        @Override
        public void execute(Player player, Player killer) {
            playEffect(Particle.FLAME, killer, player.getLocation(), 25, 5);
        }
    };

    private final String displayName;
    private final Material material;
    private final int slot;


    KillEffect(String displayName, Material material, int slot) {
        this.displayName = displayName;
        this.material = material;
        this.slot = slot;
    }

    public void execute(Player player, Player killer) {
    }

    public void playEffect(Particle particle, Player player, Location location, int amount, int duration) {
        new NeptuneRunnable() {
            final int maxTicks = duration * 20;
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    stop();
                    return;
                }

                player.spawnParticle(particle, location, amount, RandomUtils.getRandFloat(0, 0.7f), 1, RandomUtils.getRandFloat(0, 0.7f), 0.05, null);
                ticks += 10;
            }
        }.start(10);
    }

    public String permission() {
        return "neptune.cosmetics.kill-effects." + this.name().toLowerCase();
    }
}
