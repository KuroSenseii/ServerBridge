package fr.vmarchaud.mineweb.bungee;

import net.md_5.bungee.api.plugin.*;
import fr.vmarchaud.mineweb.common.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.*;
import java.util.*;
import net.md_5.bungee.event.*;
import net.md_5.bungee.api.event.*;

public class BungeeListeners implements Listener
{
    private ICore api;
    
    public BungeeListeners(final ICore api) {
        this.api = api;
        final ProxyServer game = (ProxyServer)api.getGameServer();
        for (final ProxiedPlayer player : game.getPlayers()) {
            api.getPlayers().add(player.getName());
        }
    }
    
    @EventHandler
    public void onJoin(final PostLoginEvent e) {
        this.api.getPlayers().add(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onQuit(final PlayerDisconnectEvent e) {
        this.api.getPlayers().remove(e.getPlayer().getName());
    }
}
