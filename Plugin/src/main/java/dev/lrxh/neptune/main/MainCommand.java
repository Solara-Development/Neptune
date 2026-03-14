package dev.lrxh.neptune.main;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns.ShieldPatternCosmetic;
import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns.ShieldPatternPackage;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GithubUtils;

import dev.lrxh.neptune.utils.PlayerUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.ShieldMeta;

import java.util.*;

public class MainCommand {

    @Command(name = "", desc = "")
    @Require("neptune.admin")
    public void help(@Sender Player player) {
        new MainMenu().open(player);
    }

    @Command(name = "setspawn", desc = "")
    @Require("neptune.admin")
    public void setspawn(@Sender Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        player.sendMessage(CC.color("&aSuccessfully set spawn!"));
    }

    @Command(name = "info", desc = "")
    @Require("neptune.admin")
    public void info(@Sender Player player) {
        player.sendMessage(CC.color("&eThis server is running Neptune version: "
                + Neptune.get().getDescription().getVersion()));
        player.sendMessage(CC.color("&eCommit: &f" + GithubUtils.getCommitId()));
        player.sendMessage(CC.color("&eMessage: &f" + GithubUtils.getCommitMessage()));
    }

    @Command(name = "reload", desc = "")
    @Require("neptune.admin")
    public void reload(@Sender CommandSender sender) {
        ConfigService.get().load();
        CosmeticService.get().load();
        HotbarService.get().load();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Profile profile = API.getProfile(p);
            if (profile.getState().equals(ProfileState.IN_GAME)
                    || profile.getState().equals(ProfileState.IN_KIT_EDITOR))
                return;
            HotbarService.get().giveItems(p);
        }

        sender.sendMessage(CC.color("&aSuccessfully reloaded configs!"));
    }

    @Command(name = "stop", desc = "")
    public void stop(@Sender Player player) {
        Neptune.get().setAllowMatches(false);

        for (Match match : MatchService.get().matches) {
            match.resetArena();
        }

        Bukkit.getServer().shutdown();
    }

    @Command(name = "resetkitloadout", desc = "")
    @Require("neptune.admin")
    public void resetkitloadout(@Sender Player player, Kit kit) {
        player.sendMessage(CC.info("Resetting everyone's loadouts..."));
        DatabaseService.get().getDatabase().resetAllKitLoadouts(kit.getName());
        for (Player p : Bukkit.getOnlinePlayers()) {
            Profile profile = API.getProfile(p);
            profile.getGameData().get(kit).setKitLoadout(kit.getItems());
        }
        player.sendMessage(CC.color("&aAll users' kit loadouts have been reset."));
    }

    @Command(name = "addshield", desc = "", usage = "<name>")
    @Require("neptune.admin")
    public void addShield(@Sender Player player, String name) {
        ItemStack holding = player.getInventory().getItemInMainHand();
        ItemMeta holdingMeta = holding.getItemMeta();
        List<Pattern> patterns = null;
        if (holdingMeta instanceof BannerMeta meta) patterns = meta.getPatterns();
        if (holdingMeta instanceof ShieldMeta meta) patterns = meta.getPatterns();
        if (patterns == null || patterns.isEmpty()) {
            PlayerUtil.sendMessage(player.getUniqueId(), CC.error("No patterns found! (Are you holding a banner?)"));
            return;
        }
        List<Object> objectPatterns = new ArrayList<>();
        for (Pattern pattern : patterns) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("DYE", pattern.getColor().name());
            map.put("PATTERN",
                    Objects.requireNonNull(
                            RegistryAccess.registryAccess()
                                    .getRegistry(RegistryKey.BANNER_PATTERN)
                                    .getKey(pattern.getPattern())
                    ).getKey()
            );
            objectPatterns.add(map);
        }
        YamlConfiguration config = ShieldPatternCosmetic.get().getConfig();
        name = name.replaceAll(" ", "_").toUpperCase();
        int highestSlot = 9;
        for (ShieldPatternPackage shieldPatternPackage : ShieldPatternCosmetic.get().getPackages().values()) {
            int slot = shieldPatternPackage.getSlot();
            if (slot > highestSlot) highestSlot = slot;
        }
        config.set("SHIELD_PATTERNS." + name + ".DISPLAY_NAME", name);
        config.set("SHIELD_PATTERNS." + name + ".DESCRIPTION", List.of(name));
        config.set("SHIELD_PATTERNS." + name + ".MATERIAL", "shield");
        config.set("SHIELD_PATTERNS." + name + ".SLOT", highestSlot + 1);
        config.set("SHIELD_PATTERNS." + name + ".PATTERNS", objectPatterns);
        ShieldPatternCosmetic.get().getConfigFile().save();
        PlayerUtil.sendMessage(player.getUniqueId(), CC.success("Added the banner"));
    }
}