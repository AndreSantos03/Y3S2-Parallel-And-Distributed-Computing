import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;




public class Player {

    private final int port;
    private final String host;
    private SocketChannel socket;

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

    private void connect() throws IOException{
        this.socket = SocketChannel.open();
        this.socket.connect(new InetSocketAddress(this.host, this.port));
    }

    public void send(String message,String token) throws Exception {
        String sentMessage;
        if(token == null){
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
        else{
            String[] result = new String[2];
            result[0] = null;
            result[1] = response;
            return result;        }
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
            player.connect();

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                String message;
                String token;

                String[] serverResponse = player.receive();
                message = serverResponse[1];
                token = serverResponse[0];

                System.out.println(message);
                
                //If there's a token
                if(token != null){
                    if(token.contains("REPLY")){
                        System.out.print("Send to server: ");
                        message = consoleReader.readLine();
                        player.send(message,null);
                    }
                }

        }

        }
        catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
        }        

    }
}