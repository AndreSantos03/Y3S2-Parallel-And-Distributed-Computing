import java.io.*;
import java.net.*;

public class Player {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Player <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            // Create streams for communication with the server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Create a BufferedReader to read user input from the console
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            boolean canWrite = false; // Flag to control writing

            while (true) {
                // Wait for a message from the server
                String serverMessage = reader.readLine();
                System.out.println(serverMessage);
                
                // Parse the server message to check if writing is allowed
                String[] parts = serverMessage.split("\\|");

                System.out.println("Server says: " + parts[0]);

                if (parts.length == 2 && parts[1].equals("true")) {
                    canWrite = true;
                } else {
                    canWrite = false;
                }

                // Prompt the user for input only if writing is allowed
                if (canWrite) {
                    System.out.print("Enter message to send to server: ");
                    String userInput = consoleReader.readLine();

                    // Send the user input to the server
                    writer.println(userInput);
                }
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
