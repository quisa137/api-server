package com.jindata.apiserver;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

@Component
public class ApiServer {
    
    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress address;
    
    @Autowired
    @Qualifier("sslSocketAddress")
    private InetSocketAddress sslAddress;

    @Autowired
    @Qualifier("bossThreadCount")
    private int bossThreadCount;
    
    @Autowired
    @Qualifier("workerThreadCount")
    private int workerThreadCount;
    
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount);
        ChannelFuture channelFuture = null;
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ApiServerInitializer(null));
            
            Channel ch = b.bind(address.getPort()).sync().channel();
            
            channelFuture = ch.closeFuture();
            
            //SSL 설정 - 서버 인증서가 있어야 작업 가능, SelfSignedCertificate는 테스트환경에서나 돌려 볼 수 있음
            final SslContext sslCtx;
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                        
            //For HTTPS
            ServerBootstrap b2 = new ServerBootstrap();
            b2.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ApiServerInitializer(sslCtx));
            
            Channel ch2 = b2.bind(sslAddress.getPort()).sync().channel();
            
            channelFuture = ch2.closeFuture();
            channelFuture.sync();
        } catch (CertificateException|InterruptedException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
