import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;






public class Player {


    private static final int CONNECTION_ATTEMPTS = 5;
    private static final int CONNECTION_TIMEOUT_SEC = 5;


    private final int port;
    private final String host;
    private SocketChannel socket;

    private SSLSocket sslSocket; //socket for server authentication
    private PrintWriter out;
    private BufferedReader in;
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));


    private String username;
    private String password;
    
    // ANSI escape code for yellow color
    String yellowColor = "\u001B[33m";
    // ANSI escape code for green color
    String greenColor = "\u001B[32m";
    // ANSI escape code for red color
    String redColor = "\u001B[31m";
    // ANSI escape code for reset color
    String resetColor = "\u001B[0m";

    // Yellow square
    String yellowSquare = yellowColor + "█" + resetColor;
    // Green square
    String greenSquare = greenColor + "█" + resetColor;
    // Red square
    String redSquare = redColor + "█" + resetColor;

    public Player(int port, String host){
        this.port = port;
        this.host = host;

    }

    private SSLSocket connectSSL() throws IOException {
        System.setProperty("javax.net.ssl.trustStore", "assign2/src/clientTruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wordle");

        SSLSocketFactory sslsf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            sslSocket = (SSLSocket) sslsf.createSocket(host, port);
            sslSocket.startHandshake(); // Ensure the handshake is complete
            System.out.println("SSL Session:");
            System.out.println(" - Protocol : " + sslSocket.getSession().getProtocol());
            System.out.println(" - Cipher suite : " + sslSocket.getSession().getCipherSuite());
            System.out.println("Connected to server: " + sslSocket.getInetAddress() + "!");
            out = new PrintWriter(sslSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        } catch (IOException e) {
            if (sslSocket != null) {
                sslSocket.close();
            }
            throw e;
        }
        return sslSocket;
    }

    private void connect() throws IOException{
        this.socket = SocketChannel.open();
        this.socket.connect(new InetSocketAddress(this.host, this.port));
    }

    public void send(String message,String token) throws Exception {
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
    public String[] receive() throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(1024);        
            int bytesRead = socket.read(buffer);    
            String response = new String(buffer.array(), 0, bytesRead);


            //replace with the ansi squares
            response = response
            .replace("red", redSquare)
            .replace("green", greenSquare)
            .replace("yellow", yellowSquare);

            if(response.contains("|")){
                String[] parts = response.split("\\|");
                return parts;
            }
            else
            {
                String[] result = new String[2];
                result[0] = null;
                result[1] = response;
                return result;        
            }
    }


    private void gameStart(Player player){
        int connectionCounter = 1;
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        //waiting for connection
        try{
            while(true){
                try{
                    System.out.println("Connection attempt number #" + connectionCounter);

                    //player.connect();

                    System.out.println("Connected to server: " + player.socket.getRemoteAddress() + "!");
                    break;
                }
                catch ( IOException e){
                    connectionCounter++;
                    if(connectionCounter > CONNECTION_ATTEMPTS){
                        System.out.println("Exceeded connection attempts to the server!");
                        System.exit(0);
                    }
                    System.out.println("Connection attempt failed. Retrying...");
                    Thread.sleep(player.CONNECTION_TIMEOUT_SEC * 1000); //pass it to mili
                }
            }

            while(true){
                String message;
                String token;

                String[] serverResponse = player.receive();
                message = serverResponse[1];
                token = serverResponse[0];
                
                //If there's a token
                if(token != null){
                    if(token.contains("REPLY")){
                        System.out.print("Send to server: ");
                        message = consoleReader.readLine();
                        player.send(message,null);
                    }
                }
                else{
                    System.out.println(message);
                }

            }
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performLoginOrRegister() throws IOException {
        boolean responded = false;
        while (!responded) {
            System.out.println("Do you wish to LOGIN or REGISTER");
            String action = consoleReader.readLine().toUpperCase();
            String token;

            if ("LOGIN".equals(action)) {
                System.out.println("**** LOGIN ****");
                token = "LOGIN";
            } else if ("REGISTER".equals(action)) {
                System.out.println("**** REGISTRATION ****");
                token = "REGISTRATION";
            } else {
                System.out.println("Invalid option. Please choose LOGIN or REGISTER.");
                continue;
            }

            System.out.print("Username: ");
            username = consoleReader.readLine();
            System.out.print("Password: ");
            password = consoleReader.readLine();

            out.println(username+"|"+token);
            out.println(password+"|"+token);

            String[] response = in.readLine().split("\\|");
            if ("OK".equals(response[1])) {
                responded = true;
                System.out.println("Authentication successful.");
            } else {
                System.out.println("Authentication failed. Try again.");
            }
        }
    }

    public void startGame() throws IOException {
        connect();
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Player <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Player player = new Player(port, hostname);
        int attempt = 0;

        while (attempt < CONNECTION_ATTEMPTS) {
            try {
                player.connectSSL();
                player.performLoginOrRegister();
                player.gameStart(player);
                break;
            } catch (IOException e) {
                System.err.println("Connection attempt " + (attempt + 1) + " failed: " + e.getMessage());
                attempt++;
                if (attempt >= CONNECTION_ATTEMPTS) {
                    System.err.println("Exceeded maximum connection attempts. Exiting.");
                    break;
                }
                try {
                    Thread.sleep(CONNECTION_TIMEOUT_SEC * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    public String getUsername () {
        return username;
    }

    public SocketChannel getSocket ()
    {
        return socket;
    }
}
