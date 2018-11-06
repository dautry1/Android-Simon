package edu.umsl.simon;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGameFragment extends Fragment {

    private static final int red = 0;
    private static final int green = 1;
    private static final int orange = 2;
    private static final int blue = 3;
    private static final int timeDelayConst = 200;
    private static final int playTimeOutDelayConst = 900;

    private Button mButtonRed;
    private Button mButtonGreen;
    private Button mButtonOrange;
    private Button mButtonBlue;
    private Button mButtonStart;
    private TextView mTextViewHighScore;
    private TextView mTextViewCurrentScore;
    private int whichButtonPressed;
    boolean gameIsStarted = false;
    boolean gameIsCanceled = false;
    boolean gameIsOver = false;
    boolean cancelTimer = false;
    boolean closingInProgress = false;
    private int randomColor;
    private int nextColorButton;
    private int gameResult = 0;
    private int playerTimeOutDelay;
    private int showSequenceTimeDelay;
    private int runnableTimeDelay;
    private int checkAnswer;
    private GameResultListener listener;
    Handler generalHandler = new Handler();
    Handler timeOutHandler = new Handler();

    public PlayGameFragment() {
        // Required empty public constructor
    }

    public interface GameResultListener {
        public void gameResult(int result);     // return -1 for game canceled, otherwise
                                                // return player score
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_game, parent, false);
    }

    @Override

    public void onViewCreated(View view, Bundle savedInstanceState) {
        mButtonRed = view.findViewById(R.id.buttonRed);
        mButtonGreen = view.findViewById(R.id.buttonGreen);     // set up buttons and textviews
        mButtonOrange = view.findViewById(R.id.buttonOrange);
        mButtonBlue = view.findViewById(R.id.buttonBlue);
        mButtonStart = view.findViewById(R.id.buttonStart);
        mTextViewHighScore = view.findViewById(R.id.highScore);
        mTextViewHighScore.setText(String.format("Highest Score: %s", Integer.toString(MainActivity.simonModel.getHighScore())));
        mTextViewCurrentScore = view.findViewById(R.id.currentScore);
        mTextViewCurrentScore.setText("Current Score: 0");  // learn to speak English
        MainActivity.simonModel.setCurrentScore(0);

        DisableColorButtons();

        mButtonRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gameIsStarted) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mButtonRed.setBackgroundColor(getResources().getColor(R.color.colorDarkRed));
                        ButtonPressed();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonRed.setBackgroundColor(getResources().getColor(R.color.colorLightRed));
                        whichButtonPressed = 0;
                    }
                }
                return false;
            }
        });

        mButtonGreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gameIsStarted) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mButtonGreen.setBackgroundColor(getResources().getColor(R.color.colorDarkGreen));
                        ButtonPressed();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonGreen.setBackgroundColor(getResources().getColor(R.color.colorLightGreen));
                    }
                    whichButtonPressed = 1;
                }
                return false;
            }
        });

        mButtonOrange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gameIsStarted) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mButtonOrange.setBackgroundColor(getResources().getColor(R.color.colorDarkOrange));
                        ButtonPressed();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonOrange.setBackgroundColor(getResources().getColor(R.color.colorLightOrange));
                        whichButtonPressed = 2;
                    }
                }
                return false;
            }
        });

        mButtonBlue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gameIsStarted)
                    {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            mButtonBlue.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
                            ButtonPressed();
                        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            mButtonBlue.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                            Log.e("TAG", "Pressed Blue button");
                            whichButtonPressed = 3;
                        }
                    }
                    return false;
                }

        });

        mButtonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!gameIsStarted) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mButtonStart.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        mButtonStart.setText(R.string.cancelGame);
                        MainActivity.simonModel.setCurrentScore(0);
                        mTextViewCurrentScore.setText("Current Score: 0");

                        gameIsStarted = true;
                        gameIsCanceled = false;
                        closingInProgress = false;
                        ShowReadySetGo();
                        MainActivity.simonModel.StartNewGame();
                        showSequenceTimeDelay = MainActivity.simonModel.getDifficultyLevel()* timeDelayConst;
                        playerTimeOutDelay = MainActivity.simonModel.getDifficultyLevel() * playTimeOutDelayConst;
                        runnableTimeDelay = 1000;
                        randomColor = MainActivity.simonModel.GetNextRandomColor();
                        cancelTimer = false;
                        FlashSequenceToPlayer();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonStart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    }
                } else {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                mButtonStart.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                mButtonStart.setText(R.string.startGame);
                                generalHandler.removeCallbacksAndMessages(null);    // avoids detached fragment fatal errors
                                timeOutHandler.removeCallbacksAndMessages(null);
                                gameIsOver = true;
                                gameIsStarted = false;
                                gameIsCanceled = true;
                                closingInProgress = true;
                                mButtonStart.setText(R.string.cancelingGame);
                                gameResult = -1;
                                CloseGame();        // tell main activity player canceled game
                            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                mButtonStart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }
                return false;
            }
        });

    }

    private void CloseGame(){             // player canceled game
        generalHandler.removeCallbacksAndMessages(null);
        timeOutHandler.removeCallbacks(playerTimeOutRunnable, null);
        generalHandler.postDelayed(returnToMain, 1000);
    }

    private void ShowReadySetGo() {
        Toast.makeText(getContext(), "Read...Set...Go!", Toast.LENGTH_SHORT).show();
    }

    private void GameOver() {
            generalHandler.removeCallbacksAndMessages(null);    // avoid fatal errors should fragment be detached from activity canceling scheduled callbacks
            timeOutHandler.removeCallbacks(playerTimeOutRunnable, null);
            DisableColorButtons();
            mButtonStart.setEnabled(false);
            gameIsOver = true;
            gameIsStarted = false;
            gameIsCanceled = true;
            closingInProgress = true;
            gameResult=0;

        // this animation code is from https://stackoverflow.com/questions/4852281/android-how-can-i-make-a-button-flash
        final Animation animation = new AlphaAnimation(1,0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in


        switch (MainActivity.simonModel.getNextCorrectButton()) {  // determine what button should have been pressed
            case 0:
                mButtonRed.startAnimation(animation);
                break;
            case 1:
                mButtonGreen.startAnimation(animation);
                break;
            case 2:
                mButtonOrange.startAnimation(animation);
                break;
            case 3:
                mButtonBlue.startAnimation(animation);
                break;
        }
          Toast.makeText(getContext(), "GAME OVER! Wah Wah Wah", Toast.LENGTH_SHORT).show();
            generalHandler.postDelayed(returnToMain, 5000);
    }

    private Runnable returnToMain = new Runnable() {
        @Override
        public void run() {
            listener.gameResult(gameResult);
        }
    };

    private void DisableColorButtons(){
        mButtonRed.setEnabled(false);
        mButtonGreen.setEnabled(false);
        mButtonOrange.setEnabled(false);
        mButtonBlue.setEnabled(false);
    }
    private void EnableColorButtons(){
            mButtonRed.setEnabled(true);
            mButtonGreen.setEnabled(true);
            mButtonOrange.setEnabled(true);
            mButtonBlue.setEnabled(true);
    }

    private Runnable flashButtonLightRedRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonRed.setBackgroundColor(getResources().getColor(R.color.colorLightRed));
        }
    };

    private Runnable flashButtonDarkRedRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonRed.setBackgroundColor(getResources().getColor(R.color.colorDarkRed));
        }
    };

    private Runnable flashButtonLightGreenRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonGreen.setBackgroundColor(getResources().getColor(R.color.colorLightGreen));
        }
    };

    private Runnable flashButtonDarkGreenRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonGreen.setBackgroundColor(getResources().getColor(R.color.colorDarkGreen));
        }
    };
    private Runnable flashButtonLightOrangeRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonOrange.setBackgroundColor(getResources().getColor(R.color.colorLightOrange));
        }
    };

    private Runnable flashButtonDarkOrangeRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonOrange.setBackgroundColor(getResources().getColor(R.color.colorDarkOrange));
        }
    };
    private Runnable flashButtonLightBlueRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonBlue.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        }
    };

    private Runnable flashButtonDarkBlueRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonBlue.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
        }
    };

    private Runnable playerTimeOutRunnable = new Runnable() {
        @Override
        public void run() {

                gameIsOver = true;
                MainActivity.simonModel.checkForNewHighScore();
                Log.e("TAG", "You ran out of time!.");
                GameOver();
        }
    };

    private Runnable enableButtonsRunnable = new Runnable() {
        @Override
        public void run() {
            EnableColorButtons();
        }
    };

    private void FlashSequenceToPlayer() {
        nextColorButton = MainActivity.simonModel.GetComputerSequence();
        runnableTimeDelay = 1000;
        DisableColorButtons();
        while (nextColorButton > -1 && gameIsStarted && !closingInProgress)
        {
            switch (nextColorButton) {
                case 0:
                    generalHandler.postDelayed(flashButtonLightRedRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    generalHandler.postDelayed(flashButtonDarkRedRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    nextColorButton = MainActivity.simonModel.GetComputerSequence();
                    break;

                case 1:
                    generalHandler.postDelayed(flashButtonLightGreenRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    generalHandler.postDelayed(flashButtonDarkGreenRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    nextColorButton = MainActivity.simonModel.GetComputerSequence();
                    break;


                case 2:
                    generalHandler.postDelayed(flashButtonLightOrangeRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    generalHandler.postDelayed(flashButtonDarkOrangeRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    nextColorButton = MainActivity.simonModel.GetComputerSequence();
                    break;


                case 3:
                    generalHandler.postDelayed(flashButtonLightBlueRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    generalHandler.postDelayed(flashButtonDarkBlueRunnable, runnableTimeDelay);
                    runnableTimeDelay += showSequenceTimeDelay;
                    nextColorButton = MainActivity.simonModel.GetComputerSequence();
                    break;
            }
        }
        generalHandler.postDelayed(enableButtonsRunnable, runnableTimeDelay);
        runnableTimeDelay += playerTimeOutDelay;
        timeOutHandler.postDelayed(playerTimeOutRunnable, runnableTimeDelay);
    }

        private void ButtonPressed() {
        checkAnswer = MainActivity.simonModel.CheckPlayerResponse(whichButtonPressed);
        Log.e("TAG", "check answer " + Integer.toString(checkAnswer));
        switch (checkAnswer) {
            case 1: {                // right answer
//                cancelTimer = true;
                timeOutHandler.removeCallbacks(playerTimeOutRunnable, null);
                Log.e("TAG", "Correct answer.");
                timeOutHandler.postDelayed(playerTimeOutRunnable, playerTimeOutDelay);
                break;
            }

            case 0: {                //wrong answer
                Log.e("TAG", "Wrong answer.  Game over.");
                timeOutHandler.removeCallbacks(playerTimeOutRunnable, null);
                MainActivity.simonModel.checkForNewHighScore();
                GameOver();

                break;
            }

            case 2: {                //successful round
                Log.e("TAG", "Successful round.");
                MainActivity.simonModel.setCurrentScore(MainActivity.simonModel.getCurrentScore()+1);
//                currentScore++;
                mTextViewCurrentScore.setText(String.format("Current Score: %s", Integer.toString(MainActivity.simonModel.getCurrentScore())));
                if (MainActivity.simonModel.getCurrentScore() > MainActivity.simonModel.getHighScore()) {
                    mTextViewHighScore.setText(String.format("Highest Score: %s", Integer.toString(MainActivity.simonModel.getCurrentScore())));
                    MainActivity.simonModel.setHighScore(MainActivity.simonModel.getCurrentScore());
;
                }
//                randomColor = MainActivity.simonModel.GetNextRandomColor();
                Toast.makeText(getContext(), "Correct!  Next round...", Toast.LENGTH_SHORT).show();
                timeOutHandler.removeCallbacks(playerTimeOutRunnable, null);
                FlashSequenceToPlayer();
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameResultListener) {
            listener = (GameResultListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlayGameFragment.GameResultListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

