package edu.umsl.simon;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements OpeningScreenFragment.DifficultySelectedListener, PlayGameFragment.GameResultListener, GameOverFragment.GameOverListener {

    public static SimonModel simonModel;
    private FragmentTransaction ft;
    public Activity mActivity = MainActivity.this;  // important for operations in other frags and objects

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simonModel = new SimonModel();      // stores data and does other important functions
        simonModel.SetActivity(mActivity);  // tell the model what the MainActivity activity is
        simonModel.LoadHighScoresFromShared();          // restore high scores if available
        setContentView(R.layout.activity_main);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new OpeningScreenFragment());
        ft.commit();
    }

    public void difficultySelected(int difficulty) {        // listener for difficulty selection screen

        simonModel.setDifficultyLevel(difficulty);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new PlayGameFragment());
        ft.commit();
    }

    public void gameResult(int result) {     // listener for gameplay screen
        if (result > -1)                    // > -1 means player did not cancel game
        {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new GameOverFragment());
            ft.commit();    // switch to game over screen
        } else {                // player canceled game
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new OpeningScreenFragment());
            ft.commit();    // switch to difficulty selection screen to start new game
        }
    }

    public void gameOver() {    // listener for game over fragment
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new OpeningScreenFragment());
        ft.commit();
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        simonModel.LoadHighScoresFromShared();  // restore high score list
    }

    public void onPause() {
        super.onPause();
        simonModel.SaveHighScoresToShared();    // save high scores to sharedprefs
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        simonModel = null;
        ft = null;
        super.onDestroy();
    }

}