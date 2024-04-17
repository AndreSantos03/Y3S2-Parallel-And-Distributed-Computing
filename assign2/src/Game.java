
import java.net.Socket;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class Game {
    private List<Socket> userSockets = new ArrayList<>();
    private Map<Socket,Integer> scores = new HashMap<>();
    private List<Socket> roundWordChosers = new ArrayList<>(); // list to iterate through rounds with who's choosing the word
    private Integer currentRound = 0;
    private String currentWord;

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


    public Game( List<Socket> userSockets) {
        this.userSockets = userSockets;
    }

    public void start(int num_rounds) {
        //Creates the rounds with who's choosing the words
        for(int i = 0; i < num_rounds; i++){
            for(Socket player : userSockets){
                roundWordChosers.add(player);
            }
        }

        //initialize scores
        for(Socket player : userSockets){
            scores.put(player, 0);
        }


    }    

    public Map<Socket,Integer> getScores(){
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

    public Socket get_word_chooser(){
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
            return guess.toUpperCase();
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
                squares += greenSquare;
                letterFrequency.put(charWord, letterFrequency.get(charWord) - 1);
            }
            else if(letterFrequency.containsKey(charGuess)){
                if(letterFrequency.get(charGuess) != 0){
                    squares += yellowSquare;
                    letterFrequency.put(charWord, letterFrequency.get(charWord) - 1);
                }
                else{
                    squares += redSquare;
                }
            }
            else{
                squares += redSquare;
            }
        }
        String currentWordWithDash = String.join("-", currentWord.split(""));
        String squaresWithDash = String.join("-", squares.split(""));

        return currentWordWithDash + '\n' + squaresWithDash + '\n';
    }

    public void setRoundResults(List<Socket> roundWinners){
        for(Socket player :roundWinners ){
            scores.put(player, scores.get(player) + 1);
        }
    }

}
    