package gossip.app;

import gossip.app.gui.UserInterface;
import gossip.app.listener.Listener;
import gossip.app.listener.Parser;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    private PcapNetworkInterface net;
    private Listener listener;
    private UserInterface gui;

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

    }

    public void registerGossipPackage(byte[] rawData) {
        System.out.println(Arrays.toString(rawData));
        Parser.parse(rawData);
    }
}
