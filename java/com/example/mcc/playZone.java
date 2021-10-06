package com.example.mcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class playZone extends AppCompatActivity {

    Button startButton;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button buttonSignout;
    TextView sumText;
    int location;
    TextView resultText;
    int score = 0;
    int noOfQn = 0;
    TextView scoreText, Location;
    TextView timer;
    Button playAgainBt;
    ConstraintLayout gameLayout;
    FusedLocationProviderClient fusedLocationProviderClient;

    public void playAgain(View view) {

        score = 0;
        noOfQn = 0;
        timer.setText("30s");
        scoreText.setText(Integer.toString(score) + "/" + Integer.toString(noOfQn));
        resultText.setText("");

        playAgainBt.setVisibility(View.INVISIBLE);

        newQn();

        new CountDownTimer(30100, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                timer.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {

                resultText.setText("Done!");
                playAgainBt.setVisibility(View.VISIBLE);

            }
        }.start();
    }

    ArrayList<Integer> answers = new ArrayList<>();

    public void showAnswer(View view) {

        if (Integer.toString(location).equals(view.getTag().toString())) {
            resultText.setText("Correct!!");
            score++;
        } else {
            resultText.setText("Wrong :(");
        }
        noOfQn++;
        scoreText.setText(Integer.toString(score) + "/" + Integer.toString(noOfQn));
        newQn();

    }

    public void start(View view) {

        startButton.setVisibility(View.INVISIBLE);
        gameLayout.setVisibility(View.VISIBLE);
        playAgain(findViewById(R.id.timerTextView));
    }

    public void newQn() {

        Random rand = new Random();

        int a = rand.nextInt(21);
        int b = rand.nextInt(21);

        sumText.setText(Integer.toString(a) + " + " + Integer.toString(b));

        location = rand.nextInt(4);
        answers.clear();

        for (int i = 0; i < 4; i++) {
            if (i == location) {
                answers.add(a + b);
            } else {
                int wrongAnswer = rand.nextInt(41);

                while (wrongAnswer == a + b)
                    wrongAnswer = rand.nextInt(41);

                answers.add(wrongAnswer);
            }
        }

        button0.setText(Integer.toString(answers.get(0)));
        button1.setText(Integer.toString(answers.get(1)));
        button2.setText(Integer.toString(answers.get(2)));
        button3.setText(Integer.toString(answers.get(3)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_zone);

        Intent intent = getIntent();

        startButton = (Button) findViewById(R.id.startButton);
        sumText = (TextView) findViewById(R.id.sumTextView);
        resultText = (TextView) findViewById(R.id.resultTextView);
        scoreText = (TextView) findViewById(R.id.scoreTextView);
        gameLayout = findViewById(R.id.gameLayout);
        timer = findViewById(R.id.timerTextView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        playAgainBt = findViewById(R.id.platAgainButton);
        playAgainBt.setVisibility(View.INVISIBLE);
        buttonSignout = findViewById(R.id.buttonSignout);
        Location = findViewById(R.id.locationTextView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //initialize geocoder
                        Geocoder geocoder = new Geocoder(playZone.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        Location.setText("Player from: "+addresses.get(0).getLocality());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        gameLayout.setVisibility(View.INVISIBLE);
    }
}