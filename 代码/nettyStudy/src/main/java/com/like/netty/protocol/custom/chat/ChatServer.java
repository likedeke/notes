package com.like.netty.protocol.custom.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.like.netty.protocol.custom.handler.LikeChannelPipeline.*;

/**
 * Create By like On 2021-04-11 14:30
 *
 * @Description: 聊天服务器
 */
public class ChatServer {

    /** chart server port */
    public static final int serverPort = 7788;

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap boot = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(getLogHandler());
                                ch.pipeline().addLast(getLikeProtocolCodecSharable());
                                ch.pipeline().addLast(getLikeProtocolFrameDecoder());
                            }
                        });

        boot.bind(serverPort);
    }


}
