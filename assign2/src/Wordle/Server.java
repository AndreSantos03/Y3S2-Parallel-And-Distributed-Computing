import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import Wordle.Game;



public class Server {

    private List<Socket> connectedPlayers = new ArrayList<>();


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        Server server = new Server();
        int port = Integer.parseInt(args[0]);

        server.simpleConnection(port);        
        
        // List<Socket> connectedPlayers = simpleConnection(port);
    }

    private void simpleConnection(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            //hardcoded 3 players
            while (connectedPlayers.size() < 3) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                connectedPlayers.add(socket);
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        run_game();
    }

    private void run_game(){
        Game game = new Game(connectedPlayers);
        game.start(3);//hardcoded for now

        for(int i = 0; i < 3;i++){
            Socket roundLeader = game.get_word_chooser();
            String chosenWord = chooseWord(roundLeader);
            game.set_word(chosenWord);
        }
    }

    private String chooseWord(Socket player){
        String message = "You're this round captain! Choose a word: ";
        String responseString = "";

        Runnable runnable = new Runnable() {
            public void run(){
                do{
                    request(player, message);
                    responseString = response(player);
                }
                while (!responseString.matches("[a-zA-Z]+"));            
            }
        };
        Thread.ofVirtual().start(runnable);
        return responseString;
    }

    private String response(Socket socket){
        StringBuilder responseBuilder = new StringBuilder();
        try (
            PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Append the received string to the StringBuilder
                responseBuilder.append(inputLine).append("\n");
                out.println(inputLine);
            }
        } catch (IOException e) { 
            e.printStackTrace();
        }
        // Return the built string
        return responseBuilder.toString();
    }

    private void request(Socket clientSocket, String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

}
