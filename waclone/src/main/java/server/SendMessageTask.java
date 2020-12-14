package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import com.google.gson.Gson;

public class SendMessageTask implements Runnable {

    private SocketChannel channel;
    private Request request;
    private ByteBuffer buffer;

    public SendMessageTask(SocketChannel channel, Request request) {
        this.channel = channel;
        this.request = request;
    }

    private int convertUTF(String str) {
        final int strlen = str.length();
        int utflen = strlen; // optimized for ASCII

        for (int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0)
                utflen += (c >= 0x800) ? 2 : 1;
        }

        final byte[] bytearr = new byte[utflen + 2];

        int count = 0;
        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) { // optimized for initial run of ASCII
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0)
                break;
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            int c = str.charAt(i);
            if (c < 0x80 && c != 0) {
                bytearr[count++] = (byte) c;
            } else if (c >= 0x800) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }

        buffer = ByteBuffer.wrap(bytearr);
        return count;

    }

    private void storeAndCloseConnection() {
        System.out.println("FFFFFFFFFFFFFFFFFFFFFF Receiver Offline, saving to DB");
        GlobalVariables.databaseInsertData(request);
        try {
            if (GlobalVariables.removeClientFromOnlineList(channel)) {
                System.out.println("Closing Channel");
            }
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void run() {
        Gson gson = new Gson();
        int len = convertUTF(gson.toJson(request));

        System.out.println("Sending" + buffer.array());
        // synchronized (channel) {
        System.out.println(len);
        while (len > 0) {
            int clen;
            try {
                clen = channel.write(buffer);
                len -= clen;
            } catch (ClosedChannelException e) {
                System.out.println("channel closed");
                e.printStackTrace();
                storeAndCloseConnection();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                storeAndCloseConnection();
                return;
            }
        }
        return;
    }
}
