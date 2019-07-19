package pers.sun.nettyapi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import pers.sun.nettyapi.handler.EchoHandler;
import java.util.concurrent.TimeUnit;

/**
 * Created by sungaozhao on 2/16/2016.
 */
public class NormalNettyServer {
    private int serverPort = 9090;
    
    public NormalNettyServer(int port) {
        serverPort = port;
    }

    public void start() throws Exception {
        // 创建Accpet线程池 (1)
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        // 创建Work线程池
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            // 创建ServerBootstrap (2)
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup).      // 加入两个线程池 (3)
                    channel(NioServerSocketChannel.class). // 初始化 NioServerSocketChannel (4)
                    childHandler(new ChannelInitializer<SocketChannel>() {  // 初始化处理handler (5)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 加入用户心跳监测机制
                    ch.pipeline().addLast("timeout", new IdleStateHandler(60, 10, 10, TimeUnit.SECONDS));
                    // 加入业务处理handler
                    ch.pipeline().addLast("echo", new EchoHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 128) // (6)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (7) 连接超过指定的时间没有收到数据，
                                                                    // 就会触发TCP层发起心调检测

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(serverPort).sync();
            f.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

