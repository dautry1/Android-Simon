package edu.umsl.simon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class GameOverFragment extends Fragment {

    private Button mButtonOK;
    private Button mButtonCorrect;
    private TextView mTextViewYourScore;
    private TextView mTextViewHighScores;
    private ArrayList<Integer> highScoresList = new ArrayList<>();
    private String highScoresString;

    public GameOverFragment() {
        // Required empty public constructor
    }

    private GameOverListener listener;


    public interface GameOverListener {
        public void gameOver();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_over, parent, false);
    }

    @Override

    public void onViewCreated(View view, Bundle savedInstanceState) {
        mButtonOK = view.findViewById(R.id.buttonOK);
        mButtonCorrect = view.findViewById(R.id.buttonCorrect);
        mTextViewHighScores = view.findViewById((R.id.high_scores));
        mTextViewYourScore = view.findViewById(R.id.yourScore);
        highScoresList = MainActivity.simonModel.GetHighScores();
        highScoresString = "High Scores\n"; // create high score list in text form for display
        for (int i = 0; i < 10; i++){   // yes I know this a brute foce approach to displaying the high score list
            highScoresString = highScoresString + Integer.toString(highScoresList.get(i)) + "\n";
        }
        mTextViewHighScores.setText(highScoresString);
        mTextViewYourScore.setText(String.format("Your score:  %d", MainActivity.simonModel.getCurrentScore()));

        // now rub it in flashing the correct color at the player
        // this animation code is from https://stackoverflow.com/questions/4852281/android-how-can-i-make-a-button-flash
        final Animation animation = new AlphaAnimation(1,0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        mButtonCorrect.startAnimation(animation);

        switch (MainActivity.simonModel.getNextCorrectButton()) {  // determine what button should have been pressed
            case 0:
                mButtonCorrect.setBackgroundColor(getResources().getColor(R.color.colorLightRed));
                break;
            case 1:
                mButtonCorrect.setBackgroundColor(getResources().getColor(R.color.colorLightGreen));
                break;
            case 2:
                mButtonCorrect.setBackgroundColor(getResources().getColor(R.color.colorLightOrange));
                break;
            case 3:
                mButtonCorrect.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                break;
        }


        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.gameOver();
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameOverFragment.GameOverListener) {
            listener = (GameOverFragment.GameOverListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}