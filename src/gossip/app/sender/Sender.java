package gossip.app.sender;

import gossip.app.Runner;
import gossip.app.frame.GossipFrameMeaning;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

import java.util.Arrays;

public class Sender {
    private GossipFrameMeaning frame;
    private Runner runner;

    public Sender(GossipFrameMeaning frame, Runner runner) {
        try {
            this.frame = (GossipFrameMeaning) frame.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }

        this.runner = runner;
    }

    public void changePacketToResend() {
        frame.setTtl(frame.getTtl()-1);
        frame.setSrcAddr(runner.getMyMac());
    }

    public void send(String dstMac, PcapNetworkInterface device) throws PcapNativeException {
        PcapHandle sendHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();

        System.out.println("sending to: " + dstMac + " from: " + runner.getMyMac());

        System.out.println(Arrays.toString(frame.getMessageArray()));

        ethBuilder.dstAddr(MacAddress.getByName(dstMac))
                .srcAddr(MacAddress.getByName(runner.getMyMac()))
                .type(EtherType.getInstance((short)0x9001))
                .pad(frame.getMessageArray());

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
