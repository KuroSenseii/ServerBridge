package fr.vmarchaud.mineweb.bukkit;

import fr.vmarchaud.mineweb.common.*;
import com.google.common.collect.*;
import com.comphenix.protocol.utility.*;
import com.comphenix.protocol.reflect.*;
import fr.vmarchaud.mineweb.utils.*;
import java.util.*;
import fr.vmarchaud.mineweb.common.injector.*;
import java.lang.reflect.*;
import io.netty.channel.*;

public class BukkitNettyInjector extends NettyInjector
{
    protected List<VolatileField> bootstrapFields;
    protected volatile List<Object> networkManagers;
    private ICore api;
    
    public BukkitNettyInjector(final ICore api) {
        this.bootstrapFields = (List<VolatileField>)Lists.newArrayList();
        this.api = api;
    }
    
    @Override
    public synchronized void inject() {
        if (this.injected) {
            throw new IllegalStateException("Cannot inject twice.");
        }
        try {
            final FuzzyReflection fuzzyServer = FuzzyReflection.fromClass(MinecraftReflection.getMinecraftServerClass());
            final Method serverConnectionMethod = fuzzyServer.getMethodByParameters("getServerConnection", MinecraftReflection.getServerConnectionClass(), new Class[0]);
            final Object server = fuzzyServer.getSingleton();
            final Object serverConnection = serverConnectionMethod.invoke(server, new Object[0]);
            final ChannelInboundHandler endInitProtocol = new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel channel) throws Exception {
                    try {
                        synchronized (BukkitNettyInjector.this.networkManagers) {
                            BukkitNettyInjector.this.injectChannel(channel);
                        }
                        // monitorexit(this.this$0.networkManagers)
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            final ChannelInboundHandler beginInitProtocol = new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel channel) throws Exception {
                    channel.pipeline().addLast(endInitProtocol);
                }
            };
            final ChannelHandler connectionHandler = new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
                    final Channel channel = (Channel)msg;
                    channel.pipeline().addFirst(beginInitProtocol);
                    ctx.fireChannelRead(msg);
                }
            };
            this.networkManagers = (List<Object>)FuzzyReflection.fromObject(serverConnection, true).invokeMethod(null, "getNetworkManagers", List.class, serverConnection);
            this.bootstrapFields = this.getBootstrapFields(serverConnection);
            for (final VolatileField field : this.bootstrapFields) {
                final List<Object> list = (List<Object>)field.getValue();
                if (list == this.networkManagers) {
                    continue;
                }
                field.setValue(new BootstrapList(list, connectionHandler));
            }
            this.injected = true;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to inject channel futures.", e);
        }
    }
    
    @Override
    protected void injectChannel(final Channel channel) {
        channel.pipeline().addFirst(new JSONAPIChannelDecoder(this.api));
    }
    
    protected List<VolatileField> getBootstrapFields(final Object serverConnection) {
        final List<VolatileField> result = (List<VolatileField>)Lists.newArrayList();
        for (final Field field : FuzzyReflection.fromObject(serverConnection, true).getFieldListByType(List.class)) {
            final VolatileField volatileField = new VolatileField(field, serverConnection, true).toSynchronized();
            final List<Object> list = (List<Object>)volatileField.getValue();
            if (list.size() == 0 || list.get(0) instanceof ChannelFuture) {
                result.add(volatileField);
            }
        }
        return result;
    }
    
    public synchronized void close() {
        if (!this.closed) {
            this.closed = true;
            for (final VolatileField field : this.bootstrapFields) {
                final Object value = field.getValue();
                if (value instanceof BootstrapList) {
                    ((BootstrapList)value).close();
                }
                field.revertValue();
            }
        }
    }
}
