package fr.vmarchaud.mineweb.common.injector.router;

import fr.vmarchaud.mineweb.utils.*;
import fr.vmarchaud.mineweb.utils.http.*;
import java.util.regex.*;
import java.util.*;
import fr.vmarchaud.mineweb.utils.regex.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.*;
import io.netty.channel.*;

public class RouteMatcher extends SimpleChannelInboundHandler<FullHttpRequest>
{
    private final List<PatternBinding> getBindings;
    private final List<PatternBinding> putBindings;
    private final List<PatternBinding> postBindings;
    private final List<PatternBinding> deleteBindings;
    private final List<PatternBinding> optionsBindings;
    private final List<PatternBinding> headBindings;
    private final List<PatternBinding> traceBindings;
    private final List<PatternBinding> connectBindings;
    private final List<PatternBinding> patchBindings;
    private Handler<FullHttpResponse, RoutedHttpRequest> noMatchHandler;
    private Handler<Void, RoutedHttpResponse> everyMatchHandler;
    
    public RouteMatcher() {
        this.getBindings = new ArrayList<PatternBinding>();
        this.putBindings = new ArrayList<PatternBinding>();
        this.postBindings = new ArrayList<PatternBinding>();
        this.deleteBindings = new ArrayList<PatternBinding>();
        this.optionsBindings = new ArrayList<PatternBinding>();
        this.headBindings = new ArrayList<PatternBinding>();
        this.traceBindings = new ArrayList<PatternBinding>();
        this.connectBindings = new ArrayList<PatternBinding>();
        this.patchBindings = new ArrayList<PatternBinding>();
    }
    
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        this.serveRequest(ctx, request);
    }
    
    List<PatternBinding> getBindingsForRequest(final FullHttpRequest request) {
        final HttpMethod m = request.getMethod();
        if (m.equals(HttpMethod.GET)) {
            return this.getBindings;
        }
        if (m.equals(HttpMethod.PUT)) {
            return this.putBindings;
        }
        if (m.equals(HttpMethod.POST)) {
            return this.postBindings;
        }
        if (m.equals(HttpMethod.DELETE)) {
            return this.deleteBindings;
        }
        if (m.equals(HttpMethod.OPTIONS)) {
            return this.optionsBindings;
        }
        if (m.equals(HttpMethod.HEAD)) {
            return this.headBindings;
        }
        if (m.equals(HttpMethod.TRACE)) {
            return this.traceBindings;
        }
        if (m.equals(HttpMethod.PATCH)) {
            return this.patchBindings;
        }
        if (m.equals(HttpMethod.CONNECT)) {
            return this.connectBindings;
        }
        return null;
    }
    
    public boolean serveRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (!request.getDecoderResult().isSuccess()) {
            this.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return false;
        }
        return this.route(ctx, request, this.getBindingsForRequest(request));
    }
    
    public RouteMatcher get(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.getBindings);
        return this;
    }
    
    public RouteMatcher put(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.putBindings);
        return this;
    }
    
    public RouteMatcher post(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.postBindings);
        return this;
    }
    
    public RouteMatcher delete(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.deleteBindings);
        return this;
    }
    
    public RouteMatcher options(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.optionsBindings);
        return this;
    }
    
    public RouteMatcher head(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.headBindings);
        return this;
    }
    
    public RouteMatcher trace(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.traceBindings);
        return this;
    }
    
    public RouteMatcher connect(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.connectBindings);
        return this;
    }
    
    public RouteMatcher patch(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.patchBindings);
        return this;
    }
    
    public RouteMatcher all(final String pattern, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addPattern(pattern, handler, this.getBindings);
        addPattern(pattern, handler, this.putBindings);
        addPattern(pattern, handler, this.postBindings);
        addPattern(pattern, handler, this.deleteBindings);
        addPattern(pattern, handler, this.optionsBindings);
        addPattern(pattern, handler, this.headBindings);
        addPattern(pattern, handler, this.traceBindings);
        addPattern(pattern, handler, this.connectBindings);
        addPattern(pattern, handler, this.patchBindings);
        return this;
    }
    
    public RouteMatcher getWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.getBindings);
        return this;
    }
    
    public RouteMatcher putWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.putBindings);
        return this;
    }
    
    public RouteMatcher postWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.postBindings);
        return this;
    }
    
    public RouteMatcher deleteWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.deleteBindings);
        return this;
    }
    
    public RouteMatcher optionsWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.optionsBindings);
        return this;
    }
    
    public RouteMatcher headWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.headBindings);
        return this;
    }
    
    public RouteMatcher traceWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.traceBindings);
        return this;
    }
    
    public RouteMatcher connectWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.connectBindings);
        return this;
    }
    
    public RouteMatcher patchWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.patchBindings);
        return this;
    }
    
    public RouteMatcher allWithRegEx(final String regex, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        addRegEx(regex, handler, this.getBindings);
        addRegEx(regex, handler, this.putBindings);
        addRegEx(regex, handler, this.postBindings);
        addRegEx(regex, handler, this.deleteBindings);
        addRegEx(regex, handler, this.optionsBindings);
        addRegEx(regex, handler, this.headBindings);
        addRegEx(regex, handler, this.traceBindings);
        addRegEx(regex, handler, this.connectBindings);
        addRegEx(regex, handler, this.patchBindings);
        return this;
    }
    
    public RouteMatcher noMatch(final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
        this.noMatchHandler = handler;
        return this;
    }
    
    public RouteMatcher everyMatch(final Handler<Void, RoutedHttpResponse> handler) {
        this.everyMatchHandler = handler;
        return this;
    }
    
    private static void addPattern(final String input, final Handler<FullHttpResponse, RoutedHttpRequest> handler, final List<PatternBinding> bindings) {
        final Matcher m = Pattern.compile(":([A-Za-z][A-Za-z0-9_]*)").matcher(input);
        final StringBuffer sb = new StringBuffer();
        final Set<String> groups = new HashSet<String>();
        while (m.find()) {
            final String group = m.group().substring(1);
            if (groups.contains(group)) {
                throw new IllegalArgumentException("Cannot use identifier " + group + " more than once in pattern string");
            }
            m.appendReplacement(sb, "(?<$1>[^\\/]+)");
            groups.add(group);
        }
        m.appendTail(sb);
        final String regex = sb.toString();
        final PatternBinding binding = new PatternBinding(NamedPattern.compile(regex), groups, handler, null);
        bindings.add(binding);
    }
    
    private static void addRegEx(final String input, final Handler<FullHttpResponse, RoutedHttpRequest> handler, final List<PatternBinding> bindings) {
        final PatternBinding binding = new PatternBinding(NamedPattern.compile(input), null, handler, null);
        bindings.add(binding);
    }
    
    public FullHttpResponse getResponse(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        FullHttpResponse resp = null;
        if (!request.getDecoderResult().isSuccess()) {
            resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            this.sendHttpResponse(ctx, request, resp);
        }
        else {
            resp = this.getResponseForRoute(ctx, request, this.getBindingsForRequest(request));
        }
        return resp;
    }
    
    public FullHttpResponse getResponseForRoute(final ChannelHandlerContext ctx, final FullHttpRequest request, final List<PatternBinding> bindings) {
        final RoutedHttpRequest rreq = new RoutedHttpRequest(ctx, request);
        for (final PatternBinding binding : bindings) {
            final QueryStringDecoder uri = new QueryStringDecoder(request.getUri());
            final NamedMatcher m = binding.pattern.matcher(uri.path());
            if (m.matches()) {
                final Map<String, List<String>> params = new HashMap<String, List<String>>(m.groupCount());
                if (binding.paramNames != null) {
                    for (final String param : binding.paramNames) {
                        final List<String> l = new ArrayList<String>();
                        l.add(m.group(param));
                        params.put(param, l);
                    }
                }
                else {
                    for (int i = 0; i < m.groupCount(); ++i) {
                        final List<String> j = new ArrayList<String>();
                        j.add(m.group(i + 1));
                        params.put("param" + i, j);
                    }
                }
                uri.parameters().putAll(params);
                final FullHttpResponse res = binding.handler.handle(rreq);
                return res;
            }
        }
        if (this.noMatchHandler != null) {
            return this.noMatchHandler.handle(rreq);
        }
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
    
    private boolean route(final ChannelHandlerContext ctx, final FullHttpRequest request, final List<PatternBinding> bindings) {
        final FullHttpResponse res = this.getResponseForRoute(ctx, request, bindings);
        this.sendHttpResponse(ctx, request, res);
        if (this.everyMatchHandler != null) {
            this.everyMatchHandler.handle(new RoutedHttpResponse(request, res));
        }
        return this.noMatchHandler != null;
    }
    
    void sendHttpResponse(final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
        if (res == null) {
            return;
        }
        final ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
    }
    
    private static class PatternBinding
    {
        final NamedPattern pattern;
        final Handler<FullHttpResponse, RoutedHttpRequest> handler;
        final Set<String> paramNames;
        
        private PatternBinding(final NamedPattern pattern, final Set<String> paramNames, final Handler<FullHttpResponse, RoutedHttpRequest> handler) {
            this.pattern = pattern;
            this.paramNames = paramNames;
            this.handler = handler;
        }
    }
}
