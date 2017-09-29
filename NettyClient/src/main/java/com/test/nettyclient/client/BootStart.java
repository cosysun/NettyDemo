package com.test.nettyclient.client;

import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2017/7/30.
 */
public class BootStart {
    public static void main(String[] args) throws Exception {
        ConnectionClient client = new ConnectionClient("127.0.0.1", 8090);
        client.connect();
        while (true) {
            //client.sendMsg("hello");
            client.sendMsg2("test");
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
