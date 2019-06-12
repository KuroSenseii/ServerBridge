package fr.vmarchaud.mineweb.bukkit;

import org.bukkit.plugin.java.*;
import fr.vmarchaud.mineweb.common.injector.router.*;
import fr.vmarchaud.mineweb.common.injector.*;
import fr.vmarchaud.mineweb.common.configuration.*;
import fr.vmarchaud.mineweb.common.*;
import org.bukkit.scheduler.*;
import com.google.gson.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;
import fr.vmarchaud.mineweb.common.methods.*;
import fr.vmarchaud.mineweb.bukkit.methods.*;
import fr.vmarchaud.mineweb.utils.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import fr.vmarchaud.mineweb.utils.http.*;
import io.netty.handler.codec.http.*;

public class BukkitCore extends JavaPlugin implements ICore
{
    public static ICore instance;
    private RouteMatcher httpRouter;
    private NettyInjector injector;
    private WebThread nettyServerThread;
    private HashMap<String, IMethod> methods;
    private RequestHandler requestHandler;
    private PluginConfiguration config;
    private ScheduledStorage storage;
    private CommandScheduler commandScheduler;
    private BukkitTask task;
    private HashSet<String> players;
    private Logger logger;
    private Gson gson;
    private FileHandler fileHandler;
    
    public BukkitCore() {
        this.logger = Logger.getLogger("Mineweb");
        this.gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    }
    
    public static ICore get() {
        return BukkitCore.instance;
    }
    
    public void onEnable() {
        BukkitCore.instance = this;
        this.getDataFolder().mkdirs();
        this.config = PluginConfiguration.load(new File(this.getDataFolder(), "config.json"), BukkitCore.instance);
        this.storage = ScheduledStorage.load(new File(this.getDataFolder(), "commands.json"), BukkitCore.instance);
        this.setupLogger();
        this.logger.info("Loading ...");
        this.methods = new HashMap<String, IMethod>();
        this.players = new HashSet<String>();
        if (this.config.port == null) {
            this.injector = new BukkitNettyInjector(this);
        }
        else {
            this.nettyServerThread = new WebThread(this);
        }
        this.httpRouter = new RouteMatcher();
        this.logger.info("Registering route ...");
        this.registerRoutes();
        this.getServer().getPluginManager().registerEvents((Listener)new BukkitListeners(BukkitCore.instance), (Plugin)this);
        if (this.config.port == null) {
            this.logger.info("Injecting http server ...");
            this.injector.inject();
        }
        else {
            this.logger.info("Start http server ...");
            this.nettyServerThread.start();
        }
        this.logger.info("Registering methods ...");
        this.requestHandler = new RequestHandler(BukkitCore.instance);
        this.registerMethods();
        this.logger.info("Starting CommandScheduler ...");
        this.commandScheduler = new CommandScheduler(BukkitCore.instance, this.storage);
        this.task = this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)this.commandScheduler, 0L, 100L);
        this.logger.info("Ready !");
    }
    
    public void onDisable() {
        if (this.task != null) {
            this.task.cancel();
        }
        if (this.commandScheduler != null) {
            this.commandScheduler.save();
        }
        if (this.logger != null) {
            this.logger.info("Shutting down ...");
        }
        if (this.fileHandler != null) {
            this.fileHandler.close();
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("mineweb")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
                if (sender instanceof Player && !sender.isOp() && !sender.hasPermission("mineweb.port")) {
                    return false;
                }
                (this.config = new PluginConfiguration(new File(this.getDataFolder(), "config.json"))).save(BukkitCore.instance);
                sender.sendMessage("MineWebBridge configuration reset!");
                this.logger.info("MineWebBridge configuration reset!");
                return true;
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("port")) {
                if (sender instanceof Player && !sender.isOp() && !sender.hasPermission("mineweb.reset")) {
                    return false;
                }
                this.config.port = Integer.parseInt(args[1]);
                this.config.save(BukkitCore.instance);
                this.nettyServerThread = new WebThread(BukkitCore.instance);
                this.logger.info("Try to start http server ...");
                this.nettyServerThread.start();
                try {
                    TimeUnit.MILLISECONDS.sleep(5L);
                }
                catch (Exception ex) {}
                if (!this.nettyServerThread.isAlive()) {
                    sender.sendMessage("MineWebBridge port setup failed!");
                    this.logger.info("HTTP server start failed!");
                    return true;
                }
                sender.sendMessage("MineWebBridge port setup!");
                this.logger.info("MineWebBridge port setup!");
                return true;
            }
        }
        return false;
    }
    
    public void registerRoutes() {
        this.httpRouter.everyMatch(event -> {
            this.logger.fine(String.format("[HTTP Request] %d %s on %s", event.getRes().getStatus().code(), event.getRequest().getMethod().toString(), event.getRequest().getUri()));
            return null;
        });
        this.httpRouter.get("/", event -> HttpResponseBuilder.ok());
    }
    
    public void registerMethods() {
        this.methods.put("GET_PLAYER_LIST", new CommonGetPlayerList());
        this.methods.put("GET_PLAYER_COUNT", new CommonGetPlayerCount());
        this.methods.put("IS_CONNECTED", new CommonIsConnected());
        this.methods.put("GET_PLUGIN_TYPE", new CommonPluginType());
        this.methods.put("GET_SYSTEM_STATS", new CommonGetSystemStats());
        this.methods.put("RUN_COMMAND", new CommonRunCommand());
        this.methods.put("RUN_SCHEDULED_COMMAND", new CommonScheduledCommand());
        this.methods.put("GET_SERVER_TIMESTAMP", new CommonGetTimestamp());
        this.methods.put("GET_BANNED_PLAYERS", new BukkitGetBannedPlayers());
        this.methods.put("GET_MAX_PLAYERS", new BukkitGetMaxPlayers());
        this.methods.put("GET_MOTD", new BukkitGetMOTD());
        this.methods.put("GET_VERSION", new BukkitGetVersion());
        this.methods.put("GET_WHITELISTED_PLAYERS", new BukkitGetWhitelistedPlayers());
    }
    
    public void setupLogger() {
        try {
            this.logger.setLevel(Level.parse(this.config.logLevel));
            this.logger.setUseParentHandlers(false);
            new File(this.getDataFolder() + File.separator).mkdirs();
            (this.fileHandler = new FileHandler(this.getDataFolder() + File.separator + "mineweb.log", true)).setFormatter(new CustomLogFormatter());
            this.logger.addHandler(this.fileHandler);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public void runCommand(final String command) {
        this.getServer().getScheduler().runTask((Plugin)this, () -> this.getServer().dispatchCommand((CommandSender)this.getServer().getConsoleSender(), command));
    }
    
    public RouteMatcher getHTTPRouter() {
        return this.httpRouter;
    }
    
    public Object getPlugin() {
        return this;
    }
    
    public EnumPluginType getType() {
        return EnumPluginType.BUKKIT;
    }
    
    public Object getGameServer() {
        return this.getServer();
    }
    
    public HashSet<String> getPlayers() {
        return this.players;
    }
    
    public Logger logger() {
        return this.logger;
    }
    
    public Gson gson() {
        return this.gson;
    }
    
    public Map<String, IMethod> getMethods() {
        return this.methods;
    }
    
    public PluginConfiguration config() {
        return this.config;
    }
    
    public RequestHandler requestHandler() {
        return this.requestHandler;
    }
    
    public CommandScheduler getCommandScheduler() {
        return this.commandScheduler;
    }
}
