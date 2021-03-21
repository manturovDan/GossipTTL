package gossip.app;

import gossip.app.gui.UserInterface;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
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
        PcapNetworkInterface net = getNetworkDevice();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        UserInterface gui = new UserInterface();

        executor.execute(new Thread(gui));

    }
}
