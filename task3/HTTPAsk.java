import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import tcpclient.TCPClient;

public class HTTPAsk {
    public static void main(String[] args) {
        String hostname = null;
        byte[] inp = new byte[0];
        int port = 0;
        Boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;

        try {
            ServerSocket http = new ServerSocket(Integer.parseInt(args[0]));

            while (true) {
                Socket socket = http.accept();
                byte[] buffer = new byte[1024];

                socket.getInputStream().read(buffer);
                OutputStream output = socket.getOutputStream();
                
                StringBuilder stringBuild = new StringBuilder();

                String url = new String(buffer, StandardCharsets.UTF_8);
                String[] urlArray = new String[url.length()];

                url = url.replace("?", " ");
                url = url.replace("=", " ");
                url = url.replace("&", " ");
                url = url.replace("\r\n", " ");
                urlArray = url.split(" ");

                if (!(url.contains("ask"))) {
                    stringBuild.append("HTTP/1.1 404 Not Found\r\n");
                    output.write(stringBuild.toString().getBytes());
                    socket.close();
                    continue;
                }

                if (!(url.contains("hostname")) || !(url.contains("port")) || !(url.contains("HTTP/1.1")) || !(url.contains("GET"))) {
                    stringBuild.append("HTTP/1.1 400 Bad Request\r\n");
                    output.write(stringBuild.toString().getBytes());
                    socket.close();
                    continue;
                }

                for (int i = 2; i < urlArray.length; i++) {
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

                        case "string":
                            inp = urlArray[++i].getBytes();

                            break;

                        default:
                            break;
                    }
                }

                stringBuild.append("HTTP/1.1 200 OK\r\n\r\n");
                TCPClient tcp = new TCPClient(shutdown, timeout, limit);
                stringBuild.append(new String(tcp.askServer(hostname, port, inp)));
                stringBuild.append("\r\n");

                System.out.println(stringBuild);

                output.write(stringBuild.toString().getBytes());
                socket.close();
            }

        } catch (Exception e) {
            System.out.println("Error is: " + e);
        }
    }
}