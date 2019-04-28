package ae.co.comtrust.payment.IPG.SPIj;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnection extends Connection {
    protected Socket socket;
    protected InputStream is;

    public SocketConnection() {
    }

    public void connect(String address, int port) throws Exception {
        try {
            socket = new Socket(address, port);
        } catch (Exception e) {
            throw new Exception("Failed to connect: " + e.toString());
        }
    }

    public String send(String msg)
            throws Exception {
        String output = null;
        PrintWriter out =
                new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())));
        out.write(msg);
        out.flush();
        is = socket.getInputStream();
        while ((output = read()) != null) {
        }
        return output;
    }

    public void disconnect()
            throws Exception {
        try {
            socket.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public String read()
            throws IOException {
        StringBuffer buffer = new StringBuffer();
        int ch;
        while ((ch = is.read()) > -1) {
            buffer.append((char) ch);
        }
        return buffer.toString();
    }
}
