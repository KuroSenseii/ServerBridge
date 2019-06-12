package fr.vmarchaud.mineweb.utils.http;

import io.netty.handler.codec.http.*;

public class RoutedHttpResponse
{
    private FullHttpRequest request;
    private FullHttpResponse res;
    
    public RoutedHttpResponse(final FullHttpRequest request2, final FullHttpResponse res2) {
        this.request = request2;
        this.res = res2;
    }
    
    public FullHttpResponse getRes() {
        return this.res;
    }
    
    public void setRes(final FullHttpResponse res) {
        this.res = res;
    }
    
    public FullHttpRequest getRequest() {
        return this.request;
    }
    
    public void setRequest(final FullHttpRequest request) {
        this.request = request;
    }
}
