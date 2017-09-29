package pers.sun.nettyapi.handler;

import com.google.protobuf.ExtensionRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import proto.Base;
import proto.HelloTest;
import proto.testdemo.Testdemo;

import static proto.testdemo.Testdemo.helloRsp;

/**
 * Created by sungaozhao on 2/16/2016.
 */
public class EchoHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ByteBuf buf = (ByteBuf)o;
        byte[] packet = new byte[buf.readableBytes()];
        buf.readBytes(packet);

        // 解包
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        Testdemo.registerAllExtensions(registry);
        Base.PBDemo pbdemo = Base.PBDemo.parseFrom(packet, registry);
        Testdemo.HelloRequest req = pbdemo.getTestdemoReq().getExtension(Testdemo.helloReq);
        System.out.println(req.getMessage().toString());

        // 回包
        Testdemo.HelloResponse.Builder rspBuilder = Testdemo.HelloResponse.newBuilder();
        rspBuilder.setMessage(req.getMessage().toString() + " ack");
        Testdemo.HelloResponse rsp = rspBuilder.build();

        Base.TestDemoResponse.Builder testBuilder = Base.TestDemoResponse.newBuilder();
        testBuilder.setExtension(helloRsp, rsp);

        Base.PBDemo.Builder baseBuilder = Base.PBDemo.newBuilder();
        Base.PBDemo pbDemo = baseBuilder.setTestdemoRsp(testBuilder.build()).build();
        byte[] packetAck = pbDemo.toByteArray();
        ByteBuf bufAck = Unpooled.buffer();
        bufAck.writeBytes(packetAck);
        channelHandlerContext.channel().writeAndFlush(bufAck);

        /*HelloTest.Hello hello = HelloTest.Hello.parseFrom(packet);
        System.out.println(hello.getContent().toString());

        // 回包
        HelloTest.Hello.Builder builder = HelloTest.Hello.newBuilder();
        builder.setContent(hello.getContent().toString() + " ack");
        HelloTest.Hello helloAck = builder.build();
        byte[] packetAck = helloAck.toByteArray();
        ByteBuf bufAck = Unpooled.buffer();
        bufAck.writeBytes(packetAck);
        channelHandlerContext.channel().writeAndFlush(bufAck);*/

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
