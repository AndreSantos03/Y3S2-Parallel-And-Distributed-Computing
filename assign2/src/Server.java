import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class Server {

    private List<Map.Entry<String, SocketChannel>> connectedPlayers = new ArrayList<>();
    private List<Map.Entry<String, SocketChannel>> allPlayers = new ArrayList<>();
    private Map<String, SSLSocket> authenticatedSSLSockets = new HashMap<>();
    private List<Game> runningGames = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition playerAvailable = lock.newCondition();
    private int gameId = 1;
    private Auth auth;

    private int playerPort = 8001;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <game_player_count>");
            return;
        }

        Server server = new Server();

        int port = Integer.parseInt(args[0]);
        int playersPerGame = Integer.parseInt(args[1]);

        // Start the SSL connection listener in a separate thread
        Thread sslThread = new Thread(() -> server.sslConnection(port, playersPerGame));
        sslThread.start();

        // Start the simple TCP connection handler in the main thread or another thread
        Thread tcpThread = new Thread(() -> server.simpleConnection(server.playerPort, playersPerGame));
        tcpThread.start();

        server.playerPort++;

        try {
            sslThread.join();
            tcpThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        return auth.authenticate(username, password);
    }

    private void sslConnection(int port, int playersPerGame) {
        System.setProperty("javax.net.ssl.keyStore", "assign2/src/serverKeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wordle");

        SSLServerSocketFactory sslssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try (SSLServerSocket listener = (SSLServerSocket) sslssf.createServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                SSLSocket sslSocket = (SSLSocket) listener.accept();
                Thread.startVirtualThread(() -> handleClient(sslSocket, playersPerGame));
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(SSLSocket sslSocket, int playersPerGame) {
        try {
            sslSocket.startHandshake();
            System.out.println("SSL Session:");
            System.out.println(" - Protocol : " + sslSocket.getSession().getProtocol());
            System.out.println(" - Cipher suite : " + sslSocket.getSession().getCipherSuite());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true)) {

                String[] inputLine;
                String user, token;
                auth = new Auth();

                inputLine = in.readLine().split("\\|");
                user = inputLine[0];
                token = inputLine[1];
                inputLine = in.readLine().split("\\|");
                String password = inputLine[0];

                if (Objects.equals(token, "REGISTRATION")) {
                    System.out.println("Registering user: " + user);
                    auth.register(user, password);
                }

                System.out.println("Logging in user: " + user);
                if (auth.authenticate(user, password)) {
                    System.out.println("User authenticated: " + user);
                    out.println("OK");
                    out.println("SWITCH_TO_TCP" + "|" + (playerPort));

                    // Add the authenticated user to the authenticatedSSLSockets map
                    lock.lock();
                    try {
                        authenticatedSSLSockets.put(user, sslSocket);
                        playerAvailable.signalAll();
                    } finally {
                        lock.unlock();
                    }

                } else {
                    System.out.println("User not authenticated: " + user);
                    out.println("ERROR");
                }
            }
        } catch (Exception e) {
            System.out.println("Handshake failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                sslSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void simpleConnection(int port, int playersPerGame) {
        this.auth = new Auth();
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Server is listening on port " + port);

            var executorService = Executors.newVirtualThreadPerTaskExecutor();

            while (true) {
                lock.lock();
                try {
                    while (authenticatedSSLSockets.isEmpty()) {
                        playerAvailable.await();
                    }

                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("New client connected: " + socketChannel.isConnected());

                    // Read username from the client to match with authenticated SSLSocket
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    socketChannel.read(buffer);
                    buffer.flip();
                    String username = new String(buffer.array(), buffer.position(), buffer.limit()).trim();



                    authenticatedSSLSockets.remove(username);

                    handleNewPlayer(socketChannel, playersPerGame, executorService, username);
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleNewPlayer(SocketChannel socketChannel, int playersPerGame, ExecutorService executorService, String username) {
        boolean isPlayerRejoin = true;
        try {

            isPlayerRejoin = false;
            lock.lock();
            try {
                for (Map.Entry<String, SocketChannel> player : allPlayers) {
                    if (Objects.equals(player.getKey(), username)) {
                        player.setValue(socketChannel);
                        isPlayerRejoin = true;
                        break;
                    }
                }
                if (!isPlayerRejoin) {
                    allPlayers.add(Map.entry(username, socketChannel));
                }
            } finally {
                lock.unlock();
            }

            Map.Entry<String, SocketChannel> playerEntry = null;
            lock.lock();
            try {
                for (Map.Entry<String, SocketChannel> entry : allPlayers) {
                    if (entry.getValue() == socketChannel) {
                        playerEntry = entry;
                        break;
                    }
                }
            } finally {
                lock.unlock();
            }

            if (!isPlayerRejoin) {
                System.out.println("New client connected: " + socketChannel);
                connectedPlayers.add(playerEntry);
            } else {
                lock.lock();
                try {
                    for (Game game : runningGames) {
                        game.updatePlayer(playerEntry);
                    }
                } finally {
                    lock.unlock();
                }
            }

            if (connectedPlayers.size() >= playersPerGame) {
                List<Map.Entry<String, SocketChannel>> gamePlayers = new ArrayList<>();
                int currentGameId = gameId++; // Assign the current game ID

                System.out.println("Starting Game #" + currentGameId + " with " + connectedPlayers.size() + " players:");
                for (Map.Entry<String, SocketChannel> player : connectedPlayers) {
                    gamePlayers.add(player);
                    System.out.println("- Player: " + player.getKey());
                }

                executorService.submit(() -> {
                    try {
                        for (Map.Entry<String, SocketChannel> player : gamePlayers) {
                            send(player.getValue(), "Game #" + currentGameId + " has started!\n", null);
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
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void run_game(List<Map.Entry<String, SocketChannel>> players) throws Exception {
        final Game game = new Game(players);
        int num_rounds = 4; // hardcoded for now
        int max_attempts = 6;
        game.start(num_rounds);

        runningGames.add(game);

        for (int i = 0; i < num_rounds; i++) {
            Map.Entry<String, SocketChannel> roundLeader = game.get_word_chooser();
            List<Map.Entry<String, SocketChannel>> guessers = new ArrayList<>(game.getPlayers());
            guessers.remove(roundLeader);

            chooseWord(roundLeader, game, guessers);

            for (int j = 0; j < max_attempts; j++) {
                int turn_number = j + 1;

                var executorService = Executors.newVirtualThreadPerTaskExecutor();
                List<Map.Entry<String, SocketChannel>> winners = new ArrayList<>();
                Set<Map.Entry<String, SocketChannel>> completedGuessers = new HashSet<>();

                for (Map.Entry<String, SocketChannel> guesser : guessers) {
                    executorService.submit(() -> {
                        try {
                            send(guesser.getValue(), "Attempt number #" + turn_number + "!\n", null);

                            String guess = guessWord(guesser, roundLeader, game, game.get_word().length());
                            String guess_result = game.give_guess(guess);
                            if (guess_result.equals("!W")) {
                                winners.add(guesser);
                                send(guesser.getValue(), "You guessed the word!", null);
                            } else {
                                send(guesser.getValue(), guess_result, null);
                            }
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                            System.exit(1);
                        } finally {
                            lock.lock();
                            try {
                                completedGuessers.add(guesser);
                                if (completedGuessers.size() < guessers.size()) {
                                    try {
                                        send(guesser.getValue(), "Waiting for other guesses to be submitted...", null);
                                    } catch (Exception ex) {
                                        Thread.currentThread().interrupt();
                                        ex.printStackTrace();
                                    }
                                }
                            } finally {
                                lock.unlock();
                            }
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

                if (!winners.isEmpty()) {
                    game.setRoundResults(winners);
                    for (var player : connectedPlayers) {
                        if (winners.contains(player)) {
                            send(player.getValue(), "Congratulations! You guessed the word!", null);
                        } else {
                            send(player.getValue(), "Someone guessed the word!", null);
                        }
                    }
                    break;
                }
            }

            game.newRound();
            sendLeaderboard(game);
        }
    }

    private void chooseWord(Map.Entry<String, SocketChannel> roundLeader, Game game, List<Map.Entry<String, SocketChannel>> guessers) throws Exception {
        for (Map.Entry<String, SocketChannel> guesser : guessers) {
            send(guesser.getValue(), roundLeader.getKey() + " is choosing the word!\n", null);
        }

        String message = "You're this round captain! Choose a word: ";
        String responseString = " ";
        while (true) {
            SocketChannel socket = game.getSocket(roundLeader.getKey());
            send(socket, message, "REPLY");
            responseString = receive(socket)[1];
            if (responseString == null) {
                send(socket, "You cannot enter an empty word!", null);
            } else if (responseString.contains(" ")) {
                send(socket, "Your word cannot contain spaces!", null);
            } else if (!responseString.matches("[a-zA-Z]+")) {
                send(socket, "Your word can only contain letters!", null);
            } else {
                game.set_word(responseString);
                send(socket, "Awaiting the other users' guesses", null);
                return;
            }
        }
    }

    private String guessWord(Map.Entry<String, SocketChannel> player, Map.Entry<String, SocketChannel> roundLeader, Game game, int wordLength) throws Exception {
        String message = "Try to guess the " + wordLength + " letters word:";
        String responseString = "";
        while (true) {
            try {
                SocketChannel socket = game.getSocket(player.getKey());
                send(socket, message, "REPLY");
                responseString = receive(socket)[1];
                if (responseString == null) {
                    send(socket, "You cannot enter an empty word!", null);
                } else if (responseString.length() != wordLength) {
                    send(socket, "The given word must be " + wordLength + " characters long!", null);
                } else if (responseString.contains(" ")) {
                    send(socket, "Your word cannot contain spaces!", null);
                } else if (!responseString.matches("[a-zA-Z]+")) {
                    send(socket, "Your word can only contain letters!", null);
                } else {
                    send(socket, player.getKey() + " guessed the word " + responseString + "!", null);
                    return responseString;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendLeaderboard(Game game) throws Exception {
        List<Map.Entry<Map.Entry<String, SocketChannel>, Integer>> entryList = new ArrayList<>(game.getScores().entrySet());

        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
        int rank = 1;
        for (Map.Entry<Map.Entry<String, SocketChannel>, Integer> entry : entryList) {
            Map.Entry<String, SocketChannel> name = entry.getKey();
            Integer score = entry.getValue();
            leaderboard.append(rank).append(". Socket: ").append(name.getKey()).append(", Score: ").append(score).append("\n");
            rank++;
        }

        for (Map.Entry<Map.Entry<String, SocketChannel>, Integer> entry : entryList) {
            Map.Entry<String, SocketChannel> socket = entry.getKey();
            String leaderboardString = leaderboard.toString();
            send(socket.getValue(), leaderboardString, null);
        }
    }

    public void send(SocketChannel socket, String message, String token) throws Exception {
        String sentMessage;
        if (token != null) {
            sentMessage = token + "|" + message;
        } else {
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

    public String[] receive(SocketChannel socket) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socket.read(buffer);
        String response = new String(buffer.array(), 0, bytesRead);
        if (response.contains("|")) {

            return response.split("\\|");
        } else {

            return new String[]{null, response};
        }
    }
}
