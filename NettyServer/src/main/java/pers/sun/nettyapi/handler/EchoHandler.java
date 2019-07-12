package pers.sun.nettyapi.handler;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;

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
        ByteBuf buf = (ByteBuf) o;
        byte[] packet = new byte[buf.readableBytes()];
        buf.readBytes(packet);

        // 解包
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        Testdemo.registerAllExtensions(registry);
        Base.PBDemo pbdemo = Base.PBDemo.parseFrom(packet, registry);
        int cmd = pbdemo.getDemoHead().getCmd();
        switch (cmd) {
            case 1: { // echoserver
                // hash转发
                int id = pbdemo.getDemoHead().getId();
                MyServiceNode node = ServerRoute.getInstance().goRoute(id);
                this.simulateEchoServer(channelHandlerContext, packet, node.toString());
            }
            break;  
        }

        /*
         * HelloTest.Hello hello = HelloTest.Hello.parseFrom(packet);
         * System.out.println(hello.getContent().toString());
         * 
         * // 回包 HelloTest.Hello.Builder builder = HelloTest.Hello.newBuilder();
         * builder.setContent(hello.getContent().toString() + " ack"); HelloTest.Hello
         * helloAck = builder.build(); byte[] packetAck = helloAck.toByteArray();
         * ByteBuf bufAck = Unpooled.buffer(); bufAck.writeBytes(packetAck);
         * channelHandlerContext.channel().writeAndFlush(bufAck);
         */

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 模拟业务服务处理， 正常情况一下应该另外起一个业务服务来处理，这里demo 所以用一个函数代替
    private void simulateEchoServer(ChannelHandlerContext channelHandlerContext, byte[] packet,  String route)
            throws InvalidProtocolBufferException {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        Testdemo.registerAllExtensions(registry);
        Base.PBDemo pbdemo = Base.PBDemo.parseFrom(packet, registry);
        
        int subcmd = pbdemo.getDemoHead().getSubcmd();
        int id = pbdemo.getDemoHead().getId();
        switch (subcmd) { // echo server handle
            case 1: { // 
                Testdemo.HelloRequest req = pbdemo.getTestdemoReq().getExtension(Testdemo.helloReq);
                System.out.println(req.getMessage().toString());
        
                // 回包
                Testdemo.HelloResponse.Builder rspBuilder = Testdemo.HelloResponse.newBuilder();
                rspBuilder.setMessage(req.getMessage().toString() + " ack, id:" + String.valueOf(id) + ", route to:" + route);
                Testdemo.HelloResponse rsp = rspBuilder.build();
        
                Base.TestDemoResponse.Builder testBuilder = Base.TestDemoResponse.newBuilder();
                testBuilder.setExtension(helloRsp, rsp);

                Base.DemoHead.Builder headBuilder = Base.DemoHead.newBuilder();
                headBuilder.setCmd(1);
                headBuilder.setSubcmd(1);
                headBuilder.setId(1);
        
                Base.PBDemo.Builder baseBuilder = Base.PBDemo.newBuilder();
                baseBuilder.setTestdemoRsp(testBuilder.build());
                baseBuilder.setDemoHead(headBuilder.build());
                Base.PBDemo pbDemo = baseBuilder.build();

                byte[] packetAck = pbDemo.toByteArray();
                ByteBuf bufAck = Unpooled.buffer();
                bufAck.writeBytes(packetAck);
                channelHandlerContext.channel().writeAndFlush(bufAck);
            }
            break;
        }
    }
}
