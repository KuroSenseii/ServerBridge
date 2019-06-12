package fr.vmarchaud.mineweb.utils.http;

import io.netty.handler.codec.http.*;
import io.netty.buffer.*;

public class HttpResponseBuilder
{
    private HttpResponseStatus status;
    private byte[] body;
    private EnumContent contentType;
    
    public HttpResponseBuilder() {
        this.status = HttpResponseStatus.OK;
        this.body = new byte[0];
        this.contentType = EnumContent.JSON;
    }
    
    public FullHttpResponse build() {
        final ByteBuf buf = Unpooled.copiedBuffer(this.body);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, this.status, buf);
        response.headers().set("Content-Length", buf.readableBytes());
        response.headers().set("Content-Type", this.contentType.mime);
        return response;
    }
    
    public HttpResponseBuilder code(final HttpResponseStatus status) {
        this.status = status;
        return this;
    }
    
    public HttpResponseBuilder text(final String text) {
        this.body = text.getBytes();
        this.contentType = EnumContent.PLAIN;
        return this;
    }
    
    public HttpResponseBuilder json(final String json) {
        this.body = json.getBytes();
        this.contentType = EnumContent.JSON;
        return this;
    }
    
    public HttpResponseBuilder raw(final byte[] bytes) {
        this.body = bytes;
        this.contentType = EnumContent.BINARY;
        return this;
    }
    
    public static FullHttpResponse ok() {
        final ByteBuf buf = Unpooled.EMPTY_BUFFER;
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set("Content-Length", buf.readableBytes());
        return response;
    }
    
    public static FullHttpResponse status(final HttpResponseStatus status) {
        final ByteBuf buf = Unpooled.EMPTY_BUFFER;
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        response.headers().set("Content-Length", buf.readableBytes());
        return response;
    }
    
    public enum EnumContent
    {
        JSON("JSON", 0, "application/json"), 
        PLAIN("PLAIN", 1, "text/plain"), 
        HTML("HTML", 2, "text/html"), 
        BINARY("BINARY", 3, "application/octet-stream");
        
        String mime;
        
        private EnumContent(final String s, final int n, final String mime) {
            this.mime = mime;
        }
    }
}
