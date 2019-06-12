package fr.vmarchaud.mineweb.bungee;

import fr.vmarchaud.mineweb.common.*;
import java.lang.reflect.*;
import net.md_5.bungee.api.config.*;
import io.netty.util.*;
import io.netty.channel.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.protocol.*;
import net.md_5.bungee.*;
import net.md_5.bungee.connection.*;
import net.md_5.bungee.netty.*;
import fr.vmarchaud.mineweb.common.injector.*;

public class BungeeNettyInjector extends NettyInjector
{
    private ICore api;
    
    public BungeeNettyInjector(final ICore api) {
        this.api = api;
    }
    
    @Override
    public synchronized void inject() {
        if (this.injected) {
            throw new IllegalStateException("Cannot inject twice.");
        }
        try {
            final Class<PipelineUtils> server = PipelineUtils.class;
            final Field field = server.getDeclaredField("SERVER_CHILD");
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(null, new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel ch) throws Exception {
                    BungeeNettyInjector.this.injectChannel(ch);
                    final ListenerInfo listener = ch.attr((AttributeKey<ListenerInfo>)PipelineUtils.LISTENER).get();
                    PipelineUtils.BASE.initChannel(ch);
                    ch.pipeline().addBefore("frame-decoder", "legacy-decoder", (ChannelHandler)new LegacyDecoder());
                    ch.pipeline().addAfter("frame-decoder", "packet-decoder", (ChannelHandler)new MinecraftDecoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
                    ch.pipeline().addAfter("frame-prepender", "packet-encoder", (ChannelHandler)new MinecraftEncoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
                    ch.pipeline().addBefore("frame-prepender", "legacy-kick", (ChannelHandler)new KickStringWriter());
                    ch.pipeline().get(HandlerBoss.class).setHandler((PacketHandler)new InitialHandler((ProxyServer)BungeeCord.getInstance(), listener));
                }
            });
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
}
