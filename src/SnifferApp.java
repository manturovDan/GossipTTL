import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

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

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(PcapPacket pcapPacket) {
                System.out.println(pcapPacket);
            }
        };

        try {
            int maxPackets = 50;
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handle.close();
    }
}
