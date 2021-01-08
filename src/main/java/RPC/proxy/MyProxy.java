package RPC.proxy;

import RPC.Dispatcher;
import RPC.protocol.MyContent;
import RPC.transport.ClientFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

public class MyProxy {

    public static <T>T proxyGet(Class<T>  interfaceInfo){
        //实现各个版本的动态代理。。。。
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        //TODO  LOCAL REMOTE  实现：  用到dispatcher  直接给你返回，还是本地调用的时候也代理一下
        Dispatcher dis =Dispatcher.getDis();

        T res = (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Object res = null;
                Object o = dis.get(interfaceInfo.getName());
                System.out.println("interfaceInfo.getName : " + interfaceInfo.getName());
                // Dispatcher 在服务器端是有数据的，那么这一步意味着服务器调用RPC请求自己。所以直接local就行。
                // 在Client端的 Dispatcher 一直没有数据
                if(o == null){
                    System.out.println("By RPC.");
                    // 走rpc
                    String name = interfaceInfo.getName();
                    String methodName = method.getName();
                    // 参数类型
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    // 初始化client 的 package 的 content
                    MyContent content = new MyContent();
                    content.setArgs(args);
                    content.setServiceName(name);
                    content.setMethodName(methodName);
                    content.setParameterTypes(parameterTypes);
                    // TODO 未来的需求会改变

                    /**
                     * 1,缺失了注册发现，zk
                     * 2,第一层负载面向的provider
                     * 3，consumer  线程池  面向 service；并发就有木桶，倾斜
                     * serviceA
                     *      ipA:port
                     *          socket1
                     *          socket2
                     *      ipB:port
                     */
                    CompletableFuture resF = ClientFactory.transport(content);
                    // 阻塞，等待Server回复
                    res = resF.get();
                    System.out.println("Client: get the response from Server: " + res.toString());
                }else{
                    // Local
                    // 插入一些插件的机会，做一些扩展
                    System.out.println("Local Object Calling....");
                    Class<?> clazz = o.getClass();
                    try {
                        Method m = clazz.getMethod(method.getName(), method.getParameterTypes());
                        res = m.invoke(o, args);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return  res;
            }
        });
        return res;
    }
}
