package fr.vmarchaud.mineweb.common.injector;

import fr.vmarchaud.mineweb.common.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.*;
import io.netty.channel.*;

class JSONAPIHandler extends SimpleChannelInboundHandler<Object>
{
    private ICore api;
    
    public JSONAPIHandler(final ICore api) {
        this.api = api;
    }
    
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        this.handleHttpRequest(ctx, (FullHttpRequest)msg);
    }
    
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }
        this.api.getHTTPRouter().serveRequest(ctx, req);
    }
    
    private static void sendHttpResponse(final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
        final ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
