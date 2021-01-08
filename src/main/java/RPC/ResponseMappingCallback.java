package RPC;

import RPC.protocol.MyPackage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseMappingCallback {
    // map of requestID (header id) and CompletableFuture
    static ConcurrentHashMap<Long, CompletableFuture> mapping = new ConcurrentHashMap<>();

    // 添加回调，等待回调
    public static void addCallBack(long requestID,CompletableFuture cb){
        mapping.putIfAbsent(requestID,cb);
    }

    // 运行回调
    public static void runCallBack(MyPackage pkg){
        CompletableFuture cf = mapping.get(pkg.getHeader().getRequestID());
        // 运行回调，传入Content中的返回值，即本次RPC调用应返回client的值
        cf.complete(pkg.getContent().getRes());
        removeCB(pkg.getHeader().getRequestID());
    }

    // a RPC process is over
    private static void removeCB(long requestID) {
        mapping.remove(requestID);
    }

}

