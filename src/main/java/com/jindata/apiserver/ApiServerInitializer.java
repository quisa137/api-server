package com.jindata.apiserver;

import com.jindata.apiserver.core.ApiRequestParser;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class ApiServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    
    public ApiServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if(sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }        
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpContentCompressor());
        p.addLast(new ApiRequestParser());
    }

}
