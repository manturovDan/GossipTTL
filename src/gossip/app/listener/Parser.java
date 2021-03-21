package gossip.app.listener;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Parser {
    public static int parse(byte[] frame) {
        byte typeOld = frame[12];
        byte typeYng = frame[13];

        //System.out.println(String.format("0x%08X", typeOld) + " " + String.format("0x%08X", typeYng));
        if (typeOld != -112 || typeYng != 1) { //type 0x0901
            System.out.println(typeOld);
            System.out.println("type error");
            return 2; //type error
        }

        String dstAddr = getMacAddr(0, frame);
        String srcAddr = getMacAddr(6, frame);

        System.out.println("from: " + srcAddr);
        System.out.println("to: " + dstAddr);

        byte gossipType = frame[14];

        if (gossipType == 0)  { //add me
            System.out.println("register");
        } else if (gossipType == 1) { //getting gossip
            System.out.println("next info: ");
            System.out.println(byteFlowToInt(frame, 15, 2));
        } else {
            //error
            System.out.println("another type: " + gossipType);
        }

        return 0;
    }

    public static String getMacAddr(int startByte, byte[] rawData) {
        StringBuilder macBld = new StringBuilder();

        for (int i = startByte; i < startByte + 6; ++i) {
            macBld.append(String.format("%02X", rawData[i]));
            if (i != startByte + 5) {
                macBld.append(":");
            }
        }

        return macBld.toString();
    }

    public static int byteFlowToInt(byte[] arr, int start, int len) {
        byte[] copy = new byte[len];
        System.arraycopy(arr, start, copy, 0, len);

        ByteBuffer wrapped = ByteBuffer.wrap(copy);
        if (len == 2)
            return wrapped.getShort();
        else {
            return wrapped.getInt();
        }
    }
}
