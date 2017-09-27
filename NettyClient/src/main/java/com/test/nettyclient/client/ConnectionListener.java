package com.test.nettyclient.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by sun on 2017/7/14.
 */
public class ConnectionListener implements ChannelFutureListener {

    private ConnectionClient client;

    public ConnectionListener(ConnectionClient client) {
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            System.out.println("connect reconnect");
            this.client.reconnect(future.channel());
        } else {
            // 连接成功
            System.out.println("connect success");
            this.client.setChannel(future.channel());
        }
    }
}
