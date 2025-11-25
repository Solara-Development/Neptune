package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class ItemUtils {

    public ItemStack[] color(ItemStack[] itemStackList, Color color) {
        ItemStack[] items = new ItemStack[itemStackList.length];

        for (int i = 0; i < itemStackList.length; i++) {
            ItemStack itemStack = itemStackList[i];

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.LEATHER_BOOTS || itemStack.getType() == Material.LEATHER_CHESTPLATE
                    || itemStack.getType() == Material.LEATHER_HELMET
                    || itemStack.getType() == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            } else if (itemStack.getType().name().contains("WOOL")) {
                itemStack.setType(color.equals(Color.BLUE) ? Material.BLUE_WOOL : Material.RED_WOOL);
            }

            items[i] = itemStack;
        }

        return items;
    }

    public String serialize(List<ItemStack> items) {
        if (items == null) {
            items = new ArrayList<>();
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(
                        new GZIPOutputStream(outputStream))) {

            dataOutput.writeInt(items.size());

            for (ItemStack item : items) {
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                dataOutput.writeObject(item);
            }

            dataOutput.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            ServerUtils.error("Occurred while saving items " + e.getMessage());
            return null;
        }
    }

    public String serialize(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(
                        new GZIPOutputStream(outputStream))) {
            dataOutput.writeObject(item);
            dataOutput.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            ServerUtils.error("Occurred while saving item " + e.getMessage());
            return null;
        }
    }

    public List<ItemStack> deserialize(String base64) {
        List<ItemStack> items = new ArrayList<>();
        if (base64 == null)
            return items;
        byte[] data = Base64.getDecoder().decode(base64);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new GZIPInputStream(inputStream))) {

            int size = dataInput.readInt();

            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) dataInput.readObject();
                items.add(item);
            }

            return items;
        } catch (IOException | ClassNotFoundException e) {
            ServerUtils.error("Occurred while loading items " + e.getMessage());
            return null;
        }
    }

    public List<String> getLore(List<String> lore, Replacement... replacements) {
        List<String> newLore = new ArrayList<>();

        for (String line : lore) {
            String modifiedLine = line;
            for (Replacement replacement : replacements) {
                if (modifiedLine.contains(replacement.getPlaceholder())) {
                    modifiedLine = modifiedLine.replace(
                            replacement.getPlaceholder(),
                            MiniMessage.miniMessage().serialize(replacement.getReplacement()));
                }
            }

            List<String> splitLines = Arrays.asList(modifiedLine.split("<br>"));
            newLore.addAll(splitLines);
        }

        return newLore;
    }

    public ItemStack deserializeItem(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new GZIPInputStream(inputStream))) {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            ServerUtils.error("Occurred while loading item!");
            return null;
        }
    }
}
