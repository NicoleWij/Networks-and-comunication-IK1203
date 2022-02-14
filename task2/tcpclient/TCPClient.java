package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 10;
    boolean shutdown = false;
    Integer timeout;
    Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit){
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] bytesToServer) throws IOException {
        Socket socket = new Socket(hostname, port);
        socket.getOutputStream().write(bytesToServer);

        if(shutdown){
            socket.close();
            return bytesToServer;
        }

        byte[] buffer = new byte[BUFFERSIZE];

        InputStream i = socket.getInputStream();
        ByteArrayOutputStream o = new ByteArrayOutputStream();

        if(timeout != null){
            socket.setSoTimeout(timeout);
        }

        int a;
        int count = 0;

        try{
            while(((a = i.read(buffer)) != -1)){
                if(limit != null && ((count * BUFFERSIZE) >= limit)){
                    return o.toByteArray();
                }
                o.write(buffer, 0, a);
                count++;
            }
        } catch(SocketTimeoutException e) {
            socket.close();
            return bytesToServer;
        }

        socket.close();
        return o.toByteArray();
    }
}
