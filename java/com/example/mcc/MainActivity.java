package com.example.mcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{

    LocationManager locationManager;
    LocationListener locationListener;
    FirebaseDatabase rootNode;
    DatabaseReference reference;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    EditText password;
    EditText Username;
    EditText email;
    Button register;
    Button alreadyRegister;
    String USERNAME, EMAIL, PASSWORD;
    FirebaseAuth fAuth;
    Intent intent;

    public void signout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void chat_bot(View view) {
        Intent botIntent = new Intent(getApplicationContext(), ChatBot.class);
        startActivity(botIntent);
    }

    public void Register(View view) {

        USERNAME = Username.getText().toString().trim();
        PASSWORD = password.getText().toString().trim();
        EMAIL = email.getText().toString().trim();

        rootNode = FirebaseDatabase.getInstance();

        if (register.getText().toString().matches("Register")) {

            if (Username.getText().toString().matches("") || password.getText().toString().matches("") || email.getText().toString().matches("")) {
                Toast.makeText(this, "Something is Missing!!!", Toast.LENGTH_SHORT).show();
            } else if (PASSWORD.length() < 6) {
                password.setError("Password must be greater than 6 characters");
            }else {

                reference = rootNode.getReference("Users");

                UserHelperClass user = new UserHelperClass(EMAIL,USERNAME,PASSWORD);

                reference.child(USERNAME).setValue(user);

                fAuth.createUserWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),playZone.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else{
            if (password.getText().toString().matches("") || email.getText().toString().matches("")) {
                Toast.makeText(this, "Something is Missing!!!", Toast.LENGTH_SHORT).show();
            } else if (PASSWORD.length() < 6) {
                password.setError("Password must be greater than 6 characters");
            }

            fAuth.signInWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(MainActivity.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),playZone.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void alreadyRegistered(View view) {
        Username.setVisibility(View.INVISIBLE);
        register.setText("Login");
        password.setText("");
        email.setText("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Username = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        fAuth = FirebaseAuth.getInstance();
        intent = new Intent(getApplicationContext(),playZone.class);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                //Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }

        register = (Button) findViewById(R.id.registerButton);
        alreadyRegister = (Button) findViewById(R.id.loginButton);

    }
}