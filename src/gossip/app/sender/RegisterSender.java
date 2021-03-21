package gossip.app.sender;

import gossip.app.Runner;
import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

public class RegisterSender {
    private GossipFrame frame;
    private Runner runner;

    public RegisterSender(GossipFrame frame, Runner runner) {
        this.frame  = frame;
        this.runner = runner;
    }

    public void send(String dstMac, PcapNetworkInterface device) throws PcapNativeException {
        PcapHandle sendHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();

        System.out.println("register to: " + dstMac + " from: " + runner.getMyMac());

        ethBuilder.dstAddr(MacAddress.getByName(dstMac))
                .srcAddr(MacAddress.getByName(runner.getMyMac()))
                .type(EtherType.getInstance((short)0x9001))
                .pad(new byte[] {0});

        Packet p = ethBuilder.build();
        System.out.println(p);

        try {
            sendHandle.sendPacket(p);
        } catch (NotOpenException e) {
            e.printStackTrace();
        }

        sendHandle.close();
    }
}
