import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;



public class Server {

    private List<Socket> connectedPlayers = new ArrayList<>();
    private Game game;

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
            while (connectedPlayers.size() < 2) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                connectedPlayers.add(socket);
            }

            run_game();


        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void run_game(){
        int num_rounds = 1;//hardcoded for now        
        int max_attempts = 6;
        game = new Game(connectedPlayers);
        game.start(num_rounds);

        //loop through rounds
        for(int i = 0; i < num_rounds;i++){
            Socket roundLeader = game.get_word_chooser();
            chooseWord(roundLeader);
            List<Socket> guessers = new ArrayList<>(connectedPlayers);
            guessers.remove(roundLeader);

            //loop through attempts
            for(int j = 0; j < max_attempts; j++){
                for(Socket guesser : guessers){
                    String guess = guessWord(guesser,game.get_word().length());
                    String guess_result = game.give_guess(guess);

                    System.out.println(guess_result);
                }
            }
            
        }
    }

    private void chooseWord(Socket player){
        String message = "You're this round captain! Choose a word: ";
        String responseString = " ";
        while(true){
            sendMessage(player, message,true);
            responseString = receiveMessage(player);
            if(responseString == null){
                sendMessage(player,"You can not enter empty a empty word!",false);
            }
            else if (responseString.contains(" ")) {
                System.out.println("dadwdawd");
                // Check if the response contains a space
                sendMessage(player, "Your word cannot contain spaces!", false);
            } 
            else if (!responseString.matches("[a-zA-Z]+")) {
                // Check if the response contains only letters
                sendMessage(player, "Your word can only contain letters!", false);
            }
            else{        
                game.set_word(responseString);

                return;
            }
        }
    }

    private String guessWord(Socket player, int wordLength){
        String message = "Try to guess the " + wordLength + " letters word:";
        String responseString = " ";
        while(true){
            sendMessage(player, message,true);
            responseString = receiveMessage(player);
            if(responseString == null){
                sendMessage(player,"You can not enter empty a empty word!",false);
            }
            else if (responseString.contains(" ")) {
                // Check if the response contains a space
                sendMessage(player, "Your word cannot contain spaces!", false);
            } 
            else if (!responseString.matches("[a-zA-Z]+")) {
                // Check if the response contains only letters
                sendMessage(player, "Your word can only contain letters!", false);
            }
            else{        
                return responseString;
            }
        }
    }

    private String receiveMessage(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine = in.readLine();
            
            if (inputLine != null) {
                return inputLine;
            } else {
                // Return an empty string if no message is received
                return "";
            }
        } catch (IOException e) { 
            e.printStackTrace();
            return null; //for exceptions
        }
    }

    private void sendMessage(Socket clientSocket, String message, boolean awaitingInput) {
        String messageToSend = message + "|" + awaitingInput;
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(messageToSend);
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

}
