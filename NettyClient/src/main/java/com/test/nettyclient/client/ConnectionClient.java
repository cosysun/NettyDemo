package com.test.nettyclient.client;

import com.test.nettyclient.proto.HelloTest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;

public class ConnectionClient {

    private EventLoopGroup loop = new NioEventLoopGroup();
	private Channel channel;
    private String host = "127.0.0.1";
    private int port = 0;

	public ConnectionClient(String host, int port) {
        this.host = host;
        this.port = port;
	}

    public boolean connect() {
		Bootstrap b = new Bootstrap();
        final HeartBeatHandler hearthandler = new HeartBeatHandler(this);
        final ClientHandler handler = new ClientHandler(this);
		b.group(loop).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(60, 20, 0, TimeUnit.SECONDS));
                pipeline.addLast("hearthandler", hearthandler);
				pipeline.addLast("handler",  handler);
			}
		});
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.option(ChannelOption.TCP_NODELAY, true);
        ChannelFuture future = b.connect(host, port);
        future.addListener(new ConnectionListener(this));
		return true;
	}

    public void reconnect(final Channel ch) {
        final EventLoop eventLoop = ch.eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                connect();
                System.out.println("reconnect server:" + host + ", Port:" + port);
            }
        }, 10L, TimeUnit.SECONDS);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

	public void disconnect() throws Exception {
		if (channel != null) {
			ChannelFuture future = channel.close();
			future.awaitUninterruptibly();
		}
	}

	public void sendHeartBeat() {
		if (channel != null && channel.isOpen() && channel.isWritable()) {
          //  channel.writeAndFlush("heartbeat");
		}
	}

    public void sendMsg(String msg) {
        if (channel != null && channel.isOpen() && channel.isWritable()) {
            HelloTest.Hello.Builder builder = HelloTest.Hello.newBuilder();
            builder.setContent(msg);
            HelloTest.Hello hello = builder.build();
            byte[] packet = hello.toByteArray();

            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(packet);
            channel.writeAndFlush(buf);

        }
    }
}
