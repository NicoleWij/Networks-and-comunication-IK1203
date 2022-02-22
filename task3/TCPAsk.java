import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class TCPAsk {
	static boolean shutdown = false;             // True if client should shutdown connection
	static Integer timeout = null;			     // Max time to wait for data from server (null if no limit)
	static Integer limit = null;			     // Max no. of bytes to receive from server (null if no limit)
	static String hostname = null;			     // Domain name of server
	static int port = 0;					     // Server port number
	static byte[] userInputBytes = new byte[0];  // Data to send to server

    /*
     * Usage: explain how to use the program, then exit with failure status
     */
    private static void usage() {
        System.err.println("Usage: TCPAsk [options] host port <data to server>");
        System.err.println("Possible options are:");
        System.err.println("    --shutdown");
        System.err.println("    --timeout <milliseconds>");
        System.err.println("    --limit <bytes>");
        System.exit(1);
    }

	/*
	 * Parse arguments on command line
	 */
	private static void parseArgs(String[] args) {
		try {
			int argindex = 0;

			// Options first. Loop through command line arguments and look for options.
			while (argindex < args.length && args[argindex].startsWith("--")) {
				if (args[argindex].equals("--shutdown")) {
					// Consume next argument as timeout
					shutdown = true;
				}
				else if (args[argindex].equals("--timeout")) {
					// Consume next argument as timeout
					argindex += 1;
					timeout = Integer.parseInt(args[argindex]);
				}
				else if (args[argindex].equals("--limit")) {
					// Consume next argument as limit
					argindex += 1;
					limit = Integer.parseInt(args[argindex]);
				}
				else {
					// Don't recognize this option
					usage();
				}
				argindex++;
			}

			// Then mandatory command line arguments: hostname and port number
			hostname = args[argindex++];
			port = Integer.parseInt(args[argindex++]);

			// Remaining arguments, if any, are string to send to server
			if (argindex < args.length) {
				// Collect remaining arguments into a string with single space as separator
				StringBuilder builder = new StringBuilder();
				boolean first = true;
				while (argindex < args.length) {
					if (first)
						first = false;
					else
						builder.append(" ");
					builder.append(args[argindex++]);
				}
				builder.append("\n");
				userInputBytes = builder.toString().getBytes();
			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
				// Exceeded array while parsing command line, or could
				// not convert port number argument to integer -- tell user
				// how to use the program
				usage();
		}
	}


    /*
     * Main program. Parse arguments on command line and call TCPClient
     */
    public static void main( String[] args) {
        parseArgs(args);
		try {
			TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
			byte[] serverBytes  = tcpClient.askServer(hostname, port, userInputBytes);
			String serverOutput = new String(serverBytes);
			System.out.printf("%s:%d says:\n%s", hostname, port, serverOutput);
			// For non-empty strings, make a linebreak if there isn't one at the end of the string
			if (serverOutput.length() > 0 && !serverOutput.endsWith("\n"))
				System.out.println();
		} catch(IOException ex) {
			System.err.println(ex);
			System.exit(1);
		}
	}
}

