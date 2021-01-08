package RPC;

import java.util.concurrent.ConcurrentHashMap;

// server端分配？
public class Dispatcher {

    private static Dispatcher dis = null;
    // 单例饿汉 分配器
    private Dispatcher(){}
    static {
        dis = new Dispatcher();
    }

    public static Dispatcher getDis(){
        return dis;
    }

    // 分配器记录map <TypeName(Class Name), Object>
    public static ConcurrentHashMap<String,Object> invokeMap = new ConcurrentHashMap<>();
    public void register(String k,Object obj){
        invokeMap.put(k,obj);
    }
    public Object get(String k){
        return invokeMap.get(k);
    }

}