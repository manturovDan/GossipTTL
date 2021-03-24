package gossip.app;

import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;
import gossip.app.gui.UserInterface;
import gossip.app.listener.Listener;
import gossip.app.listener.Parser;
import gossip.app.sender.RegisterSender;
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

    public String getMyIP() {
        return net.getAddresses().get(0).getAddress().getHostAddress();
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
        System.out.println("PROGRAM START (v1.1)!");
        net = getNetworkDevice();
        System.out.println("My Mac address :" + net.getLinkLayerAddresses() );
        System.out.println("My IP address: " + getMyIP());

        boolean initialized = false;
        while (!initialized) {
            initialized = initialize();
            if (!initialized)
                System.out.println("Try one more time");
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        gui = new UserInterface();
        UserInterface.setRunner(this);

        listener = new Listener(this);

        executor.execute(new Thread(gui));
        executor.execute(new Thread(listener));

        macTable = Collections.synchronizedSet(new HashSet<>());

        //macTable.add("34:e1:2d:64:54:88");
    }

    public void registerGossipPackage(byte[] rawData) {
        System.out.println("raw data: " + Arrays.toString(rawData));
        GossipFrame frame = Parser.parse(rawData);
        System.out.println("frame: " + frame);

        if (frame instanceof GossipFrameMeaning) {
            if (((GossipFrameMeaning) frame).getTtl() > 0) {
                GossipFrameMeaning resendFrame = (GossipFrameMeaning) frame;

                Sender sender = new Sender(resendFrame, this);
                sender.changePacketToResend();

                sendRequest(sender);
            }
        } else {
            macTable.add(frame.getSrcAddr());
        }
    }

    public void sendRequest(Sender sender) {

        List<String> chosenMacs = new ArrayList<>(macTable);
        Collections.shuffle(chosenMacs);

        for (int i = 0; i < Math.min(chosenMacs.size(), sendersCount); ++i) {
            try {
                System.out.println("> " + chosenMacs.get(i));
                sender.send(chosenMacs.get(i), net);
            } catch (PcapNativeException e) {
                System.out.println(e);
            }
        }
    }

    private boolean initialize() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert count of resend destinations (N): ");
        sendersCount = Integer.parseInt(sc.nextLine());

        System.out.println("Insert dst MACs (throw comma), or 0 to do nothing: " );
        String macs = sc.nextLine();

        if (macs.equals("0"))
            return true;

        String[] macAddr = macs.split(",\\s*");

        GossipFrame register = new GossipFrame(getMyMac());
        RegisterSender sender = new RegisterSender(register, this);

        for (String mac : macAddr) {
            try {
                System.out.println("reg: " + mac);
                sender.send(mac, net);
            } catch (PcapNativeException e) {
                return false;
            }
        }

        return true;
    }
}
