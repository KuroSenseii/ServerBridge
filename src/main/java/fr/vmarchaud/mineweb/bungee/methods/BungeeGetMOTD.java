package fr.vmarchaud.mineweb.bungee.methods;

import fr.vmarchaud.mineweb.common.*;
import net.md_5.bungee.api.config.*;
import net.md_5.bungee.api.*;

public class BungeeGetMOTD implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        return ((ProxyServer)instance.getGameServer()).getConfigurationAdapter().getListeners().iterator().next().getMotd();
    }
}
