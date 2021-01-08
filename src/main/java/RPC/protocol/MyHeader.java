package RPC.protocol;

import java.io.Serializable;
import java.util.UUID;


public class MyHeader implements Serializable {

    //通信上的协议
    // 协议状态位 初始0x14141414 即 client发给server的Header
    public static final int FLAG_DEFAULT = 0x14141414;
    public static final int FLAG_CLIENT_CALL = FLAG_DEFAULT;
    // server responce 0x14141424
    public static final int FLAG_SERVER_RESP = 0x14141424;
    int flag;
    // 请求状态
    long requestID;
    // MyContent 长度
    long dataLen;

    /**
     * Input the Content(type of MyContent), create corresponding Header(MyHeader). Flag is default value.
     * @param msg byte[] of content
     * @return
     */
    public static MyHeader createHeader(byte[] msg){
        MyHeader header = new MyHeader();
        header.setFlag(FLAG_DEFAULT);
        header.setDataLen(msg.length);
        header.setRequestID(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        return header;
    }

    /**
     * Input the Content(type of MyContent), create corresponding Header(MyHeader). Flag is given by user.
     * @param msg byte[] of content
     * @param flag
     * @return
     */
    public static MyHeader createHeader(byte[] msg, int flag){
        MyHeader header = new MyHeader();
        header.setFlag(flag);
        header.setDataLen(msg.length);
        header.setRequestID(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        return header;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getDataLen() {
        return dataLen;
    }

    public void setDataLen(long dataLen) {
        this.dataLen = dataLen;
    }

//    public static void main(String[] args) {
//        MyHeader myHeader = MyHeader.createHeader(new byte[10000]);
//        byte[] res = SerialUtil.serailizeObj(myHeader);
//        System.out.println(res.length);
//    }

    @Override
    public String toString() {
        return "MyHeader{" +
                "flag=" + flag +
                ", requestID=" + requestID +
                ", dataLen=" + dataLen +
                '}';
    }
}
