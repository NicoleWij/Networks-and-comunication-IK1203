package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    
    public TCPClient() {
        
    }

    public byte[] askServer(String hostname, int port, byte [] bytesToServer) throws IOException {
        Socket socket = new Socket(hostname, port);
        socket.getOutputStream().write(bytesToServer);

        byte[] buffer = new byte[BUFFERSIZE];

        InputStream i = socket.getInputStream();
        ByteArrayOutputStream o = new ByteArrayOutputStream();

        int a;

        while((a = i.read(buffer)) != -1){
            o.write(buffer, 0, a);
        }

        socket.close();
        return o.toByteArray();
    }
}
