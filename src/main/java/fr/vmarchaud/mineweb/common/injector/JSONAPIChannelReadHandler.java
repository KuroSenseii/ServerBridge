package fr.vmarchaud.mineweb.common.injector;

import io.netty.channel.nio.*;
import fr.vmarchaud.mineweb.common.*;
import java.util.*;
import io.netty.channel.*;
import io.netty.buffer.*;

@ChannelHandler.Sharable
public class JSONAPIChannelReadHandler extends ChannelInboundHandlerAdapter
{
    List<Map.Entry<String, ChannelHandler>> handlers;
    NioEventLoopGroup eventGroup;
    ICore api;
    
    public JSONAPIChannelReadHandler(final ICore api, final NioEventLoopGroup eventGroup) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        this.handlers = new ArrayList<Map.Entry<String, ChannelHandler>>();
        this.eventGroup = eventGroup;
        this.api = api;
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final Channel child = (Channel)msg;
        child.pipeline().addFirst(new JSONAPIChannelDecoder(this.api));
        this.eventGroup.register(child);
        ctx.fireChannelRead(msg);
    }
    
    public class HTTPRequest extends ByteBufInputStream
    {
        public HTTPRequest(final ByteBuf buf) {
            super(buf);
        }
    }
}
