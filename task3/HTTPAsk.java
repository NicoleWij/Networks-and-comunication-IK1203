import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import tcpclient.TCPClient;

public class HTTPAsk {
    public static void main( String[] args) {
        try {
            ServerSocket http = new ServerSocket(Integer.parseInt(args[0]));
            String hostname = null;
            int port = -1;
            byte[] userInput = {'h', 'e', 'l', 'l', 'o'};
            Integer limit = null;
            Integer timeout = null;
            Boolean shutdown = null;

            while(true){
                Socket socket = http.accept();
                OutputStream output = socket.getOutputStream();
                output.write(userInput);

                byte[] buffer = new byte[1024];
                socket.getInputStream().read(buffer);

                String url = new String(buffer, StandardCharsets.UTF_8);
                String[] urlArray = new String[url.length()];

                System.out.println("URL = " + url);

                url = url.replace("?", " ");
                url = url.replace("=", " ");
                url = url.replace("&", " ");
                urlArray = url.split(" ");

                System.out.println("URL ARRAY = " + urlArray[1]);

                if(!(url.contains("ask"))){
                    System.out.println("Error 404 not found");
                    break;
                }

                if(!((url.contains("hostname") || url.contains("port")))){
                    System.out.println("400 Bad request");
                    break;
                }

                for(int i = 2; i < urlArray.length; i++){
                    switch (urlArray[i]) {
                        case "hostname":
                            hostname = urlArray[++i];

                            break;
                    
                        case "port":
                            port = Integer.parseInt(urlArray[++i]);

                            break;
                        
                        case "limit":
                            limit = Integer.valueOf(urlArray[++i]);

                            break;
                    
                        case "timeout":
                            timeout = Integer.valueOf(urlArray[++i]);

                            break;
                    
                        case "shutdown":
                            shutdown = Boolean.parseBoolean(urlArray[++i]);

                            break;

                        default:
                            break;
                    }
                }

                // TCPClient tcp = new tcpclient.TCPClient(shutdown, timeout, limit);
                // tcp.askServer(hostname, port, userInput);

                // output.flush();
            }

        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }
}