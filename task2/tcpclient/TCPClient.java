package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    int BUFFERSIZE = 1024;
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
            socket.shutdownOutput();
        }

        byte[] buffer = new byte[this.BUFFERSIZE];

        InputStream i = socket.getInputStream();
        ByteArrayOutputStream o = new ByteArrayOutputStream();

        if(timeout != null){
            socket.setSoTimeout(timeout);
        }

        int a;
        int count = 0;

        try{
            if (this.limit < this.BUFFERSIZE){
                buffer = new byte[this.limit];
                this.BUFFERSIZE = this.limit;
            }

            while(((a = i.read(buffer)) != -1)){
                if(limit != null && ((count * this.BUFFERSIZE) >= limit)){
                    socket.close();
                    return o.toByteArray();
                }
                o.write(buffer, 0, a);
                count++;

                if ((count * this.BUFFERSIZE) + this.BUFFERSIZE > this.limit) buffer = new byte[this.limit - (count * this.BUFFERSIZE)];
            }
        } catch(SocketTimeoutException e) {
            socket.close();
            return bytesToServer;
        }

        socket.close();
        return o.toByteArray();
    }
}
