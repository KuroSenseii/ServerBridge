package fr.vmarchaud.mineweb.bungee;

import fr.vmarchaud.mineweb.common.injector.router.*;
import fr.vmarchaud.mineweb.common.injector.*;
import fr.vmarchaud.mineweb.common.configuration.*;
import fr.vmarchaud.mineweb.common.*;
import net.md_5.bungee.api.scheduler.*;
import com.google.gson.*;
import net.md_5.bungee.api.plugin.*;
import java.util.concurrent.*;
import fr.vmarchaud.mineweb.common.methods.*;
import fr.vmarchaud.mineweb.bungee.methods.*;
import fr.vmarchaud.mineweb.utils.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import fr.vmarchaud.mineweb.utils.http.*;
import io.netty.handler.codec.http.*;

public class BungeeCore extends Plugin implements ICore
{
    public static ICore instance;
    private RouteMatcher httpRouter;
    private NettyInjector injector;
    private HashMap<String, IMethod> methods;
    private RequestHandler requestHandler;
    private PluginConfiguration config;
    private ScheduledStorage storage;
    private CommandScheduler commandScheduler;
    private ScheduledTask task;
    private HashSet<String> players;
    private Logger logger;
    private Gson gson;
    private FileHandler fileHandler;
    
    public BungeeCore() {
        this.logger = Logger.getLogger("Mineweb");
        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }
    
    public static ICore get() {
        return BungeeCore.instance;
    }
    
    public void onEnable() {
        BungeeCore.instance = this;
        this.config = PluginConfiguration.load(new File(this.getDataFolder(), "config.json"), BungeeCore.instance);
        this.storage = ScheduledStorage.load(new File(this.getDataFolder(), "commands.json"), BungeeCore.instance);
        this.setupLogger();
        this.logger.info("Loading ...");
        this.methods = new HashMap<String, IMethod>();
        this.players = new HashSet<String>();
        this.injector = new BungeeNettyInjector(this);
        this.httpRouter = new RouteMatcher();
        this.logger.info("Registering route ...");
        this.registerRoutes();
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new BungeeListeners(BungeeCore.instance));
        this.logger.info("Injecting http server ...");
        this.injector.inject();
        this.logger.info("Registering methods ...");
        this.requestHandler = new RequestHandler(BungeeCore.instance);
        this.registerMethods();
        this.logger.info("Starting CommandScheduler ...");
        this.commandScheduler = new CommandScheduler(BungeeCore.instance, this.storage);
        this.task = this.getProxy().getScheduler().schedule((Plugin)this, (Runnable)this.commandScheduler, 5L, TimeUnit.SECONDS);
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
        this.methods.put("GET_MAX_PLAYERS", new BungeeGetMaxPlayers());
        this.methods.put("GET_MOTD", new BungeeGetMOTD());
        this.methods.put("GET_VERSION", new BungeeGetVersion());
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
    
    public RouteMatcher getHTTPRouter() {
        return this.httpRouter;
    }
    
    public Object getGameServer() {
        return this.getProxy();
    }
    
    public Object getPlugin() {
        return this;
    }
    
    public EnumPluginType getType() {
        return EnumPluginType.BUNGEE;
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
    
    public void runCommand(final String command) {
        this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), command);
    }
    
    public CommandScheduler getCommandScheduler() {
        return this.commandScheduler;
    }
}
