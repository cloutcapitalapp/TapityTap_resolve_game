package com.resolvethecompany.tapitytap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog
        .MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    userIDGenerator_Class userIDGen_Class;
    int gameScore, buttonTapCount;
    Button gameLogic_Button, gameStart_Button, scoreTab_Button, coinStore_Button;
    TextView scoreCounter, scoreBonus, timerCounter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef =
            database.getReference("UsersID");
    public void allVars(){
        mediaPlayer =
                MediaPlayer.create(getApplicationContext(),
                        R.raw.backgroundmusic);
        gameScore = 100000;
        buttonTapCount = 0;
        userIDGen_Class = new userIDGenerator_Class();
        scoreBonus = findViewById(R.id.ScoreBonus_TextView);
        gameStart_Button = findViewById(R.id.gameStart_Button);
        gameLogic_Button = findViewById(R.id.gameStart_Button);
        timerCounter = findViewById(R.id.timer_TextView);
        scoreCounter = findViewById(R.id.gameCount_TextView);
        scoreTab_Button = findViewById(R.id.scoreTabButton);
        coinStore_Button = findViewById(R.id.coinStore_Button);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Take instance of Action Bar
        // using getSupportActionBar and
        // if it is not Null
        // then call hide function
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Collect all vars
        allVars();

        //handle creating and storing uID
        handleUID();
    }
    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences uID =
                this.getSharedPreferences("uID", 0);

        myRef.child(String
                .valueOf(uID.getInt("key", 0)))
                .child("isOnline")
                .setValue("online");

        //On button press, start game logic.
        gameStart_Button.setOnClickListener(v -> {
            allVars();
            gameLogic_Button.setText(R.string.on_start_tap);
            mediaPlayer.start();
            gameTimer();
        });

        scoreTab_Button.setOnClickListener(v -> {
            Intent goToScoreTab = new Intent(
                    MainActivity.this,
                    myScoreActivity.class);

            startActivity(goToScoreTab);
        });

        coinStore_Button.setOnClickListener(v -> {
            notReady_Alert();
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onStop(){
        super.onStop();
        allVars();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences uID =
                this.getSharedPreferences("uID", 0);
        myRef.child(String
                .valueOf(uID.getInt("key", 0)))
                .child("isOnline")
                .setValue("Offline");
    }
    private void handleUID(){
        SharedPreferences uID =
                this.getSharedPreferences("uID",
                        MODE_PRIVATE);

        if(uID.contains("key")){
            //we're good
            Log.d("uID_nonNull_Check",
                    "uID found" + " "
                            + uID.getInt("key",0));
        }else{
            final int min = 111111111;
            final int max = 999999999;
            final int random_key =
                    new Random().nextInt((max - min) + 1)
                            + min;

            SharedPreferences.Editor myEdit = uID.edit();

            myEdit.putInt("key", random_key);
            myEdit.apply();

            Log.d("uID_apply_check", String
                    .valueOf(random_key));
        }
    }
    private void gameLogic(){
        allVars();
        gameStart_Button.setOnClickListener(v -> {
            gameScore = gameScore - 10;
            buttonTapCount = buttonTapCount + 1;
            if(buttonTapCount%5==0){
                gameScore = gameScore - 100;
                scoreBonus.setText(R.string.bonus_100);
                bonusTimer();
            }
            if(buttonTapCount%20==0){
                gameScore = gameScore - 500;
                scoreBonus.setText(R.string.bonus_500);
                bonusTimer();
            }
            if(buttonTapCount%100==0){
                gameScore = gameScore - 1000;
                scoreBonus.setText(R.string.bonus_1000);
                bonusTimer();
            }

            scoreCounter.setText(String.valueOf(gameScore));
            Log.d("Score_Test", "" + gameScore + " "
                    + buttonTapCount);
        });
    }
    private void gameTimer(){
        new CountDownTimer(15000,
                1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                timerCounter.setText(""
                        + millisUntilFinished / 1000);
                //here you can have your logic to
                //set text to edittext
                gameLogic();
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                timerCounter.setText("OVER");
                gameStart_Button.setText(R.string.restart);
                gameLogic_Button
                        .setOnClickListener(v -> gameEndAlert());
            }

        }.start();
    }
    private void bonusTimer(){
        new CountDownTimer(2000,
                1000) {

            public void onTick(long millisUntilFinished) {
                //timerCounter.setText(""
                // + millisUntilFinished / 1000);
            }

            public void onFinish() {
                scoreBonus
                        .setText(R.string.bonus);
            }

        }.start();
    }
    private void gameEndAlert(){
        allVars();

        final AlertDialog confirm = new
                MaterialAlertDialogBuilder
                (MainActivity.this).create();
        LinearLayout layout =
                new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        confirm.setView(layout);

        confirm.setCancelable(false);
        confirm.setCanceledOnTouchOutside(false);

        Window window = confirm.getWindow();
        window.setGravity(Gravity.BOTTOM);

        window.setLayout(ViewGroup
                .LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        confirm.setTitle("WOOOO!");
        confirm.setMessage("You made it! We'll save " +
                "your score " +
                "for you in the score tab.");

        MaterialButton button =
                new MaterialButton(MainActivity.this);
        button.setText(R.string.confirm_string);
        button.setBackgroundResource(R.color.purple_200);
        layout.addView(button);
        button.setOnClickListener(v -> {
            //points need to be added to the
            //database according to uID

            int endingScore = gameScore -
                    Integer.parseInt
                            (String.valueOf(scoreCounter
                                    .getText()));

            Log.d("ending_test", "" + endingScore);

            SharedPreferences uID =
                    this.getSharedPreferences("uID",
                            0);

            Log.d("Score_Send_Check", "" +
                    myRef.child
                            (String.valueOf
                                    (uID.getInt("key",
                                            0)))
                            .push()
                            .setValue
                                    (String.valueOf
                                            (endingScore)));
            confirm.dismiss();

            mediaPlayer.stop();
            mediaPlayer.release();

            finish();
            startActivity(getIntent());
        });
        confirm.show();
    }
    private void notReady_Alert(){
        allVars();

        final AlertDialog confirm = new
                MaterialAlertDialogBuilder
                (MainActivity.this).create();
        LinearLayout layout =
                new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        confirm.setView(layout);

        confirm.setCancelable(false);
        confirm.setCanceledOnTouchOutside(false);

        Window window = confirm.getWindow();
        window.setGravity(Gravity.BOTTOM);

        window.setLayout(ViewGroup
                        .LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        confirm.setTitle("Coin Store");
        confirm.setMessage("Store isn't ready just yet, but we're saving your score.");

        MaterialButton button =
                new MaterialButton(MainActivity.this);
        button.setText(R.string.confirm_string);
        button.setBackgroundResource(R.color.purple_200);
        layout.addView(button);
        button.setOnClickListener(v -> {

            confirm.dismiss();

            mediaPlayer.stop();
            mediaPlayer.release();

            finish();
            startActivity(getIntent());
        });
        confirm.show();
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }
}