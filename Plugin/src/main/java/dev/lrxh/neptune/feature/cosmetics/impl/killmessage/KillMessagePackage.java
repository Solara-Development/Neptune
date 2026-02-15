package dev.lrxh.neptune.feature.cosmetics.impl.killmessage;

import dev.lrxh.api.features.cosmetics.killmessages.IKillMessagePackage;
import dev.lrxh.neptune.feature.cosmetics.impl.CosmeticPackage;
import dev.lrxh.neptune.utils.RandomUtils;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
public class KillMessagePackage extends CosmeticPackage implements IKillMessagePackage {
    private final List<String> messages;

    public KillMessagePackage(
            String name,
            String displayName,
            Material material,
            List<String> description,
            int slot,
            List<String> messages
    ) {
        super(name, displayName, material, description, slot);
        this.messages = messages;
    }

    public String getRandomMessage() {
        return messages.get(RandomUtils.getRandInt(messages.size()));
    }
    public String permission() {
        return "neptune.cosmetics.killmessages." + name.toLowerCase();
    }
}
