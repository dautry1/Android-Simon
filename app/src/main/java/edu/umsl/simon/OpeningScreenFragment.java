package edu.umsl.simon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class OpeningScreenFragment extends Fragment {
    final int easyLevel =5;
    final int mediumLevel = 3;
    final int hardLevel = 1;
    private Button mButtonEasy;
    private Button mButtonMedium;
    private Button mButtonHard;
    private TextView mTextViewHighScores;
    private String highScoresString;    // used for lazy way of displaying high scores list
    private ArrayList<Integer> highScoresList = new ArrayList<>();

    private DifficultySelectedListener listener;

    public interface DifficultySelectedListener {
        public void difficultySelected(int difficulty);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_opening_screen, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        ListView lv = (ListView) view.findViewById(R.id.lvSome);
//        lv.setAdapter(adapter);

        mButtonEasy = view.findViewById(R.id.buttonEasy);
        mButtonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "Pressed Easy button");
                listener.difficultySelected(easyLevel);
            }
        });

        mButtonMedium = view.findViewById(R.id.buttonMedium);
        mButtonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "Pressed Medium button");
                listener.difficultySelected(mediumLevel);
            }
        });

        mButtonHard = view.findViewById(R.id.buttonHard);
        mButtonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "Pressed Hard button");
                listener.difficultySelected(hardLevel);
            }
        });

        mTextViewHighScores = view.findViewById((R.id.high_scores));
        highScoresList = MainActivity.simonModel.GetHighScores();
        highScoresString = "High Scores\n";
        for (int i = 0; i < 10; i++){   // convert high scores list to string for displaying
            highScoresString = highScoresString + Integer.toString(highScoresList.get(i)) + "\n";
        }
        mTextViewHighScores.setText(highScoresString);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DifficultySelectedListener) {
            listener = (DifficultySelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();}  // Always call the superclass method first

    @Override
    public void onPause(){
        super.onPause();
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
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}

