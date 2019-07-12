package pers.sun.nettyapi.handler;

import java.util.Arrays;

import pers.sun.nettyapi.consistenthash.ConsistentHashRouter;

public class ServerRoute {
    private ConsistentHashRouter<MyServiceNode> consistentHashRouter ;
    private static class ServerRouteInstance{
        private static final ServerRoute instance=new ServerRoute();
    }
        
    private ServerRoute(){
        MyServiceNode node1 = new MyServiceNode("IDC1","127.0.0.1",8080);
        MyServiceNode node2 = new MyServiceNode("IDC1","127.0.0.1",8081);
        MyServiceNode node3 = new MyServiceNode("IDC1","127.0.0.1",8082);
        MyServiceNode node4 = new MyServiceNode("IDC1","127.0.0.1",8084);
        //hash them to hash ring
        consistentHashRouter = new ConsistentHashRouter<>(Arrays.asList(node1,node2,node3,node4),10);//10 virtual node
    }
        
    public static ServerRoute getInstance(){
        return ServerRouteInstance.instance;
    }

    public MyServiceNode goRoute(int id) {
        MyServiceNode node = consistentHashRouter.routeNode(String.valueOf(id));
        System.out.println(String.valueOf(id) + " is route to " + node);
        return node;
    }
}