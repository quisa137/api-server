package com.jindata.apiserver.core;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

public class ApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage>{
    
    private static final Logger logger = LogManager.getLogger(ApiRequestParser.class);
    private HttpRequest request;
    private JsonObject apiResult;
    
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    
    private HttpPostRequestDecoder decoder;
    
    private Map<String,String> reqHeader = new HashMap<String,String>();
    private Map<String,String> reqData = new HashMap<String,String>();
    
    private static final Set<String> usingHeader = new HashSet<String>();
    static {
        usingHeader.add("token");
        usingHeader.add("email");
    }
    
    public ApiRequestParser() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        // TODO Auto-generated method stub
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest)msg;
            
            if(HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            
            HttpHeaders headers = request.headers();            
            if(!headers.isEmpty()) {
                for(Map.Entry<String, String> h: headers) {
                    String key = h.getKey();
                    reqHeader.put(key, h.getValue());
                }
            }
            InetAddress inetAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress();
            
            reqData.put("REQUEST_URI", request.getUri());
            reqData.put("REQUEST_METHOD", request.getMethod().name());
            reqData.put("REQUEST_CLIENT_IP", inetAddress.getHostAddress());
        }
        
        if (msg instanceof HttpContent) {
            if (msg instanceof LastHttpContent) {
                logger.debug("Last Http Content message received, " +request.getUri());
                LastHttpContent trailer = (LastHttpContent) msg;
                
                readPostData();
                
                ApiRequest service = ServiceDispatcher.dispatch(reqHeader,reqData);
                
                try {
                    service.executeService();
                    
                    apiResult = service.getApiResult();
                } finally {
                    reqData.clear();
                }
            
                
                if(!writeResponse(trailer, ctx)) {
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                        .addListener(ChannelFutureListener.CLOSE);
                }
                reset();
            }
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("요청 처리 완료");
        ctx.flush();
    }
    
    private void reset() {
        request = null;
    }    
    /**
     * JSON 내용을 읽어 Map에 넣을 수 있게 수정 해야 한다. 아니면 JSON Object로 만들어 저장하던지 
     */
    private void readPostData() {
        try {
            decoder = new HttpPostRequestDecoder(factory, request);
            for(InterfaceHttpData data:decoder.getBodyHttpDatas()) {
                if(HttpDataType.Attribute == data.getHttpDataType()) {
                    try {
                        Attribute attribute = (Attribute) data;
                        reqData.put(attribute.getName(), attribute.getValue());
                    } catch (IOException e) {
                        logger.error("BODY attribute: " + data.getHttpDataType().name(), e);
                        return;
                    }
                }else{
                    logger.info("BODY data : " + data.getHttpDataType().name() + ": " + data);
                }
            }
        } catch(ErrorDataDecoderException e) {
            logger.error(e);
        } finally {
            if(decoder != null) {
                decoder.destroy();
            }
        }
    }
    

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.copiedBuffer(
                        apiResult.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // -
            // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause);
        ctx.close();
    }
}
