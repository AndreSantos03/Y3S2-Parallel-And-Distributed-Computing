import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;




public class Server {

    private List<SocketChannel> connectedPlayers = new ArrayList<>();
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
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Server is listening on port " + port);

            //hardcoded 3 players
            while (connectedPlayers.size() < 3) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("New client connected: " + socketChannel);
                connectedPlayers.add(socketChannel);
            }

            run_game();

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void run_game() throws Exception{
        int num_rounds = 1;//hardcoded for now        
        int max_attempts = 6;
        game = new Game(connectedPlayers);
        game.start(num_rounds);

                //loop through rounds
        for(int i = 0; i < num_rounds;i++){
            SocketChannel roundLeader = game.get_word_chooser();
            chooseWord(roundLeader);
            List<SocketChannel> guessers = new ArrayList<>(connectedPlayers);
            guessers.remove(roundLeader);
                    
            ReentrantLock  guessLock = new ReentrantLock();
            //loop through attempts
            for (int j = 0; j < 1; j++) {
                // Create a new ExecutorService for each iteration of the outer loop
                var executorService = Executors.newVirtualThreadPerTaskExecutor();
            
                for (SocketChannel guesser : guessers) {
                    executorService.submit(() -> {
                        try {
                            guessLock.lock(); // Acquire the lock
                            String guess = guessWord(guesser, game.get_word().length());
                            String guess_result = game.give_guess(guess);
                        } 
                        catch (Exception e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace(); 
                            System.exit(1);

                        }
                        finally {
                            guessLock.unlock(); 
                        }
            
                    });
                }
            
                executorService.shutdown();
                
                try {
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace(); 
                    System.exit(1);
                }
            }
        }
    }

    private void chooseWord(SocketChannel player) throws Exception{
        String message = "You're this round captain! Choose a word: ";
        String responseString = " ";
        while(true){
            send(player, message,"REPLY");
            responseString = receive(player)[1];
            if(responseString == null){
                send(player,"You can not enter empty a empty word!",null);
            }
            else if (responseString.contains(" ")) {
                // Check if the response contains a space
                send(player, "Your word cannot contain spaces!", null);
            } 
            else if (!responseString.matches("[a-zA-Z]+")) {
                // Check if the response contains only letters
                send(player, "Your word can only contain letters!", null);
            }
            else{        
                game.set_word(responseString);
                return;
            }
        }
    }

    private String guessWord(SocketChannel player, int wordLength) throws Exception{
        String message = "Try to guess the " + wordLength + " letters word:";
        String responseString = " ";
        while(true){
            send(player, message,"REPLY");
            responseString = receive(player)[1];
            if(responseString == null){
                send(player,"You can not enter empty a empty word!",null);
            }
            if(responseString.length() != wordLength){
                send(player,"The given word must be " + wordLength + " characters long!",null);
            }
            else if (responseString.contains(" ")) {
                // Check if the response contains a space
                send(player, "Your word cannot contain spaces!",null);
            } 
            else if (!responseString.matches("[a-zA-Z]+")) {
                // Check if the response contains only letters
                send(player, "Your word can only contain letters!",null);
            }
            else{        
                return responseString;
            }
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
        if(response.contains("|")){
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
