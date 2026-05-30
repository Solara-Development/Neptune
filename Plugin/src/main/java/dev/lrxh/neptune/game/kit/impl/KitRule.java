package dev.lrxh.neptune.game.kit.impl;

import dev.lrxh.api.kit.IKitRule;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum KitRule implements IKitRule {
    HIDDEN(Material.REDSTONE_TORCH, "If the kit shouldn't appear in the queue and party events menu (Used mainly for FFA-only kits)", "Hidden", "hidden", false),
    BUILD(Material.LAVA_BUCKET, "Allow/Deny Players to place and break blocks placed by them.", "Build", "build"),
    HUNGER(Material.COOKED_BEEF, "Allow/Deny Players losing Hunger", "Hunger", "hunger"),
    SUMO(Material.LEAD, "Allow/Deny Sumo", "Sumo", "sumo"),
    FALL_DAMAGE(Material.DIAMOND, "Allow/Deny Player to take Fall Damage", "Fall Damage", "fallDamage"),
    DENY_MOVEMENT(Material.WATER_BUCKET, "Allow/Deny Movement on match start", "Deny Movement", "denyMovement"),
    BOXING(Material.DIAMOND_CHESTPLATE, "Allow/Deny Boxing", "Boxing", "boxing"),
    ALLOW_ARENA_BREAK(Material.WOODEN_AXE, "Allow Players to break blocks from the arena (They need to be whitelisted)", "Arena Break", "arenaBreak"),
    DAMAGE(Material.DIAMOND_SWORD, "Allow/Deny Players to take Damage", "Damage", "damage"),
    BEST_OF_THREE(Material.GOLDEN_AXE, "If enabled Players would need to win 3 times", "Best of 3", "bestOfThree"),
    SATURATION_HEAL(Material.GOLDEN_APPLE, "If Players should regen from saturation", "Natural Regeneration", "saturationHeal"),
    SHOW_HP(Material.APPLE, "If players should see their enemies health under their name", "Show Health", "showHP"),
    ALLOW_KIT_EDITOR(Material.WOODEN_AXE, "If players should be able to modify this kit in kiteditor.", "Allow Kit Editor", "allowKitEditor"),
    ALLOW_PARTY(Material.PARROT_SPAWN_EGG, "If players should be able to play this kit in party events.", "Allow Party Events", "allowParty"),
    BED_WARS(Material.RED_BED, "Allow/Deny Bedwars", "Bedwars", "bedwars"),
    PARKOUR(Material.ENDER_PEARL, "Allow/Deny Parkour", "Parkour", "parkour"),
    RESET_ARENA_AFTER_ROUND(Material.BARRIER, "If the arena should be reset after the round", "Reset Arena After Round", "resetArenaAfterMatch", false),
    DROPPER(Material.DROPPER, "Allow/Deny Dropper", "Dropper", "dropper"),
    ENDERPEARL_COOLDOWN(Material.ENDER_PEARL, "Enderpearls have a 15s delay when being used.", "Enderpearl cooldown", "enderpearlCooldown"),
    SATURATION(Material.ENCHANTED_GOLDEN_APPLE, "Allow/Deny Saturation from eating", "Saturation", "saturation"),
    AUTO_IGNITE(Material.TNT, "Whether to automatically ignite TNT / creepers", "Auto Ignite", "autoIgnite");
    private final String description;
    private final String name;
    private final Material icon;
    private final String saveName;
    private final boolean customKit;


    KitRule(Material icon, String description, String name, String saveName) {
        this(icon, description, name, saveName, true);
    }

    KitRule(Material icon, String description, String name, String saveName, boolean customKit) {
        this.description = description;
        this.name = name;
        this.saveName = saveName;
        this.icon = icon;
        this.customKit = customKit;
    }
}