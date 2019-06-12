package fr.vmarchaud.mineweb.common;

import fr.vmarchaud.mineweb.common.injector.router.*;
import java.util.logging.*;
import com.google.gson.*;
import fr.vmarchaud.mineweb.common.configuration.*;
import java.util.*;

public interface ICore
{
    RouteMatcher getHTTPRouter();
    
    Object getGameServer();
    
    Object getPlugin();
    
    EnumPluginType getType();
    
    Set<String> getPlayers();
    
    default ICore get() {
        return null;
    }
    
    Logger logger();
    
    Gson gson();
    
    PluginConfiguration config();
    
    RequestHandler requestHandler();
    
    Map<String, IMethod> getMethods();
    
    CommandScheduler getCommandScheduler();
    
    void runCommand(final String p0);
    
    public enum EnumPluginType
    {
        BUKKIT("BUKKIT", 0), 
        BUNGEE("BUNGEE", 1);
        
        private EnumPluginType(final String s, final int n) {
        }
    }
}
