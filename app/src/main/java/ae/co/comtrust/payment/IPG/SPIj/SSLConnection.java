package ae.co.comtrust.payment.IPG.SPIj;

import android.util.Log;

import com.sun.security.sasl.Provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.Security;
import java.util.StringTokenizer;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLConnection
        extends Connection {
    protected SSLSocket m_cSocket;
    boolean connected = false;
    protected String address;

    public SSLConnection() {
    }

    public void connect(String address, int port, int timeout, String mKeystore, String mPassword, String tKeystore, String tPassword, String tlsVersion)
            throws Exception {
        try {
            Security.insertProviderAt(
                    new Provider(), 1);

            SSLContext ctx = null;

            if ((tlsVersion != null) && (!tlsVersion.isEmpty())) {
                ctx = SSLContext.getInstance(tlsVersion, "SunJSSE");
            } else {
                ctx = SSLContext.getInstance("TLS", "SunJSSE");
            }


            KeyStore keyStore = KeyStore.getInstance("JKS");

            try {
                keyStore.load(new FileInputStream(mKeystore),
                        mPassword.toCharArray());
            } catch (IOException e) {
                keyStore = KeyStore.getInstance("pkcs12");
                keyStore.load(new FileInputStream(mKeystore),
                        mPassword.toCharArray());
            }


            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            keyManagerFactory.init(keyStore, mPassword.toCharArray());


            KeyStore trustedKeyStore = KeyStore.getInstance("JKS");

            if (tPassword == null) {
                try {
                    trustedKeyStore.load(new FileInputStream(tKeystore), null);
                } catch (IOException e) {
                    Log.e("Error", "Invalid keystore format");

                }
                trustedKeyStore = KeyStore.getInstance("pkcs12");
                trustedKeyStore.load(new FileInputStream(tKeystore),
                        null);
            } else {
                try {
                    trustedKeyStore.load(new FileInputStream(tKeystore),
                            tPassword.toCharArray());
                } catch (IOException e) {
                    if (e.toString().endsWith("Invalid keystore format")) {
                        trustedKeyStore = KeyStore.getInstance("pkcs12");
                        trustedKeyStore.load(new FileInputStream(tKeystore),
                                tPassword.toCharArray());
                    }
                }
            }
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            trustManagerFactory.init(trustedKeyStore);


            ctx.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);

            m_cSocket =
                    ((SSLSocket) sslSocketFactory.createSocket(address, port));
            m_cSocket.setSoTimeout(timeout);

            this.address = address;


            m_cSocket
                    .addHandshakeCompletedListener(new HandshakeCompletedListener() {


                        public void handshakeCompleted(HandshakeCompletedEvent hce) {
                        }


                    });
            connected = true;

            m_cSocket.startHandshake();
        } catch (Exception e) {
            throw e;
        }
    }

    public String send(String msg)
            throws Exception {
        String output_from_server = new String();

        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            m_cSocket.getOutputStream(), "UTF-8")));

            out.print("POST /Payment.asp HTTP/1.1\r\n");
            out.print("Host: " + address + "\r\n");
            out.print("Accept: text/xml-standard-api\r\n");
            out.print("Content-Type: text/xml-standard-api\r\n");
            out.print("Content-Length: " + msg.getBytes("UTF-8").length +
                    "\r\n");
            out.print("User-Agent: SPIj/1.0\r\n");
            out.print("\r\n");

            out.write(msg);
            out.flush();
            BufferedReader in = null;

            try {
                InputStream is = m_cSocket.getInputStream();
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (IOException ioExp) {
                IOException newExp = new IOException("<ErrorCode:9112>" +
                        ioExp.toString() + "\nDetailed Message : " +
                        ioExp.getMessage());

                throw newExp;
            }


            String headerLine = null;
            String header = "";
            int length = 0;
            int responseCode = 0;


            try {
                if (m_cSocket.isClosed()) {
                    throw new IOException("Connection has been closed");
                }

                headerLine = in.readLine();
            } catch (IOException ioExp) {
                System.out.println(ioExp.toString());
                IOException newExp = new IOException("<ErrorCode:9113>" +
                        ioExp.toString() + "\nDetailed Message : " +
                        ioExp.getMessage());

                throw newExp;
            }


            if (headerLine == null) {
                throw new Exception("There is no server output");
            }
            String responseString = null;
            String responseMessage = null;
            while ((headerLine != null) && (headerLine.length() != 0)) {
                header = header + headerLine + "\n";
                if (headerLine.startsWith("HTTP/1.1")) {
                    StringTokenizer subElements = new StringTokenizer(
                            headerLine);
                    subElements.nextToken();
                    try {
                        responseCode =
                                Integer.parseInt(subElements.nextToken());
                        responseMessage = subElements.nextToken() + " ";
                        while (subElements.hasMoreTokens()) {
                            responseMessage = responseMessage + subElements.nextToken() + " ";
                        }
                    } catch (NumberFormatException numform) {
                        throw new Exception(
                                "Reading returned HTTP header error : " +
                                        numform.toString());
                    }
                }


                if (headerLine.startsWith("Content-Length:")) {
                    String lengthString = headerLine.substring(
                            headerLine.lastIndexOf(":") + 1,
                            headerLine.length()).trim();
                    try {
                        length = Integer.parseInt(lengthString);
                    } catch (NumberFormatException numform) {
                        throw new Exception("I cant read header : " +
                                numform.toString());
                    }
                }
                try {
                    headerLine = in.readLine();
                } catch (IOException ioExp) {
                    IOException newExp = new IOException("<ErrorCode:9114>" +
                            ioExp.toString() + "\nDetailed Message : " +
                            ioExp.getMessage());

                    throw newExp;
                }
            }


            if ((responseCode != 100) && (responseCode != 200)) {
                throw new Exception("HTTP Server error. HTTP Response Code: " +
                        responseCode + "Response Message: " + responseMessage);
            }

            StringBuffer buffer = new StringBuffer();
            try {
                int ch;
                while ((ch = in.read()) > -1) {
                    buffer.append((char) ch);
                }
            } catch (IOException ioExp) {
                IOException newExp = new IOException("<ErrorCode:9115>" +
                        ioExp.toString());

                throw newExp;
            }
            int ch;
            output_from_server = buffer.toString();

            in.close();
            out.close();
            return output_from_server;
        } catch (Exception e) {
            throw e;
        }
    }

    public void disconnect() throws Exception {
        if (connected) {
            try {
                m_cSocket.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
