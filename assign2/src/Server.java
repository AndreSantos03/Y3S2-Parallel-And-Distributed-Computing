import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;




public class Server {

    private List<Map.Entry<String, SocketChannel>> connectedPlayers = new ArrayList<>();
    int gameId = 1;
    private Auth auth;
    private List<Game> runningGames = new ArrayList<>();
    private List<Map.Entry<String, SocketChannel>> allPlayers = new ArrayList<>();
    private List<SocketChannel> waitingPlayers = new ArrayList<>();

    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.out.println("Usage: java Server <port> <game_player_count>");
            return;
        }

        Server server = new Server();
        int port = Integer.parseInt(args[0]);
        int playersPerGame = Integer.parseInt(args[1]);
        server.simpleConnection(port,playersPerGame);        
        
        // List<Socket> connectedPlayers = simpleConnection(port);
    }

    public boolean login(String username, String password) {
        return auth.authenticate(username, password);
    } 

    private void simpleConnection(int port, int playersPerGame) {
        int gameId = 1; // Initialize the game ID counter
    
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Server is listening on port " + port);
    
            // Multithreading with virtual threads
            var executorService = Executors.newVirtualThreadPerTaskExecutor();
    
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                waitingPlayers.add(socketChannel);

                boolean isPlayerRejoin = true;
                SocketChannel waitingPlayer = waitingPlayers.getFirst();
                try
                {
                   
                    String username = receive(socketChannel)[1];
                
                    String[] response = receive(socketChannel);
                    String password = response[0];
                    String token = response[1];
               

                    if(token == "REGISTRATION"){
                        auth.register(username, password);
                    }

                    if (login(username, password))
                    {
                        System.out.println("logged in");
                        send(waitingPlayer, "OKUSERNAME", null);
                        waitingPlayers.remove(waitingPlayer);
                        
                        isPlayerRejoin = false;
                        for (Map.Entry<String, SocketChannel> player : allPlayers)
                        {
                            if (player.getKey().toString() == username)
                            {
                                player.setValue(socketChannel);
                                isPlayerRejoin = true;
                                break;
                            }
                        }
                        if (!isPlayerRejoin)
                        {
                            allPlayers.add(Map.entry(username, socketChannel));
                        }
                    }
                }
                catch (Exception e) {}

                Map.Entry<String, SocketChannel> playerEntry = null;
                for (Map.Entry<String, SocketChannel> entry : allPlayers) {
                    if (entry.getValue() == socketChannel) {
                        playerEntry = entry;
                        break;
                    }
                }
                
                if (!isPlayerRejoin)
                {
                    System.out.println("New client connected: " + socketChannel);
                    connectedPlayers.add(playerEntry);
                }
                else
                {
                    for (Game game : runningGames) {
                        game.updatePlayer(playerEntry);
                    }
                }
                
                if (connectedPlayers.size() >= playersPerGame) {
                    // Start a new game
                    List<Map.Entry<String, SocketChannel>> gamePlayers = new ArrayList<>();
                    int currentGameId = gameId++; // Assign the current game ID

                    //prints game starting and players
                    System.out.println("Starting Game #" + currentGameId + " with " + connectedPlayers.size() + " players:");
                    for (Map.Entry<String, SocketChannel> player : connectedPlayers) {
                        gamePlayers.add(player);
                        System.out.println("- Player: " + player.getKey());
                    }    
                    
                    //starts a virtual thread for the game
                    executorService.submit(() -> {
                        try {
                            //warn of started game
                            for(Map.Entry<String, SocketChannel> player : gamePlayers ){
                                send(player.getValue(),"Game #" + currentGameId + " has started!\n",null);
                            }

                            run_game(gamePlayers);
                            System.out.println("Game #" + currentGameId + " has finished.");
                        } catch (Exception ex) {
                            System.out.println("Error in running the game: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
                    connectedPlayers.clear(); // Clear the list for the next game
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void run_game(List<Map.Entry<String, SocketChannel>> players) throws Exception{
        final Game game = new Game(players);
        int num_rounds = 4;//hardcoded for now        
        int max_attempts = 6;
        game.start(num_rounds);

        runningGames.add(game);

        //loop through rounds
        for(int i = 0; i < num_rounds;i++){
            Map.Entry<String, SocketChannel> roundLeader = game.get_word_chooser();
            List<Map.Entry<String, SocketChannel>> guessers = new ArrayList<>(game.getPlayers());
            guessers.remove(roundLeader);

            chooseWord(roundLeader,game,guessers);


            //loop through attempts
            for (int j = 0; j < max_attempts; j++) {
                int turn_number = j + 1;

                // Create a new ExecutorService for each iteration of the outer loop
                //Creates virtual threads
                var executorService = Executors.newVirtualThreadPerTaskExecutor();
                List<Map.Entry<String, SocketChannel>> winners = new ArrayList<>();

                // Create a set to keep track of users who have submitted their guesses
                Set<Map.Entry<String, SocketChannel>> completedGuessers = ConcurrentHashMap.newKeySet();



                for (Map.Entry<String, SocketChannel> guesser : guessers) {
                    executorService.submit(() -> {
                        try {

                            send(guesser.getValue(), "Attempt number #" + turn_number + "!\n", null);


                            String guess = guessWord(guesser,roundLeader, game, game.get_word().length());
                            String guess_result = game.give_guess(guess);
                            if(guess_result.equals("!W")){
                                winners.add(guesser);
                                send(guesser.getValue(), "You guessed the word!", null);
                            }
                            else{
                                send(guesser.getValue(), guess_result, null);
                            }
                        } 
                        catch (Exception e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace(); 
                            System.exit(1);
                        }
                        finally {
                            completedGuessers.add(guesser);
                            // Check if all guessers have submitted their guesses
                            if (completedGuessers.size() < guessers.size()) {
                                try {
                                    send(guesser.getValue(), "Waiting for other guesses to be submitted...", null);
                                } 
                                catch (Exception ex) {
                                    Thread.currentThread().interrupt(); 
                                    ex.printStackTrace();
                                }

                            }
                        }
                    });   
                }


            
                executorService.shutdown();

                try {
                    //awaiting for all the tasks to be finished
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace(); 
                    System.exit(1);
                }
            
                if(!winners.isEmpty()){
                    game.setRoundResults(winners);
                    for(var player : connectedPlayers){
                        if(winners.contains(player)){
                            send(player.getValue(),"Congratulations! You guessed the word!",null);
                        }
                        else{
                            send(player.getValue(),"Someone guessed the word!",null );
                        }
                    }
                    break;
                }

            }

            game.newRound();
            sendLeaderboard(game);
            
        }
    }

    private void chooseWord(Map.Entry<String, SocketChannel> roundLeader, Game game, List<Map.Entry<String, SocketChannel>> guessers) throws Exception{
        //warn guessers who's choosing the word
        for(Map.Entry<String, SocketChannel> guesser : guessers){
            send(guesser.getValue(), roundLeader + " is choosing the word!\n",null);
        }

        String message = "You're this round captain! Choose a word: ";
        String responseString = " ";
        while(true){
            SocketChannel socket = game.getSocket(roundLeader.getKey());
            send(socket, message,"REPLY");
            responseString = receive(socket)[1];
            if(responseString == null){
                send(socket,"You can not enter empty a empty word!",null);
            }
            else if (responseString.contains(" ")) {
                // Check if the response contains a space
                send(socket, "Your word cannot contain spaces!", null);
            } 
            else if (!responseString.matches("[a-zA-Z]+")) {
                // Check if the response contains only letters
                send(socket, "Your word can only contain letters!", null);
            }
            else{        
                game.set_word(responseString);
                send(socket,"Awaiting for the other users guessess",null);
                return;
            }
        }
    }

    private String guessWord(Map.Entry<String, SocketChannel> player, Map.Entry<String, SocketChannel> roundLeader, Game game, int wordLength) throws Exception{
        String message = "Try to guess the " + wordLength + " letters word:";
        String responseString = "";
        while(true){
            try
            {
                SocketChannel socket = game.getSocket(roundLeader.getKey());
                    send(socket, message,"REPLY");
                responseString = receive(socket)[1];
                if(responseString == null){
                    send(socket,"You can not enter empty a empty word!",null);
                }
                else if(responseString.length() != wordLength){
                    send(socket,"The given word must be " + wordLength + " characters long!",null);
                }
                else if (responseString.contains(" ")) {
                    // Check if the response contains a space
                    send(socket, "Your word cannot contain spaces!",null);
                } 
                else if (!responseString.matches("[a-zA-Z]+")) {
                    // Check if the response contains only letters
                    send(socket, "Your word can only contain letters!",null);
                }
                else{        
                    //send message to the host about the guess
                    send(socket, socket + " guessed the word " + responseString + "!", null);
                    return responseString;
                }
            }
            catch(Exception e){}
            
        }
    }

    private void sendLeaderboard(Game game) throws Exception{
        List<Map.Entry<Map.Entry<String, SocketChannel>, Integer>> entryList = new ArrayList<>(game.getScores().entrySet());

        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
        int rank = 1;
        for (Map.Entry<Map.Entry<String, SocketChannel>, Integer> entry : entryList) {
            Map.Entry<String, SocketChannel> socket = entry.getKey();
            Integer score = entry.getValue();
            leaderboard.append(rank).append(". Socket: ").append(socket).append(", Score: ").append(score).append("\n");
            rank++;
        }

        for (Map.Entry<Map.Entry<String, SocketChannel>, Integer> entry : entryList) {
            Map.Entry<String, SocketChannel> socket = entry.getKey();
            String leaderboardString = leaderboard.toString();
            send(socket.getValue(), leaderboardString, null);
        }       
    }

    public void send(SocketChannel socket,String message,String token) throws Exception {
        
        String sentMessage;
        if(token != null){
            sentMessage = token + "|" + message;
        }
        else{
            sentMessage = message;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);      
        buffer.clear();                                     
        buffer.put(sentMessage.getBytes());                    
        buffer.flip();                                   
        while (buffer.hasRemaining()) {                     
            socket.write(buffer);
        }
    }

    //returns the String and the token
    public String[] receive(SocketChannel socket) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);        
        int bytesRead = socket.read(buffer);    
        String response = new String(buffer.array(), 0, bytesRead);
        if(response.contains("|"))
        {
            String[] parts = response.split("\\|");
            return parts;
        }
        else{
            String[] result = new String[2];
            result[0] = null;
            result[1] = response;
            return result;        }
    }

}
