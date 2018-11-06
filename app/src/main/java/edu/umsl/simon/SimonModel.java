package edu.umsl.simon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;        // used to make saving an array list to shared preferences much easier
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dautry1 on 2/15/2018.
 */

public class SimonModel {

    private final int maxDifficultyLevel = 6;
    private final String highScoresKey = "HighScores";
    private ArrayList<Integer> computerSequence = new ArrayList<>();
    private ArrayList<Integer> playerSequence = new ArrayList<>();
    private ArrayList<Integer> highScores = new ArrayList<>();
    private int sequenceNumber = 0;
    private int roundCounter = 0;
    private int randomColorNum;
    private Random randomNumGenerator = new Random();
    private int highScore;
    private int currentScore;
    private int difficultyLevel;
    int nextCorrectButton;      // this is used to show the monkey player what button should have been pressed
    private Activity mainFragActivity;


    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setHighScore (int highScore)
        {
            this.highScore = highScore;
        }

    public int getHighScore ()
    {
        return highScores.get(0);
    }

    public void setCurrentScore (int currentScore)
    {
        this.currentScore = currentScore;
    }

    public int getCurrentScore ()
    {
        return currentScore;
    }

    public int getNextCorrectButton(){
        return nextCorrectButton;
    }

    public ArrayList<Integer> GetHighScores(){
        return highScores;
    }

    public void checkForNewHighScore(){
        int score = getCurrentScore();
        int oldscore = score;
        for (int i = 0; i<10; i++) {
            if (score > highScores.get(i)) {
                oldscore = highScores.get(i);
                highScores.set(i, score);       // replace lower score with higher one
                score = oldscore;               // and allow numbers to trickle down
            }
        }
    }

    public void StartNewGame() {
        sequenceNumber = 0;
        roundCounter = 0;
        currentScore = 0;
        computerSequence.clear();
        playerSequence.clear();
    }

    public int GetComputerSequence() {     // returns the next color in the randomly generated sequence
        int returnResult;                  // or -1 if at end of sequence
            if (roundCounter < sequenceNumber) {
                returnResult = computerSequence.get(roundCounter);
                roundCounter++;
            return returnResult;
        } else {
                roundCounter = 0;
                return -1;
        }
    }

    public int GetNextRandomColor() {
        if (computerSequence.size() ==0) {              // generate 1 or more initial random sequence colors based on difficulty
            for (sequenceNumber = 0; sequenceNumber < (maxDifficultyLevel - difficultyLevel); sequenceNumber++)
            {
                randomColorNum = randomNumGenerator.nextInt(4); //randomly generate a 0 to 3 integer
                if (computerSequence.size()==0){    // this is needed if the player fails to press any button at the beginning of a sequence
                    nextCorrectButton = randomColorNum;
                }
                computerSequence.add(randomColorNum);
            }
        }
        else {
            randomColorNum = randomNumGenerator.nextInt(4);
            computerSequence.add(randomColorNum);
            sequenceNumber++;
        }
        return randomColorNum;
    }

    public int GetLastColorInSequence(){
        return computerSequence.get(computerSequence.size() - 1);
    };


    public int CheckPlayerResponse(int colorPick) {     // self explanatory
        int result = 0;                                 // result =1 if correct, 0 if wrong button pressed
        if (roundCounter < sequenceNumber) {
            if (computerSequence.get(roundCounter) == colorPick) {

                roundCounter++;
                if (roundCounter < sequenceNumber)
                {
                    nextCorrectButton=computerSequence.get(roundCounter);
                }
                result = 1;
            } else {
                nextCorrectButton = computerSequence.get(roundCounter);
                result = 0;
            }
        }
        if (roundCounter == sequenceNumber) {
            roundCounter = 0;
            nextCorrectButton = computerSequence.get(0);
            GetNextRandomColor();
            result = 2;
        }
        return result;
    }

    public void SetActivity(Activity Act) {
        mainFragActivity = Act;
    }

    // some of the code below comes from https://freakycoder.com/android-notes-40-how-to-save-and-get-arraylist-into-sharedpreference-7d1f044bc79a
    public void SaveHighScoresToShared(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainFragActivity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(highScores);
        editor.putString(highScoresKey, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public void LoadHighScoresFromShared() {
//        SharedPreferences mySPrefs =PreferenceManager.getDefaultSharedPreferences(mainFragActivity);
//        SharedPreferences.Editor editor = mySPrefs.edit();  /// the part was used for testing
//        editor.remove(highScoresKey);
//        editor.apply();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainFragActivity);
        Gson gson = new Gson();
        if (prefs.contains(highScoresKey)) {            // make sure high scores are there to retrieve
            String json = prefs.getString(highScoresKey, null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> stringArray;
            stringArray = gson.fromJson(json, type);
            highScores.clear();
            for (int i = 0; i < 10; i++) {      // convert the string arraylist to int arraylist
                highScores.add(Integer.parseInt(stringArray.get(i)));
            }
        } else {
            highScores.clear();                 // create a new blank high scores list
            for (int i = 0; i < 10; i++) {
                this.highScores.add(0);
            }
        }
    }
}




