package RPC.transport;

import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientPool {
    NioSocketChannel[] clients;
    // lock[i] is the lock of clients[i]
    Object[] lock;

    ClientPool(int size){
        clients = new NioSocketChannel[size];//init  连接都是空的
        lock = new Object[size]; //锁是可以初始化的
        for(int i = 0 ; i< size ; i++){
            lock[i] = new Object();
        }

    }
}
