package fr.vmarchaud.mineweb.common;

import javax.crypto.spec.*;
import java.nio.charset.*;
import io.netty.handler.codec.http.*;
import io.netty.buffer.*;
import com.google.gson.reflect.*;
import fr.vmarchaud.mineweb.common.interactor.requests.*;
import fr.vmarchaud.mineweb.utils.*;
import com.google.gson.stream.*;
import java.io.*;
import com.google.gson.*;
import fr.vmarchaud.mineweb.common.interactor.responses.*;
import java.lang.reflect.*;
import java.util.*;
import fr.vmarchaud.mineweb.utils.http.*;

public class RequestHandler
{
    private ICore api;
    private SecretKeySpec key;
    private boolean debug;
    
    public RequestHandler(final ICore api) {
        this.api = api;
        if (api.config().secretkey != null) {
            this.key = new SecretKeySpec(api.config().secretkey.getBytes(), "AES");
        }
        api.getHTTPRouter().post("/handshake", request -> this.handleHandshake(request.getRequest()));
        api.getHTTPRouter().post("/ask", request -> this.handle(request.getRequest()));
        this.debug = (System.getenv("DEBUG") != null && System.getenv("DEBUG").equals("true"));
    }
    
    public void refreshKey(final String secretKey) {
        this.key = new SecretKeySpec(secretKey.getBytes(), "AES");
    }
    
    public FullHttpResponse handleHandshake(final FullHttpRequest httpRequest) {
        final ByteBuf buf = httpRequest.content();
        final String content = buf.toString(buf.readerIndex(), buf.readableBytes(), Charset.forName("UTF-8"));
        final HandshakeRequest handshake = this.api.gson().fromJson(content, HandshakeRequest.class);
        this.api.logger().info(String.format("New Handshake id: %s (%s, %s)", handshake.getId(), handshake.getSecretKey(), handshake.getDomain()));
        if (!handshake.isValid()) {
            this.api.logger().info(String.format("Handshake failed id: %s (reason: invalid params)", handshake.getId()));
            return new HttpResponseBuilder().code(HttpResponseStatus.BAD_REQUEST).build();
        }
        if (this.api.config().secretkey != null) {
            this.api.logger().info(String.format("Handshake failed id: %s (reason: already linked)", handshake.getId()));
            return new HttpResponseBuilder().code(HttpResponseStatus.FORBIDDEN).build();
        }
        try {
            final HandshakeResponse response = new HandshakeResponse();
            final String secret = handshake.getSecretKey();
            this.api.config().secretkey = secret;
            this.api.config().domain = handshake.getDomain();
            this.api.config().save(this.api);
            response.setMsg("Successfully retrieved secret key, now ready !");
            response.setStatus(true);
            this.api.logger().info(String.format("Handshake request %s has been successfully valided (secret: %s)", handshake.getId(), secret));
            this.refreshKey(secret);
            return new HttpResponseBuilder().code(HttpResponseStatus.OK).json(this.api.gson().toJson(response)).build();
        }
        catch (Exception e) {
            if (this.debug) {
                e.printStackTrace();
            }
            return new HttpResponseBuilder().code(HttpResponseStatus.INTERNAL_SERVER_ERROR).json(this.api.gson().toJson(e.getMessage())).build();
        }
    }
    
    public FullHttpResponse handle(final FullHttpRequest httpRequest) {
        if (this.api.config().secretkey == null) {
            this.api.logger().severe("Secret key isnt defined, please setup like wrote in the mineweb documentation.");
            return new HttpResponseBuilder().code(HttpResponseStatus.NOT_IMPLEMENTED).build();
        }
        final ByteBuf buf = httpRequest.content();
        final String content = buf.toString(buf.readerIndex(), buf.readableBytes(), Charset.forName("UTF-8"));
        final JsonArray response = new JsonArray();
        final Type token = new TypeToken<List<Command>>() {}.getType();
        AskRequest request;
        List<Command> requests;
        try {
            request = this.api.gson().fromJson(content, AskRequest.class);
            final String tmp = this.debug ? request.getSigned() : CryptoUtils.decryptAES(request.getSigned(), this.key, request.getIv());
            final JsonReader reader = new JsonReader(new StringReader(tmp));
            reader.setLenient(true);
            requests = this.api.gson().fromJson(reader, token);
        }
        catch (Exception e) {
            this.api.logger().severe(String.format("Cant decipher/parse a request : %s", e.getMessage()));
            if (this.debug) {
                e.printStackTrace();
            }
            return HttpResponseBuilder.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        for (final Command command : requests) {
            final IMethod method = this.api.getMethods().get(command.getName());
            final Object[] inputs = command.getArgs();
            if (method == null) {
                final JsonObject res = new JsonObject();
                res.addProperty("name", command.getName());
                res.addProperty("response", "NOT_FOUND");
                response.add(res);
            }
            else {
                final MethodHandler annot = method.getClass().getDeclaredAnnotation(MethodHandler.class);
                if (annot == null) {
                    final JsonObject res2 = new JsonObject();
                    res2.addProperty("name", command.getName());
                    res2.addProperty("response", "INVALID_IMPLEMENTED_METHOD");
                    response.add(res2);
                }
                else if (annot.inputs() != inputs.length) {
                    final JsonObject res2 = new JsonObject();
                    res2.addProperty("name", command.getName());
                    res2.addProperty("response", "BAD_REQUEST_ARGS_LENGTH");
                    response.add(res2);
                }
                else {
                    if (annot.inputs() > 0) {
                        boolean valid = true;
                        for (int i = 0; i < annot.types().length; ++i) {
                            if (this.debug) {
                                this.api.logger().fine("Comparing input " + inputs[i] + " of class " + inputs[i].getClass().getName() + " to class " + annot.types()[i].getName());
                            }
                            if (!inputs[i].getClass().getName().equals(annot.types()[i].getName())) {
                                valid = false;
                                break;
                            }
                        }
                        if (!valid) {
                            final JsonObject res3 = new JsonObject();
                            res3.addProperty("name", command.getName());
                            res3.addProperty("response", "BAD_REQUEST_ARGS_TYPE");
                            response.add(res3);
                            continue;
                        }
                    }
                    final Object output = method.execute(this.api, inputs);
                    final JsonObject res3 = new JsonObject();
                    res3.addProperty("name", command.getName());
                    res3.add("response", this.api.gson().toJsonTree(output));
                    response.add(res3);
                }
            }
        }
        this.api.logger().fine(String.format("request %s : %s", httpRequest.hashCode(), this.api.gson().toJson(requests)));
        this.api.logger().fine(String.format("response %s : %s", httpRequest.hashCode(), this.api.gson().toJson(response)));
        try {
            final AskResponse askResponse = new AskResponse();
            final String json = this.api.gson().toJson(response);
            askResponse.setSigned(this.debug ? json : CryptoUtils.encryptAES(json, this.key, request.getIv()));
            askResponse.setIv(request.getIv());
            return new HttpResponseBuilder().json(this.api.gson().toJson(askResponse)).code(HttpResponseStatus.OK).build();
        }
        catch (Exception e) {
            this.api.logger().severe(String.format("Cant cipher/serialize a response : %s", e.getMessage()));
            return HttpResponseBuilder.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
