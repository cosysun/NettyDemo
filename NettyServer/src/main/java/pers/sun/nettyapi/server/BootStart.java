package pers.sun.nettyapi.server;

import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2017/7/30.
 */
public class BootStart {
    public static void main(String[] args) throws Exception {
        new NormalNettyServer(8090).start();
        while (true) {
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
