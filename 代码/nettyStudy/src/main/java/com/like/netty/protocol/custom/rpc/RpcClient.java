package com.like.netty.protocol.custom.rpc;

import com.like.netty.protocol.custom.handler.rpc.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.like.netty.protocol.custom.handler.LikeChannelMustPipeline.*;

/**
 * Create By like On 2021-04-14 15:18
 */
public class RpcClient {

    private final static Logger log = LoggerFactory.getLogger(RpcClient.class);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        CountDownLatch waitForLogin = new CountDownLatch(1);
        AtomicBoolean loginBol = new AtomicBoolean(false);
        AtomicBoolean exitBol = new AtomicBoolean(false);
        Scanner scanner = new Scanner(System.in);
        AtomicReference<String> username = new AtomicReference<>("null");
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(getLogHandler());
                    ch.pipeline().addLast(getLikeProtocolCodecSharable());
                    ch.pipeline().addLast(getLikeProtocolFrameDecoder());

                    ch.pipeline().addLast(new RpcResponseMessageHandler());
                }
            });
            Channel channel = bootstrap.connect(RpcServer.serverHost, RpcServer.serverPort).sync().channel();
/*
            ChannelFuture cf = channel.writeAndFlush(new RpcRequestMessage(
                    1, "com.like.netty.protocol.custom.server.service.impl.HelloServiceImpl", "hello", String.class.getName(),
                    new String[]{String.class.getName()}, new Object[]{"like"}
            )).addListener(p -> {
                System.out.println(p.isSuccess());
            });*/
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
