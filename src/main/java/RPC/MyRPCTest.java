package RPC;

import RPC.protocol.Config;
import RPC.proxy.MyProxy;
import RPC.service.Car;
import RPC.service.Movable;
import RPC.transport.MyDecoder;
import RPC.transport.ServerRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/*
    1，先假设一个需求，写一个RPC
    2，来回通信，连接数量，拆包？
    3，动态代理呀，序列化，协议封装
    4，连接池
    5，就像调用本地方法一样去调用远程的方法，面向java中就是所谓的 面向interface开发
 */

public class MyRPCTest {

    private void initServer(){
        System.out.println("Server starting...");
        // 初始化资源
        Car car = new Car("Init Server Car");
//        Bus bus = new Bus("Init Server Bus");

        Dispatcher dis = Dispatcher.getDis();
        dis.register(Movable.class.getName(), car);
//        dis.register(Bus.class.getName(), bus);

    }

    @Test
    public void startRPCServer() {
        Dispatcher dis = Dispatcher.getDis();
        initServer();

        NioEventLoopGroup RPCBoss = new NioEventLoopGroup(1);
        NioEventLoopGroup RPCWorkers = new NioEventLoopGroup(8);;

        ServerBootstrap RPCsbs = new ServerBootstrap();
        ChannelFuture bind = RPCsbs.group(RPCBoss, RPCWorkers)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        System.out.println("Server accept client port: " + ch.remoteAddress().getPort());
                        ChannelPipeline p = ch.pipeline();
                        // 业务逻辑
                        p.addLast(new MyDecoder());
                        p.addLast(new ServerRequestHandler(dis));
                        // 粘包拆包的问题，header+body
                    }
                }).bind(new InetSocketAddress(Config.DEFAULT_SERVER_ADDRESS, Config.DEFAULT_SERVER_PORT));

        System.out.println("Server started.");
        try {
            // 阻塞，等待客户端connect
            ChannelFuture sync = bind.sync();
            // 阻塞，等待客户端断开连接
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //模拟comsumer端 && provider
    @Test
    public void clientsGet() {

        AtomicInteger num = new AtomicInteger(0);
        int size = 5;
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(() -> {
                Movable car = (Movable) MyProxy.proxyGet(Movable.class);//动态代理实现   //是真的要去触发 RPC调用吗？
                int threadID = num.incrementAndGet();
                System.out.println("Client" + threadID + " say hello.");
                Boolean res = car.move(2,5);
                System.out.println("Client" + threadID + " move: " + res);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            // 手动阻塞
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRPC() {
        Movable car = (Movable) MyProxy.proxyGet(Movable.class);
        System.out.println("Result: " + car.move(2,2));
    }


}
