package gossip.app.listener;

import gossip.app.Runner;
import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.EtherType;

import java.util.Arrays;

public class Listener implements Runnable {
    private final Runner runner;
    private final PcapHandle handle;
    private PacketListener listener;

    public Listener(Runner runner) {
        this.runner = runner;
        int snapshotLength = 65536;//bytes
        int readTimeout = 50; //ms

        try {
            handle = runner.getNet().openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
        } catch (PcapNativeException e) {
            throw new RuntimeException();
        }

        initListener();
    }

    public void initListener() {
        listener = pcapPacket -> {
            try {
                EthernetPacket eth = EthernetPacket.newPacket(pcapPacket.getRawData(), 0, pcapPacket.length());
                EtherType type = eth.getHeader().getType();
                if (type.equals(EtherType.getInstance((short)0x9001)))/* && !eth.getHeader().getSrcAddr().toString().equals(runner.getMyMac())*/ {
                    runner.registerGossipPackage(eth.getRawData());
                }
            } catch (IllegalRawDataException e) {
                e.printStackTrace();
            }
        };
    }


    @Override
    public void run() {
        try {
            int maxPackets = Integer.MAX_VALUE;
            handle.loop(maxPackets, listener);
        } catch (InterruptedException | PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }

        handle.close();
    }
}
