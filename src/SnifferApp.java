import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.Arrays;

public class SnifferApp {
    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return device;
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = getNetworkDevice();

        System.out.println("Chosen device: " + device);

        if (device == null) {
            System.exit(1);
        }

        int snapshotLength = 65536; //bytes
        int readTimeout = 50; //ms
        PcapHandle handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        //String filter = "eth";
        //handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(PcapPacket pcapPacket) {
                //System.out.println(pcapPacket);
                try {
                    System.out.println("st---------------------//----------------------");

                    EthernetPacket eth = EthernetPacket.newPacket(pcapPacket.getRawData(), 0, pcapPacket.length());
                    System.out.println(eth);
                    System.out.println("dst: " + eth.getHeader().getDstAddr());
                    System.out.println("src: " + eth.getHeader().getSrcAddr());
                    EtherType type = eth.getHeader().getType();
                    System.out.println("type/len: " + type);
                    System.out.println(Arrays.toString(eth.getRawData()));

                    if (type.equals(EtherType.getInstance((short)0x9001))) {
                        while (true) {}
                    }

                    System.out.println("fn---------------------//----------------------");
                } catch (IllegalRawDataException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            int maxPackets = 500000;
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handle.close();
    }
}
