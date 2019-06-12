package fr.vmarchaud.mineweb.common.injector;

import fr.vmarchaud.mineweb.common.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.nio.*;
import io.netty.channel.socket.*;
import io.netty.bootstrap.*;
import io.netty.util.*;
import java.util.*;
import java.net.*;
import io.netty.util.internal.logging.*;
import io.netty.channel.*;
import java.util.concurrent.*;

public class NettyServer
{
    private ICore api;
    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    public NettyServer(final ICore api) {
        this.api = api;
    }
    
    public void start() throws Exception {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap();
            ((AbstractBootstrap<ServerBootstrap, Channel>)b.group(this.bossGroup, this.workerGroup)).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(final SocketChannel ch) throws Exception {
                    final ChannelPipeline p = ch.pipeline();
                    p.addFirst(new JSONAPIChannelDecoder(NettyServer.this.api));
                }
            });
            this.f = b.bind(this.api.config().port).sync();
            this.f.channel().closeFuture().sync();
        }
        finally {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        }
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
    
    public void stop() {
        try {
            this.f.channel().closeFuture().sync();
        }
        catch (Exception ex) {}
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
