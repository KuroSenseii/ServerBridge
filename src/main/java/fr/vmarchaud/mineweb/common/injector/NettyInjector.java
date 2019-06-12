package fr.vmarchaud.mineweb.common.injector;

import io.netty.channel.*;

public abstract class NettyInjector
{
    protected boolean injected;
    protected boolean closed;
    
    public abstract void inject();
    
    protected abstract void injectChannel(final Channel p0);
}
