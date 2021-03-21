package gossip.app;

import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;
import gossip.app.gui.UserInterface;
import gossip.app.listener.Listener;
import gossip.app.listener.Parser;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    private PcapNetworkInterface net;
    private Listener listener;
    private UserInterface gui;

    public Set<String> getMacTable() {
        return macTable;
    }

    private Set<String> macTable;

    public PcapNetworkInterface getNet() {
        return net;
    }

    private PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return device;
    }

    public void run() {
        System.out.println("PROGRAM START!");
        net = getNetworkDevice();
        System.out.println("My Mac address :" + net.getLinkLayerAddresses() );

        ExecutorService executor = Executors.newFixedThreadPool(2);

        gui = new UserInterface();
        listener = new Listener(this);

        executor.execute(new Thread(gui));
        executor.execute(new Thread(listener));

        macTable = Collections.synchronizedSet(new HashSet<>());


    }

    public void registerGossipPackage(byte[] rawData) {
        System.out.println(Arrays.toString(rawData));
        GossipFrame frame = Parser.parse(rawData);
        System.out.println(frame);

        if (frame instanceof GossipFrameMeaning) {

        } else {
            macTable.add(frame.getSrcAddr());
        }
    }
}
