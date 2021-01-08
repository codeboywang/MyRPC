package RPC.protocol;

public class MyPackage {
    MyHeader header;
    MyContent content;

    public MyHeader getHeader() {
        return header;
    }

    public void setHeader(MyHeader header) {
        this.header = header;
    }

    public MyContent getContent() {
        return content;
    }

    public void setContent(MyContent content) {
        this.content = content;
    }

    public MyPackage(MyHeader header, MyContent content) {
        this.header = header;
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyPackage{" +
                "header=" + header +
                ", content=" + content +
                '}';
    }
}
