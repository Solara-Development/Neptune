package dev.lrxh.neptune.feature.rankedloadout;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@UtilityClass
public class LoadoutPermissionUtils {

    public static final int DEFAULT_MAX = 3;
    private static final String PREFIX = "pvpkit.loadouts.";

    public static int getMaxLoadouts(Player player) {
        if (player == null) return DEFAULT_MAX;
        int max = DEFAULT_MAX;
        boolean unlimited = false;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            String permission = perm.getPermission();
            if (!perm.getValue()) continue;
            if (permission.equals(PREFIX + "unlimited")) {
                unlimited = true;
                continue;
            }
            if (permission.startsWith(PREFIX)) {
                try {
                    int value = Integer.parseInt(permission.substring(PREFIX.length()));
                    if (value > max) max = value;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return unlimited ? Integer.MAX_VALUE : max;
    }
}
