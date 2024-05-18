import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;





public class Player {

    private final int connectionAttempts = 5;
    private final int connectionTimeoutsSec = 5;

    private final int port;
    private final String host;
    private SocketChannel socket;
    private Auth auth;

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
        //this.auth = new Auth();
    }

    public void createAccount(String username, String password){
        auth.register(username, password);
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

    private void comunication(Player player, Boolean registration){
        int connectionCounter = 1;
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        //waiting for connection

        try{
            while(true){
                try{
                    System.out.println("Connection attempt number #" + connectionCounter);
                    player.connect();
                    System.out.println("Connected to server: " + player.socket.getRemoteAddress() + "!");
                    break;
                }
                catch ( IOException e){
                    connectionCounter++;
                    if(connectionCounter > player.connectionAttempts){
                        System.out.println("Exceeded connection attempts to the server!");
                        System.exit(0);
                    }
                    System.out.println("Connection attempt failed. Retrying...");
                    Thread.sleep(player.connectionTimeoutsSec * 1000); //pass it to mili
                }
            }

            boolean responded = false;
            
            while (!responded)
            {
                try
                {
                    String token;
                    if(registration){
                        token = "REGISTRATION";
                    }else{
                        token = "LOGIN";
                    }
                    send(player.getUsername(), token);
                    send(player.password,token);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);        
                    int bytesRead = socket.read(buffer);    
                    String response = new String(buffer.array(), 0, bytesRead);

                    if (response == "OKUSERNAME")
                        responded = true;
                        break;
                }
                catch ( IOException e){
                    connectionCounter++;
                    if(connectionCounter > player.connectionAttempts){
                        System.out.println("Exceeded connection attempts to the server!");
                        System.exit(0);
                    }
                    System.out.println("Connection attempt failed. Retrying...");
                    Thread.sleep(player.connectionTimeoutsSec * 1000); //pass it to mili
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

            }
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: java Player <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try{
            Player player = new Player(port, hostname);

           
            String message;

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));


            System.out.println("Do you wish to LOGIN or REGISTER");
            message = consoleReader.readLine();
            var registration = false;
            if(message.equals("LOGIN")){
                System.out.println("****LOGIN****");
                System.out.println("username: ");
                String username = consoleReader.readLine();
                player.username = username;
                System.out.println("password: ");
                String password = consoleReader.readLine();
                player.password = password;
              
    
            }else{
                System.out.println("****REGISTRATION****");
                System.out.println("username: ");
                String username = consoleReader.readLine();
                player.username = username;
                System.out.println("password: ");
                player.password = consoleReader.readLine();
                registration = true;
                
            }
            player.comunication(player, registration);

        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }        

    }

    public String getUsername()
    {
        return username;
    }

    public SocketChannel getSocket()
    {
        return socket;
    }
}