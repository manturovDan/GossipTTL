import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.Arrays;

public class SendEthernetRequest {
    public static void main(String[] args) throws PcapNativeException {
        String from = args[0];
        String to = args[1];

        PcapNetworkInterface nif;
        try {
            nif = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (nif == null) {
            return;
        }

        System.out.println(nif.getName() + "(" + nif.getDescription() + ")");

        PcapHandle sendHandle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);

        EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
        byte[] message = new byte[] {1, 0, 0, 0, 10, 1, 15, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100};

        ethBuilder.dstAddr(MacAddress.getByName(to)) //1a:35:75:85:6f:78
                .srcAddr(MacAddress.getByName(from)) //52:54:00:78:e5:97
                .type(EtherType.getInstance((short)0x9001))
                .pad(message);


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
