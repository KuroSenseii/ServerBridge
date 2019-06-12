package fr.vmarchaud.mineweb.common.injector;

import io.netty.handler.codec.*;
import fr.vmarchaud.mineweb.common.*;
import io.netty.buffer.*;
import java.util.*;
import io.netty.handler.codec.http.*;
import io.netty.channel.*;

public class JSONAPIChannelDecoder extends ByteToMessageDecoder
{
    private ICore api;
    
    public JSONAPIChannelDecoder(final ICore api) {
        this.api = api;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buf, final List<Object> list) throws Exception {
        if (buf.readableBytes() < 4) {
            return;
        }
        buf.retain(2);
        final int magic1 = buf.getUnsignedByte(buf.readerIndex());
        final int magic2 = buf.getUnsignedByte(buf.readerIndex() + 1);
        final int magic3 = buf.getUnsignedByte(buf.readerIndex() + 2);
        final int magic4 = buf.getUnsignedByte(buf.readerIndex() + 3);
        final ChannelPipeline p = ctx.channel().pipeline();
        if (this.isHttp(magic1, magic2, magic3, magic4)) {
            final ByteBuf copy = buf.copy();
            ctx.channel().config().setOption(ChannelOption.TCP_NODELAY, true);
            try {
                while (p.removeLast() != null) {}
            }
            catch (NoSuchElementException ex) {}
            p.addLast("codec-http", new HttpServerCodec());
            p.addLast("aggregator", new HttpObjectAggregator(65536));
            p.addLast("handler", new JSONAPIHandler(this.api));
            p.fireChannelRead((Object)copy);
            buf.release();
            buf.release();
        }
        else {
            try {
                p.remove(this);
            }
            catch (NoSuchElementException e) {
                System.out.println("NoSuchElementException");
            }
            buf.release();
            buf.release();
        }
    }
    
    private boolean isHttp(final int magic1, final int magic2, final int magic3, final int magic4) {
        return (magic1 == 71 && magic2 == 69 && magic3 == 84 && magic4 == 32) || (magic1 == 80 && magic2 == 79 && magic3 == 83 && magic4 == 84) || (magic1 == 80 && magic2 == 85 && magic3 == 84 && magic4 == 32) || (magic1 == 72 && magic2 == 69 && magic3 == 65 && magic4 == 68) || (magic1 == 79 && magic2 == 80 && magic3 == 84 && magic4 == 73) || (magic1 == 80 && magic2 == 65 && magic3 == 84 && magic4 == 67) || (magic1 == 68 && magic2 == 69 && magic3 == 76 && magic4 == 69) || (magic1 == 84 && magic2 == 82 && magic3 == 67 && magic4 == 67) || (magic1 == 67 && magic2 == 79 && magic3 == 78 && magic4 == 78);
    }
}
