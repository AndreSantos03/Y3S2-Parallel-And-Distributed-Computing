
import java.nio.channels.SocketChannel;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class Game {
    private List<Map.Entry<String, SocketChannel>> userSockets = new ArrayList<>();
    private Map<Map.Entry<String, SocketChannel>,Integer> scores = new HashMap<>();
    private List<Map.Entry<String, SocketChannel>> roundWordChosers = new ArrayList<>(); // list to iterate through rounds with who's choosing the word
    private Integer currentRound = 0;
    private String currentWord;


    public Game( List<Map.Entry<String, SocketChannel>> userSockets) {
        this.userSockets = userSockets;
    }

    public void start(int num_rounds) {
        //Creates the rounds with who's choosing the words
        for(int i = 0; i < num_rounds; i++){
            for(Map.Entry<String, SocketChannel> player : userSockets){
                roundWordChosers.add(player);
            }
        }

        //initialize scores
        for(Map.Entry<String, SocketChannel> player : userSockets){
            scores.put(player, 0);
        }
    }    

    public Map<Map.Entry<String, SocketChannel>,Integer> getScores(){
        return scores;
    }

    //returns -1 if the game is over, else returns number of round
    public int newRound(){
        currentRound ++;
        if(currentRound > roundWordChosers.size()){
            return -1;
        }
        return currentRound;
    }
    
    public Map.Entry<String, SocketChannel> get_word_chooser(){
        return roundWordChosers.get(currentRound);
    }

    public void set_word(String chosenWord){
        currentWord = chosenWord.toUpperCase();
    }

    public String get_word(){
        return currentWord;
    }


    public String give_guess(String guess){
        if(guess.toUpperCase().equals(currentWord)){
            return "!W";
        }


        Map<Character, Integer> letterFrequency = new HashMap<>();
        for (char ch : currentWord.toCharArray()) {
            // Update the count in the map
            letterFrequency.put(ch, letterFrequency.getOrDefault(ch, 0) + 1);
        }

        String squares = "";
        for (int i = 0; i < guess.length(); i++) {
            char charGuess = guess.charAt(i);
            charGuess = Character.toUpperCase(charGuess);
            char charWord = currentWord.charAt(i);
            if(charGuess == charWord){
                squares += "green ";
                letterFrequency.put(charWord, letterFrequency.get(charWord) - 1);
            }
            else if(letterFrequency.containsKey(charGuess)){
                if(letterFrequency.get(charGuess) != 0){
                    squares += "yellow ";
                    letterFrequency.put(charWord, letterFrequency.get(charWord) - 1);
                }
                else{
                    squares += "red ";
                }
            }
            else{
                squares += "red ";
            }
        }


        String currentWordWithDash = String.join("-", guess.split(""));
        String squaresWithDash = String.join("-", squares.split(" "));

        return currentWordWithDash + '\n' + squaresWithDash + '\n';
    }

    public void setRoundResults(List<Map.Entry<String, SocketChannel>> roundWinners){
        for(Map.Entry<String, SocketChannel> player :roundWinners ){
            scores.put(player, scores.get(player) + 1);
        }
    }

    public void updatePlayer(Map.Entry<String, SocketChannel> newplayer)
    {
        for (Map.Entry<String, SocketChannel> player : userSockets)
        {
            if (player.getKey() == newplayer.getKey())
            {
                player.setValue(newplayer.getValue());
                return;
            }
        }
    }

    public SocketChannel getSocket(String username)
    {
        for (Map.Entry<String, SocketChannel> player : userSockets)
        {
            if (player.getKey() == username)
            {
                return player.getValue();
            }
        }
        return null;
    }

    public List<Map.Entry<String, SocketChannel>> getPlayers()
    {
        return userSockets;
    }
}
    