package dev.lrxh.neptune.game.arena.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ArenaCopyCommand {

    @Command(name = "arena copy", desc = "Copy an arena physically to the copy world", usage = "<arena> <amount>")
    @Require("neptune.admin")
    public void copy(@Sender Player player, Arena sourceArena, int amount) {
        if (!sourceArena.isSetup()) {
            player.sendMessage(CC.error("Arena is not fully setup (Missing spawns/corners)."));
            return;
        }

        if (amount < 1 || amount > 50) {
            player.sendMessage(CC.error("Amount must be between 1 and 50."));
            return;
        }

        String targetWorldName = SettingsLocale.ARENA_COPY_WORLD.getString();
        World targetWorld = Bukkit.getWorld(targetWorldName);
        if (targetWorld == null) {
            player.sendMessage(CC.error("Target world '" + targetWorldName + "' is not loaded! Use /neptune setcopyworld <name>"));
            return;
        }

        player.sendMessage(CC.info("Starting copy process for &e" + sourceArena.getDisplayName() + "&7 into world &b" + targetWorld.getName() + "&7..."));
        long startTime = System.currentTimeMillis();

        Location srcMin = sourceArena.getMin();
        Location srcMax = sourceArena.getMax();

        int srcLowX = Math.min(srcMin.getBlockX(), srcMax.getBlockX());
        int srcLowY = Math.min(srcMin.getBlockY(), srcMax.getBlockY());
        int srcLowZ = Math.min(srcMin.getBlockZ(), srcMax.getBlockZ());

        int srcHighX = Math.max(srcMin.getBlockX(), srcMax.getBlockX());
        int srcHighY = Math.max(srcMin.getBlockY(), srcMax.getBlockY());
        int srcHighZ = Math.max(srcMin.getBlockZ(), srcMax.getBlockZ());

        int widthX = srcHighX - srcLowX;
        int heightY = srcHighY - srcLowY;
        int lengthZ = srcHighZ - srcLowZ;

        int nextAvailableX = 0;
        for (Arena existing : ArenaService.get().getArenas()) {
            if (existing.getMin() != null && existing.getMin().getWorld() != null
                    && existing.getMin().getWorld().getName().equals(targetWorld.getName())) {
                nextAvailableX = Math.max(nextAvailableX,
                        Math.max(existing.getMin().getBlockX(), existing.getMax().getBlockX()));
            }
            for (Arena instance : existing.getInstances()) {
                if (instance.getMin() != null && instance.getMin().getWorld() != null
                        && instance.getMin().getWorld().getName().equals(targetWorld.getName())) {
                    nextAvailableX = Math.max(nextAvailableX,
                            Math.max(instance.getMin().getBlockX(), instance.getMax().getBlockX()));
                }
            }
        }
        nextAvailableX += 500;

        int bufferBetweenCopies = 500;

        for (int i = 1; i <= amount; i++) {
            int instanceId = sourceArena.getInstances().size() + 1;
            String copyName = sourceArena.getName() + "#" + instanceId;

            int targetStartX = nextAvailableX + ((widthX + bufferBetweenCopies) * (i - 1));

            Location targetMin = new Location(targetWorld, targetStartX, srcLowY, 0);
            Location targetMax = new Location(targetWorld, targetStartX + widthX, srcHighY, lengthZ);

            for (int x = 0; x <= widthX; x++) {
                for (int y = 0; y <= heightY; y++) {
                    for (int z = 0; z <= lengthZ; z++) {
                        Block sourceBlock = srcMin.getWorld().getBlockAt(srcLowX + x, srcLowY + y, srcLowZ + z);

                        if (sourceBlock.getType() == Material.AIR) continue;

                        Block targetBlock = targetWorld.getBlockAt(targetStartX + x, srcLowY + y, z);
                        targetBlock.setBlockData(sourceBlock.getBlockData(), false);
                    }
                }
            }

            Location newRed = calculateRelativeLocation(sourceArena.getRedSpawn(), srcLowX, srcLowY, srcLowZ, targetMin);
            Location newBlue = calculateRelativeLocation(sourceArena.getBlueSpawn(), srcLowX, srcLowY, srcLowZ, targetMin);

            Arena copy = new Arena(
                    copyName,
                    sourceArena.getDisplayName() + " #" + instanceId,
                    newRed,
                    newBlue,
                    targetMin,
                    targetMax,
                    sourceArena.getBuildLimit(),
                    true,
                    new ArrayList<>(sourceArena.getWhitelistedBlocks()),
                    sourceArena.getDeathY(),
                    sourceArena.getTime()
            );

            copy.setDoneLoading(true);
            sourceArena.getInstances().add(copy);

            player.sendMessage(CC.success("Added instance #" + instanceId + " to arena &f" + sourceArena.getName()));
        }

        ArenaService.get().save();

        long time = System.currentTimeMillis() - startTime;
        player.sendMessage(CC.success("Finished creating " + amount + " copies in " + time + "ms."));
    }

    @Command(name = "arena clearcopies", desc = "Delete all copied instances of an arena", usage = "<arena>")
    @Require("neptune.admin")
    public void clearCopies(@Sender Player player, Arena sourceArena) {
        if (sourceArena == null) {
            player.sendMessage(CC.error("Arena not found."));
            return;
        }

        int removed = sourceArena.getInstances().size();
        for (Arena instance : new ArrayList<>(sourceArena.getInstances())) {
            instance.delete(false, true);
        }
        sourceArena.getInstances().clear();

        ArenaService.get().save();
        player.sendMessage(CC.success("Deleted " + removed + " arena copies for &f" + sourceArena.getName()));
    }

    private Location calculateRelativeLocation(Location original, int minX, int minY, int minZ, Location newBase) {
        double offX = original.getX() - minX;
        double offY = original.getY() - minY;
        double offZ = original.getZ() - minZ;

        return new Location(
                newBase.getWorld(),
                newBase.getBlockX() + offX,
                newBase.getBlockY() + offY,
                newBase.getBlockZ() + offZ,
                original.getYaw(),
                original.getPitch()
        );
    }
}
