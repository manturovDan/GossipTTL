package gossip.app.frame;

public class GossipFrame {
    protected String srcAddr;

    public String getSrcAddr() {
        return srcAddr;
    }

    public void setSrcAddr(String srcAddr) {
        this.srcAddr = srcAddr;
    }

    public GossipFrame(String srcAddr) {
        this.srcAddr = srcAddr;
    }

    @Override
    public String toString() {
        return srcAddr + " asks to add in MAC table";
    }
}
