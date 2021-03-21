package gossip.app.frame;

public class GossipFrameMeaning extends GossipFrame {
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

    @Override
    public String toString() {
        return srcAddr + " sent '" + message + "' with TTL=" + ttl;
    }
}
