package fr.vmarchaud.mineweb.utils.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class RoutedHttpRequest
{
    ChannelHandlerContext ctx;
    private FullHttpRequest request;
    
    public RoutedHttpRequest(final ChannelHandlerContext ctx2, final FullHttpRequest request2) {
        this.ctx = ctx2;
        this.request = request2;
    }
    
    public FullHttpRequest getRequest() {
        return this.request;
    }
    
    public void setRequest(final FullHttpRequest request) {
        this.request = request;
    }
}
