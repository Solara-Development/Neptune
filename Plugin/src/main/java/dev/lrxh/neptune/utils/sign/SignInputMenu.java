package dev.lrxh.neptune.utils.sign;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SignInputMenu {
    static final Map<UUID, SignInputMenu> sessions = new ConcurrentHashMap<>();

    final Location location;
    final Consumer<String> callback;

    private SignInputMenu(Location location, Consumer<String> callback) {
        this.location = location;
        this.callback = callback;
    }

    public static void open(Player player, String prefill, String prompt, Consumer<String> callback) {
        Location loc = player.getLocation().getBlock().getLocation();
        sessions.put(player.getUniqueId(), new SignInputMenu(loc, callback));

        player.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());

        Vector3i position = new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        String p = prompt == null ? "" : prompt;
        String[] lines = {
                prefill == null ? "" : prefill,
                "^^^^^^^^^^^^^^^",
                p.length() > 15 ? p.substring(0, 15) : p,
                p.length() > 15 ? p.substring(15, Math.min(p.length(), 30)) : ""
        };

        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerBlockEntityData(position, BlockEntityTypes.SIGN, signNBT(position, lines)));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerOpenSignEditor(position, true));
    }

    public static void remove(UUID uuid) {
        sessions.remove(uuid);
    }

    private static NBTCompound signNBT(Vector3i position, String[] lines) {
        NBTList<NBTString> messages = NBTList.createStringList();
        for (String line : lines) {
            messages.addTag(new NBTString(line));
        }

        NBTCompound front = new NBTCompound();
        front.setTag("messages", messages);
        front.setTag("color", new NBTString("black"));
        front.setTag("has_glowing_text", new NBTByte((byte) 0));

        NBTCompound nbt = new NBTCompound();
        nbt.setTag("front_text", front);
        nbt.setTag("is_waxed", new NBTByte((byte) 0));
        nbt.setTag("id", new NBTString("minecraft:sign"));
        nbt.setTag("x", new NBTInt(position.getX()));
        nbt.setTag("y", new NBTInt(position.getY()));
        nbt.setTag("z", new NBTInt(position.getZ()));
        return nbt;
    }
}
