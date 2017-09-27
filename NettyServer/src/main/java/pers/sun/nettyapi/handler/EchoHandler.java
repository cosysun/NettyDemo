package pers.sun.nettyapi.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pers.sun.nettyapi.proto.HelloTest;
/**
 * Created by sungaozhao on 2/16/2016.
 */
public class EchoHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ByteBuf buf = (ByteBuf)o;
        byte[] packet = new byte[buf.readableBytes()];
        buf.readBytes(packet);
        HelloTest.Hello hello = HelloTest.Hello.parseFrom(packet);
        System.out.println(hello.getContent().toString());

        // 回包
        HelloTest.Hello.Builder builder = HelloTest.Hello.newBuilder();
        builder.setContent(hello.getContent().toString() + " ack");
        HelloTest.Hello helloAck = builder.build();
        byte[] packetAck = helloAck.toByteArray();
        ByteBuf bufAck = Unpooled.buffer();
        bufAck.writeBytes(packetAck);
        channelHandlerContext.channel().writeAndFlush(bufAck);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
