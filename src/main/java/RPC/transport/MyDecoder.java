package RPC.transport;

import RPC.protocol.MyContent;
import RPC.protocol.MyHeader;
import RPC.protocol.MyPackage;
import RPC.util.SerialUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {

    //父类里一定有channelread{  前老的拼buf  decode（）；剩余留存 ;对out遍历 } -> bytebuf
    private final int HEADERLENGTH;
    // set the header length
    {
        MyHeader myHeader = MyHeader.createHeader(new byte[10000]);
        byte[] res = SerialUtil.serailizeObj(myHeader);
        HEADERLENGTH = res.length;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        // buf 中可能有多个Packages，并且末尾的packcage可能是残缺不全的
        System.out.println("Decoding the package.");
        while(buf.readableBytes() >= HEADERLENGTH) {
            byte[] bytes = new byte[HEADERLENGTH];
            buf.getBytes(buf.readerIndex(),bytes);  //从buf.readerIndex()开始读取，读满bytes，但是readindex不变
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();

            //DECODE在2个方向都使用
            //通信的协议，能读出一个完整的package（header + content）
            if(buf.readableBytes() - HEADERLENGTH >= header.getDataLen()){
                //处理指针
                buf.readBytes(HEADERLENGTH);  //移动指针到body开始的位置
                byte[] data = new byte[(int)header.getDataLen()];
                buf.readBytes(data); // readerIndex() 移动了
                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);

                //TODO: 这里可以定制
                if(header.getFlag() == MyHeader.FLAG_CLIENT_CALL){
                    // client call
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new MyPackage(header,content));
                }else if(header.getFlag() == MyHeader.FLAG_SERVER_RESP){
                    // server response
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new MyPackage(header,content));
                }
            }else{
                // 末尾的package 不完整，需要拼接，这个工作ByteToMessageDecoder去做了
                break;
            }
        }

    }
}

