import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);


        
        // List<Socket> connectedPlayers = simpleConnection(port);
    }

    private static List<Socket> simpleConnection(int port) {
        List<Socket> connectedPlayers = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                // Handle the client connection in a separate virtual thread
                Thread.ofVirtual().start(() -> handleClient(socket));
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        return connectedPlayers;
    }

    private static void handleClient(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received message from client " + clientSocket + ": " + message);

                // Process the message and send a response
                String response = "Server received: " + message;
                writer.println(response);
            }

        } catch (IOException ex) {
            System.out.println("Error in client thread: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void request(Socket clientSocket, String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }
}
