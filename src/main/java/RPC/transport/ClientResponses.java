package RPC.transport;

import RPC.ResponseMappingCallback;
import RPC.protocol.MyPackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientResponses extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyPackage responsepkg = (MyPackage) msg;

        //曾经我们没考虑返回的事情
        ResponseMappingCallback.runCallBack(responsepkg);

    }
}

