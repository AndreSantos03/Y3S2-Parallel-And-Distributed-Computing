package Wordle;

import java.net.*;
import java.io.*;
 
/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */

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

            // Continuously read user input and send it to the server
            while (true) {
                System.out.print("Enter message to send to server: ");
                String userInput = consoleReader.readLine();

                // Send the user input to the server
                writer.println(userInput);

                // Read and print the server's response
                String response = reader.readLine();
                System.out.println("Server response: " + response);
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}