package dev.lrxh.neptune;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
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
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.hotbar.listener.ItemListener;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserListener;
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
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.command.ArenaProvider;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureListener;
import dev.lrxh.neptune.game.duel.command.DuelCommand;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.command.KitEditorCommand;
import dev.lrxh.neptune.game.kit.command.KitProvider;
import dev.lrxh.neptune.game.kit.command.StatsCommand;
import dev.lrxh.neptune.game.kit.procedure.KitProcedureListener;
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
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import fr.mrmicky.fastboard.FastManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import com.jonahseguin.drink.annotation.Text;

@Getter
public final class Neptune extends JavaPlugin {
    private static Neptune instance;
    private Cache cache;
    private boolean placeholder = false;
    @Setter
    private boolean allowJoin;
    @Setter
    private boolean allowMatches;

    public static Neptune get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        allowJoin = false;
        allowMatches = false;
        loadManager();
        initAPI();
        allowJoin = true;
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

        loadExtensions();
        if (!isEnabled())
            return;

        new DatabaseService();
        if (!isEnabled())
            return;

        BlockChanger.initialize(this);
        ArenaService.get().load();
        KitService.get().load();
        this.cache = new Cache();
        HotbarService.get().load();

        CosmeticService.get().load();

        DivisionService.get().load();

        LeaderboardService.get().load();

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
                new ArenaProcedureListener(),
                new KitProcedureListener(),
                new ItemBrowserListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
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
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setDifficulty(Difficulty.HARD);
        }
    }

    private void loadTasks() {
        new QueueCheckTask().start(20L);
        new QueueMessageTask().start(100L);
        new LeaderboardTask().start(SettingsLocale.LEADERBOARD_UPDATE_TIME.getInt());
        new ArenaBoundaryCheckTask().start(20L);
        new MenuRunnable().start(20L);
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
        drink.registerCommands();
    }

    @Override
    public void onDisable() {
        stopService(KitService.get(), KitService::save);
        stopService(ArenaService.get(), ArenaService::save);
        stopService(MatchService.get(), MatchService::stopAllGames);
        stopService(TaskScheduler.get(), TaskScheduler::stopAllTasks);
        stopService(ProfileService.get(), ProfileService::saveAll);
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }
}