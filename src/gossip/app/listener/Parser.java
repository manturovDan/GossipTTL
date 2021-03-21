package gossip.app.listener;

import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Parser {
    public static GossipFrame parse(byte[] frame) {
        byte typeOld = frame[12];
        byte typeYng = frame[13];

        //System.out.println(String.format("0x%08X", typeOld) + " " + String.format("0x%08X", typeYng));
        if (typeOld != -112 || typeYng != 1) { //type 0x0901
            System.out.println(typeOld);
            throw new RuntimeException("frame type error"); //type error
        }

        String dstAddr = getMacAddr(0, frame);
        String srcAddr = getMacAddr(6, frame);

        System.out.println("from: " + srcAddr);
        System.out.println("to: " + dstAddr);

        byte gossipType = frame[14];

        if (gossipType == 0)  { //add me
            //System.out.println("register");

            return new GossipFrame(srcAddr);
        } else if (gossipType == 1) { //getting gossip
            //System.out.println("next info: ");
            int ttl = byteFlowToInt(frame, 15, 4);
            int len = byteFlowToInt(frame, 19, 2);
            String message = bytesToString(frame, 21, len);

            //System.out.println("ttl: " + ttl + "; " + "len: " + len + "; message: " + message);

            if (len > 1400) {
                throw new RuntimeException("error in message length");
            }

            return new GossipFrameMeaning(srcAddr, ttl, len, message);
        } else {
            //error
            throw new RuntimeException("gossip type error (" + gossipType + ")");
        }
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

    public static String bytesToString(byte[] raw, int start, int len) {
        int realLen = Math.min(len, raw.length - 21);
        byte[] copy = new byte[realLen];

        System.arraycopy(raw, start, copy, 0, realLen);

        return new String(copy, StandardCharsets.UTF_8);
    }
}
