package com.example.ip520.braintrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView timeLeftLabel;
    TextView question;
    TextView score;
    TextView[] option = new TextView[4];
    TextView result;
    Button playAgain;
    int timeLeft = 30;
    int question1;
    int question2;
    int answerOption;
    int[] optionValues = new int[4];
    int totalGamesPlayed = 0;
    int correct = 0;
    Random random = new Random();
    TextView go;
    SharedPreferences sharedPreferences;
    String highScoreKey = "highScore";

    private void createQuestion() {
        HashSet<Integer> set = new HashSet<>();
        question1 = random.nextInt(50);
        question2 = random.nextInt(50);
        while (set.size() < 4)
            set.add(random.nextInt(99));
        int i = 0;
        for (Integer val : set) {
            optionValues[i] = val;
            i++;
        }
        if (set.contains(question1 + question2))
            answerOption = Arrays
                    .asList(optionValues)
                    .indexOf(question1 + question2);
        else {
            answerOption = random.nextInt(4);
            optionValues[answerOption] = question2 + question1;
        }
        for (int j = 0; j < 4; j++)
            option[j].setText(optionValues[j] + "");
        question.setText(question1 + " + " + question2);
    }

    public void checkCorrect(View view) {
        totalGamesPlayed++;
        int id = view.getId();
        String ourId = view
                .getResources()
                .getResourceEntryName(id);
        char optionChosen = ourId.charAt(6);
        if (Character.getNumericValue(optionChosen) == answerOption) {
            correct++;
            result.setText("Correct");
        } else
            result.setText("Wrong");
        setScore();
        createQuestion();
    }

    private void setScore() {
        score.setText(correct + "/" + totalGamesPlayed);
    }

    private void gameStarted() {
        timeLeftLabel.setVisibility(View.VISIBLE);
        question.setVisibility(View.VISIBLE);
        for (int i = 0; i < 4; i++) {
            option[i].setVisibility(View.VISIBLE);
            option[i].setClickable(true);
        }
        result.setText("");
        setScore();
        score.setVisibility(View.VISIBLE);
        go.setVisibility(View.INVISIBLE);
        totalGamesPlayed = 0;
        correct = 0;
        result.setAllCaps(false);
        result.setTextSize(30f);
        result.setTextColor(getResources()
                .getColor(R.color.BLACK));
        createQuestion();
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft--;
                timeLeftLabel.setText(timeLeft + "s");
            }

            @Override
            public void onFinish() {
                timeLeftLabel.setText("0s");
                playAgain.setVisibility(View.VISIBLE);
                playAgain.setClickable(true);
                for (int i = 0; i < 4; i++) {
                    option[i].setClickable(false);
                    option[i].setText("Time Up");
                }
                question.setText("Time Up");
                result.setText("You have answered " + correct + " out of " + totalGamesPlayed);
                if (correct == totalGamesPlayed) {
                    int highScore = sharedPreferences.getInt(highScoreKey, 0);
                    Log.i("High Score is", String.format("%d", highScore));
                    if (highScore < correct) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(highScoreKey, correct);
                        editor.commit();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeLeftLabel = (TextView) findViewById(R.id.timeLeft);
        question = (TextView) findViewById(R.id.question);
        score = (TextView) findViewById(R.id.score);
        Resources r = getResources();
        String name = getPackageName();
        for (int i = 0; i < 4; i++)
            option[i] = (TextView) findViewById(r.getIdentifier("option" + i,
                    "id", name));
        result = (TextView) findViewById(R.id.result);
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        int highScore = sharedPreferences.getInt(highScoreKey, 0);
        result.setText(String.format("High Score: %d", highScore));
        result.setAllCaps(true);
        result.setTextSize(40f);
        result.setTextColor(getResources()
                .getColor(R.color.RED));
        playAgain = (Button) findViewById(R.id.playAgain);
        go = (TextView) findViewById(R.id.go);
        go.setOnClickListener((View view) -> gameStarted());
        playAgain.setOnClickListener((View view) -> {
            go.setVisibility(View.VISIBLE);
            timeLeftLabel.setVisibility(View.INVISIBLE);
            question.setVisibility(View.INVISIBLE);
            for (int i = 0; i < 4; i++)
                option[i].setVisibility(View.INVISIBLE);
            score.setVisibility(View.INVISIBLE);
            int highestScore = sharedPreferences.getInt(highScoreKey, 0);
            result.setText(String.format("High Score: %d", highestScore));
            playAgain.setVisibility(View.INVISIBLE);
            playAgain.setClickable(false);
            result.setAllCaps(true);
            result.setTextSize(40f);
            result.setTextColor(getResources()
                    .getColor(R.color.RED));
            go.setClickable(true);
            timeLeft = 30;
        });
    }
}
