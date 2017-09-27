package com.test.nettyclient.client;

import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2017/7/30.
 */
public class BootStart {
    public static void main(String[] args) throws Exception {
        ConnectionClient client = new ConnectionClient("192.168.1.2", 8090);
        client.connect();
        while (true) {
            client.sendMsg("hello");
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
