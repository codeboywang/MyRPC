package RPC.transport;

import RPC.Dispatcher;
import RPC.protocol.MyContent;
import RPC.protocol.MyHeader;
import RPC.protocol.MyPackage;
import RPC.util.SerialUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

    Dispatcher dis;
    public static final String IOMODE_SYN = "SYN";
    public static final String IOMODE_ASYN = "ASYN";
    private final String EXCP_INFO = "Exceptions on Server.";

    private String IOTHREAD_MODE = IOMODE_SYN;

    public String getIOTHREAD_MODE() {
        return IOTHREAD_MODE;
    }

    public void setIOTHREAD_MODE(String IOTHREAD_MODE) {
        if(IOTHREAD_MODE.equals(IOMODE_SYN)){
            this.IOTHREAD_MODE = IOMODE_SYN;
        }else if(IOTHREAD_MODE.equals(IOMODE_ASYN)){
            this.IOTHREAD_MODE = IOMODE_ASYN;
        }else{
            System.out.println("No such mode. Set it as IOMODE_SYN");
            this.IOTHREAD_MODE = IOMODE_SYN;
        }
    }

    public ServerRequestHandler(Dispatcher dis) {
        this.dis = dis;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server has decoded the package.");
        MyPackage requestPkg = (MyPackage) msg;
        System.out.println("Server: the Package is...\n" + msg.toString());
        Process process = new Process(requestPkg, ctx);
        if(IOTHREAD_MODE.equals(IOMODE_SYN)){
            // 用同一个executor
            ctx.executor().execute(process);
        }else if(IOTHREAD_MODE.equals(IOMODE_ASYN)){
            // 使用另一个excutor
            ctx.executor().parent().next().execute(process);
        }
    }
    private class Process implements Runnable{
        MyPackage requestPkg;
        ChannelHandlerContext ctx;
        Process(MyPackage requestPkg, ChannelHandlerContext ctx){
            this.requestPkg = requestPkg;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            System.out.println("Server preparing the result...");
            // 请求的服务名，即 Type Name
            String serviceName = requestPkg.getContent().getServiceName();
            System.out.println("Server: the client is calling type: " + serviceName);
            // 请求调用的方法
            String method = requestPkg.getContent().getMethodName();
            System.out.println("Server: the client is calling function: " + method);
            Object obj = dis.get(serviceName);
            // 返回对象的类型
            Class<?> clazz = obj.getClass();
            Object res = null;
            Boolean isSucceed = false;
            try {
                Method m = clazz.getMethod(method, requestPkg.getContent().getParameterTypes());
                res = m.invoke(obj, requestPkg.getContent().getArgs());
                System.out.println("Server: the reflection is succeed.");
                isSucceed = true;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

//            String execThreadName = Thread.currentThread().getName()
//            String s = "io thread: " + ioThreadName + " exec thread: " + execThreadName + " from args:" + requestPkg.content.getArgs()[0];
            if(!isSucceed){
                // 调取失败-设置特殊回复值
                res = EXCP_INFO;
            }


            // 初始化content
            MyContent content = new MyContent();
            content.setRes(res);
            byte[] contentByte = SerialUtil.serailizeObj(content);
            // 初始化Header
            MyHeader resHeader = new MyHeader();
            resHeader.setRequestID(requestPkg.getHeader().getRequestID());
            resHeader.setFlag(MyHeader.FLAG_SERVER_RESP);
            resHeader.setDataLen(contentByte.length);
            byte[] headerByte = SerialUtil.serailizeObj(resHeader);
            // server端分配内存
            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(headerByte.length + contentByte.length);
            byteBuf.writeBytes(headerByte);
            byteBuf.writeBytes(contentByte);
            // 服务器刷出数据
            ctx.writeAndFlush(byteBuf);
            System.out.println("Server send the response package.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client close by Exception!");
    }
}

