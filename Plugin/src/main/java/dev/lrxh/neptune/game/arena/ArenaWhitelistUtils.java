package dev.lrxh.neptune.game.arena;

import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ArenaWhitelistUtils {

    private static final Set<Material> PROTECTED_BLOCKS = Set.of(
            Material.BEDROCK,
            Material.BARRIER,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.JIGSAW,
            Material.LIGHT,
            Material.MOVING_PISTON,
            Material.PISTON_HEAD,
            Material.END_PORTAL,
            Material.END_GATEWAY,
            Material.NETHER_PORTAL,
            Material.END_PORTAL_FRAME,
            Material.DRAGON_EGG,
            Material.SPAWNER,
            Material.TRIAL_SPAWNER,
            Material.VAULT
    );

    public static boolean isProtected(Material material) {
        if (material == null || material.isAir() || material.isLegacy()) {
            return true;
        }
        if (PROTECTED_BLOCKS.contains(material)) {
            return true;
        }
        return material.name().contains("COMMAND_BLOCK");
    }

    public static List<Material> getBreakableBlocks() {
        List<Material> blocks = new ArrayList<>();
        for (Material material : ItemBrowserService.get().getBlocks()) {
            if (!isProtected(material)) {
                blocks.add(material);
            }
        }
        return blocks;
    }
}
