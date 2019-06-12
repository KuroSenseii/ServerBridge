package fr.vmarchaud.mineweb.bukkit;

import fr.vmarchaud.mineweb.common.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class BukkitListeners implements Listener
{
    private ICore api;
    
    public BukkitListeners(final ICore api) {
        this.api = api;
        final Server game = (Server)api.getGameServer();
        for (final Player player : BukkitUtils.getPlayerList(game)) {
            api.getPlayers().add(player.getName());
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        this.api.getPlayers().add(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.api.getPlayers().remove(e.getPlayer().getName());
    }
}
