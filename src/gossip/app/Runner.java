package gossip.app;

import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;
import gossip.app.gui.UserInterface;
import gossip.app.listener.Listener;
import gossip.app.listener.Parser;
import gossip.app.sender.Sender;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    private PcapNetworkInterface net;
    private Listener listener;
    private UserInterface gui;
    private int sendersCount = 2;

    public Set<String> getMacTable() {
        return macTable;
    }

    public String getMyMac() {
        return net.getLinkLayerAddresses().get(0).toString();
    }

    public String getMacTableInString() {
        StringBuilder macs = new StringBuilder();

        for (String mac : macTable) { 
            macs.append(mac).append("\n");
        }

        return macs.toString();
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
        UserInterface.setRunner(this);

        listener = new Listener(this);

        executor.execute(new Thread(gui));
        executor.execute(new Thread(listener));

        macTable = Collections.synchronizedSet(new HashSet<>());

        macTable.add("34:e1:2d:64:54:88");
    }

    public void registerGossipPackage(byte[] rawData) {
        System.out.println(Arrays.toString(rawData));
        GossipFrame frame = Parser.parse(rawData);
        System.out.println(frame);

        if (frame instanceof GossipFrameMeaning) {
            if (((GossipFrameMeaning) frame).getTtl() > 0) {
                GossipFrameMeaning resendFrame = (GossipFrameMeaning) frame;
                Sender sender = new Sender(resendFrame, this);
                sender.changePacketToResend();

                List<String> chosenMacs = new ArrayList<>(macTable);
                Collections.shuffle(chosenMacs);

                for (int i = 0; i < Math.min(chosenMacs.size(), sendersCount); ++i) {
                    try {
                        System.out.println("> " + chosenMacs.get(i));
                        sender.send(chosenMacs.get(i), net);
                        System.out.println("sent");
                    } catch (PcapNativeException e) {
                        System.out.println(e);
                    }
                }
            }
        } else {
            macTable.add(frame.getSrcAddr());
        }
    }
}
