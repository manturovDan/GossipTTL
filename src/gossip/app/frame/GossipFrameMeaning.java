package gossip.app.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class GossipFrameMeaning extends GossipFrame implements Cloneable {
    protected int ttl;
    protected String message;
    protected int len;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GossipFrameMeaning(String srcAddr, int ttl, int len, String message) {
        super(srcAddr);
        this.ttl = ttl;
        this.message = message;
        this.len = len;
    }

    public byte[] getMessageArray() {
        byte[] msg = new byte[1 + 4 + 2 + message.length()];
        msg[0] = 1;
        System.arraycopy(ByteBuffer.allocate(4).putInt(ttl).array(), 0, msg, 1, 4);
        System.arraycopy(ByteBuffer.allocate(2).putShort((short)len).array(), 0, msg, 5, 2);
        System.arraycopy(message.getBytes(StandardCharsets.UTF_8), 0, msg, 7, message.length());

        return msg;
    }

    @Override
    public String toString() {
        return srcAddr + " sent '" + message + "' with TTL=" + ttl;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
