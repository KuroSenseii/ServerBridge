package fr.vmarchaud.mineweb.bukkit;

import org.bukkit.*;
import org.bukkit.entity.*;
import java.util.*;
import java.lang.reflect.*;

public class BukkitUtils
{
    public static List<Player> getPlayerList(final Server server) {
        final List<Player> players = new ArrayList<Player>();
        try {
            final Method getCount = server.getClass().getMethod("getOnlinePlayers", (Class<?>[])new Class[0]);
            if (getCount.getReturnType() == Array.class) {
                players.addAll(Arrays.asList((Player[])getCount.invoke(server, new Object[0])));
            }
            else {
                players.addAll((Collection<? extends Player>)getCount.invoke(server, new Object[0]));
            }
        }
        catch (Exception ex) {}
        return players;
    }
}
