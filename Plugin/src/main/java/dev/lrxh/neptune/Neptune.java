package dev.lrxh.neptune;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import com.jonahseguin.drink.annotation.Text;
import com.jonahseguin.drink.provider.spigot.UUIDProvider;
import dev.lrxh.api.NeptuneAPI;
import dev.lrxh.api.NeptuneAPIImpl;
import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.neptune.cache.Cache;
import dev.lrxh.neptune.commands.FollowCommand;
import dev.lrxh.neptune.commands.LeaveCommand;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.command.CosmeticsCommand;
import dev.lrxh.neptune.feature.customkit.command.CustomKitCommand;
import dev.lrxh.neptune.feature.customkit.listener.CustomKitListener;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.event.command.EventCommand;
import dev.lrxh.neptune.feature.event.listener.EventListener;
import dev.lrxh.neptune.feature.event.task.EventScheduleTask;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.hotbar.listener.ItemListener;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.command.LeaderboardCommand;
import dev.lrxh.neptune.feature.leaderboard.task.LeaderboardTask;
import dev.lrxh.neptune.feature.party.command.PartyCommand;
import dev.lrxh.neptune.feature.queue.command.QueueCommand;
import dev.lrxh.neptune.feature.queue.command.QueueMenuCommand;
import dev.lrxh.neptune.feature.queue.command.QuickQueueCommand;
import dev.lrxh.neptune.feature.queue.tasks.QueueCheckTask;
import dev.lrxh.neptune.feature.queue.tasks.QueueMessageTask;
import dev.lrxh.neptune.feature.settings.Setting;
import dev.lrxh.neptune.feature.settings.command.SettingProvider;
import dev.lrxh.neptune.feature.settings.command.SettingsCommand;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaDuplicator;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.command.ArenaProvider;
import dev.lrxh.neptune.game.arena.listener.ArenaEditorChatListener;
import dev.lrxh.neptune.game.duel.command.DuelCommand;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.command.KitEditorCommand;
import dev.lrxh.neptune.game.kit.command.KitProvider;
import dev.lrxh.neptune.game.kit.command.StatsCommand;
import dev.lrxh.neptune.game.kit.listener.KitEditorChatListener;
import dev.lrxh.neptune.game.kit.listener.KitEditorListener;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.commands.MatchHistoryCommand;
import dev.lrxh.neptune.game.match.commands.SpectateCommand;
import dev.lrxh.neptune.game.match.listener.MatchListener;
import dev.lrxh.neptune.game.match.tasks.ArenaBoundaryCheckTask;
import dev.lrxh.neptune.main.MainCommand;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.providers.listeners.GlobalListener;
import dev.lrxh.neptune.providers.placeholder.PlaceholderImpl;
import dev.lrxh.neptune.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.scoreboard.ScoreboardService;
import dev.lrxh.neptune.utils.GithubUtils;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.menu.MenuListener;
import dev.lrxh.neptune.utils.menu.MenuRunnable;
import dev.lrxh.neptune.utils.sign.SignInputListener;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import fr.mrmicky.fastboard.FastManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Difficulty;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public final class Neptune extends JavaPlugin {
    private static Neptune instance;
    private Cache cache;
    private boolean placeholder = false;
    @Setter
    private boolean allowMatches;
    private boolean arenaGenerationDisabled;
    private boolean duplicatesEnabled;

    private boolean errored;

    public static Neptune get() {
        return instance;
    }

    public void setErrored() {
        errored = true;
    }

    @Override
    public void onEnable() {
        instance = this;
        allowMatches = false;
        loadManager();
        initAPI();
        registerPermissions();
        allowMatches = true;
    }

    private void initAPI() {
        getServer().getServicesManager().register(
                NeptuneAPI.class,
                new NeptuneAPIImpl(ProfileService.get(), MatchService.get(), KitService.get(), ScoreboardService.get(),
                        ArenaService.get(), DivisionService.get(), CosmeticService.get(), ItemBrowserService.get()),
                this,
                ServicePriority.Normal);
        ServerUtils.info("Neptune API Initialized");
    }

    private void loadManager() {
        ConfigService.get().load();
        arenaGenerationDisabled = !SettingsLocale.DYNAMIC_ARENA_GENERATION.getBoolean();

        loadExtensions();
        if (!isEnabled())
            return;

        new DatabaseService();
        if (!isEnabled())
            return;

        BlockChanger.initialize(this);
        ArenaService.get().load();
        if (arenaGenerationDisabled) {
            duplicatesEnabled = ArenaDuplicator.isAvailable();
            if (duplicatesEnabled) {
                ArenaService.get().setupDuplicatesWorld();
                ArenaService.get().loadDuplicates();
            } else {
                ServerUtils.error("FastAsyncWorldEdit is not installed - arena duplicates are disabled.");
            }
        }
        KitService.get().load();
        this.cache = new Cache();
        HotbarService.get().load();
        CosmeticService.get().load();
        DivisionService.get().load();
        LeaderboardService.get().load();
        ItemBrowserService.get().preloadSections();

        registerListeners();
        loadCommandManager();
        loadTasks();
        loadWorlds();

        if (ScoreboardLocale.ENABLED_SCOREBOARD.getBoolean()) {
            new FastManager(this, new ScoreboardAdapter());
        }

        GithubUtils.loadGitInfo();

        ServerUtils.info("Loaded Successfully");
    }

    private void registerListeners() {
        Arrays.asList(
                        new ProfileListener(),
                        new MatchListener(),
                        new GlobalListener(),
                        new ItemListener(),
                        new MenuListener(),
                        new ArenaEditorChatListener(),
                        new KitEditorChatListener(),
                        new KitEditorListener(),
                        new CustomKitListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        PacketEvents.getAPI().getEventManager().registerListener(new SignInputListener(), PacketListenerPriority.NORMAL);
    }

    private void loadExtensions() {
        placeholder = loadExtension("PlaceholderAPI");
        if (placeholder) {
            ServerUtils.info("Placeholder API found, loading expansion.");
            new PlaceholderImpl(this).register();
        }
    }

    private boolean loadExtension(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private void loadWorlds() {
        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
            world.setGameRule(GameRules.ADVANCE_TIME, false);
            world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
            world.setDifficulty(Difficulty.HARD);
        }
    }

    private void loadTasks() {
        new QueueCheckTask().start(20L);
        new QueueMessageTask().start(100L);
        new LeaderboardTask().start(SettingsLocale.LEADERBOARD_UPDATE_TIME.getInt());
        new ArenaBoundaryCheckTask().start(20L);
        new MenuRunnable().start(20L);
        if (SettingsLocale.EVENT_AUTO_SCHEDULE_ENABLED.getBoolean()) new EventScheduleTask().start(20L);
    }

    private void loadCommandManager() {
        CommandService drink = Drink.get(this);
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Arena.class).toProvider(new ArenaProvider());
        drink.bind(UUID.class).toProvider(new UUIDProvider());
        drink.bind(Setting.class).toProvider(new SettingProvider());
        drink.bind(Kit.class).annotatedWith(Text.class).toProvider(new KitProvider());

        drink.register(new KitEditorCommand(), "kiteditor").setDefaultCommandIsHelp(true);
        drink.register(new StatsCommand(), "stats").setDefaultCommandIsHelp(true);
        drink.register(new PartyCommand(), "party", "p");
        drink.register(new FollowCommand(), "follow");
        drink.register(new QueueCommand(), "queue").registerSub(new QueueMenuCommand());
        drink.register(new DuelCommand(), "duel", "1v1").setDefaultCommandIsHelp(true);
        drink.register(new LeaveCommand(), "leave", "forfeit", "spawn", "l", "ff");
        drink.register(new LeaderboardCommand(), "leaderboard", "lbs", "lb", "leaderboard")
                .setDefaultCommandIsHelp(true);
        drink.register(new SettingsCommand(), "settings").setDefaultCommandIsHelp(true);
        drink.register(new SpectateCommand(), "spec", "spectate");
        drink.register(new MainCommand(), "neptune");
        drink.register(new CosmeticsCommand(), "cosmetics");
        drink.register(new MatchHistoryCommand(), "matchhistory").setDefaultCommandIsHelp(true);
        drink.register(new QuickQueueCommand(), "quickqueue");
        drink.register(new CustomKitCommand(), "customkits", "ck");
        drink.register(new EventCommand(), "event");
        drink.registerCommands();
    }

    @Override
    public void onDisable() {
        if (!errored) {
            stopService(KitService.get(), KitService::save);
            stopService(ArenaService.get(), ArenaService::save);
        }
        stopService(MatchService.get(), MatchService::stopAllGames);
        stopService(TaskScheduler.get(), TaskScheduler::stopAllTasks);
        stopService(ProfileService.get(), ProfileService::saveAll);
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }

    /**
     * Registers all cosmetic trim permissions.
     */
    private void registerPermissions() {
        // Patterns
        String[] patterns = {
            "bolt","coast","dune","eye","flow","host","raiser","rib",
            "sentry","shaper","silence","snout","spire","tide","vex",
            "wayfinder","ward","wild"
        };
        // Materials
        String[] materials = {
            "amethyst","copper","diamond","emerald","gold","iron",
            "lapis","netherite","quartz","redstone"
        };
        // Armor slots
        String[] slots = {"helmet","chestplate","leggings","boots"};

        // Register individual permissions first.
        for (String p : patterns) {
            safeAddPermission("neptune.cosmetics.trim.pattern." + p, PermissionDefault.FALSE);
        }
        for (String m : materials) {
            safeAddPermission("neptune.cosmetics.trim.material." + m, PermissionDefault.FALSE);
        }
        for (String s : slots) {
            safeAddPermission("neptune.cosmetics.trim." + s, PermissionDefault.FALSE);
        }

        // Wildcard. grant all patterns.
        java.util.Map<String, Boolean> patternChildren = new java.util.HashMap<>();
        for (String p : patterns) patternChildren.put("neptune.cosmetics.trim.pattern." + p, true);
        safeAddPermission("neptune.cosmetics.trim.pattern.*", PermissionDefault.FALSE, patternChildren);

        // Wildcard. grant all materials.
        java.util.Map<String, Boolean> materialChildren = new java.util.HashMap<>();
        for (String m : materials) materialChildren.put("neptune.cosmetics.trim.material." + m, true);
        safeAddPermission("neptune.cosmetics.trim.material.*", PermissionDefault.FALSE, materialChildren);

        // Root wildcard. grants all.
        java.util.Map<String, Boolean> rootChildren = new java.util.HashMap<>();
        rootChildren.put("neptune.cosmetics.trim.pattern.*", true);
        rootChildren.put("neptune.cosmetics.trim.material.*", true);
        for (String s : slots) rootChildren.put("neptune.cosmetics.trim." + s, true);
        safeAddPermission("neptune.cosmetics.trim.*", PermissionDefault.FALSE, rootChildren);

        // Admin permission
        safeAddPermission("neptune.admin", PermissionDefault.OP);
    }

    private void safeAddPermission(String node, PermissionDefault def) {
        safeAddPermission(node, def, null);
    }

    private void safeAddPermission(String node, PermissionDefault def,
                                   java.util.Map<String, Boolean> children) {
        try {
            if (getServer().getPluginManager().getPermission(node) != null) return;
            Permission perm = children != null
                    ? new Permission(node, def, children)
                    : new Permission(node, def);
            getServer().getPluginManager().addPermission(perm);
        } catch (Exception ignored) {
        
        }
    }
}