package com.like.netty.protocol.custom.chat;

import com.like.netty.protocol.custom.handler.HeatBeatPongMessageHandler;
import com.like.netty.protocol.custom.message.chat.*;
import com.like.netty.protocol.custom.server.service.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.like.netty.protocol.custom.handler.LikeChannelMustPipeline.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Create By like On 2021-04-11 15:49
 * 聊天客户端
 */
public class ChatClient {
    private final static Logger log = getLogger(ChatClient.class);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        CountDownLatch waitForLogin = new CountDownLatch(1);
        AtomicBoolean loginBol = new AtomicBoolean(false);
        AtomicBoolean exitBol = new AtomicBoolean(false);
        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // ch.pipeline().addLast(getLogHandler());
                    ch.pipeline().addLast(getLikeProtocolCodecSharable());
                    ch.pipeline().addLast(getLikeProtocolFrameDecoder());
                    ch.pipeline().addLast(getIdleWriteStateHandler());  // 写空闲事件
                    ch.pipeline().addLast(new HeatBeatPongMessageHandler());

                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        LoginResponseMessage response = null;

                        // 接收响应消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);

                            if ((msg instanceof LoginResponseMessage)) {
                                response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // 如果登录成功
                                    loginBol.set(true);
                                    System.out.println(response.getReason());
                                }
                                // 唤醒 system in 线程
                                waitForLogin.countDown();
                            }
                            if (msg instanceof RegisterResponseMessage) {
                                System.out.println(((RegisterResponseMessage) msg).getReason());
                                if (!((RegisterResponseMessage) msg).isSuccess()) {
                                    ctx.close();
                                } // END IF 失败就关闭通道
                            }

                        }

                        // 在连接建立后触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                            new Thread(() -> {
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();

                                if (exitBol.get()) return;

                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();

                                if (exitBol.get()) return;

                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                System.out.println(message);
                                // 发送消息
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作...");
                                try {  // 阻塞  等待登录业务处理
                                    waitForLogin.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 如果登录失败
                                if (!loginBol.get()) {
                                    final String reason = response.getReason();
                                    if (reason.equals(UserService.LoginStats.noUserInformation)) {
                                        System.out.println(reason + ",请问您是否需要注册？[[y/n]");
                                        if (scanner.nextLine().equals("y")) {
                                            System.out.println("请输入用户名:");
                                            username = scanner.nextLine();

                                            System.out.println("请输入密码:");
                                            password = scanner.nextLine();
                                            ctx.writeAndFlush(new RegisterRequestMessage(username, password));
                                        } else {
                                            ctx.channel().close(); // 不需要注册
                                            return;
                                        }
                                    } // END IF 判断是否需要注册
                                }
                                while (true) {
                                    outMenu();  // 输出菜单
                                    String command = null;
                                    try {
                                        command = scanner.nextLine();
                                    } catch (Exception e) {
                                        break;
                                    }
                                    if (exitBol.get()) return;

                                    if (switchCommand(ctx, username, command)) return;
                                }
                            }, "system in").start();
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            exitBol.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            exitBol.set(true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", ChatServer.serverPort).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    private static boolean switchCommand(ChannelHandlerContext ctx, String username, String command) {
        String[] s = command.split(" ");
        switch (s[0]) {
            case "send":
                ctx.writeAndFlush(new ChatRequestMessage(username, s[1], contentOf(s).toString()));
                break;
            case "gsend":
                ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], contentOf(s).toString()));
                break;
            case "gcreate":
                Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                set.add(username); // 加入自己
                ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set, username));
                break;
            case "gmembers":
                ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                break;
            case "gjoin":
                ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                break;
            case "gquit":
                ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                break;
            case "quit":
                ctx.channel().close();
                return true;
        }
        return false;
    }

    /**
     * 获取用户发送的内容
     *
     * @param s string[]
     * @return {@link StringBuilder}
     * @Description: 例如 gsend xxx hello world -> hello world
     */
    private static StringBuilder contentOf(String[] s) {
        StringBuilder content = new StringBuilder();
        for (int i = 2; i < s.length; i++) {
            content.append(s[i]).append(" ");
        }
        return content;
    }

    private static void outMenu() {
        System.out.println("==================================");
        System.out.println("register [username] [password]");
        System.out.println("send [username] [content]");
        System.out.println("gsend [group name] [content]");
        System.out.println("gcreate [group name] [m1,m2,m3...]");
        System.out.println("gmembers [group name]");
        System.out.println("gjoin [group name]");
        System.out.println("gquit [group name]");
        System.out.println("quit");
        System.out.println("==================================");
    }

}



