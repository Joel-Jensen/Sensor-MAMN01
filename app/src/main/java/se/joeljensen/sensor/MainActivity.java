package se.joeljensen.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_main);
    }

    public void launchCompass(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void launchGyro(View view) {
        Intent intent = new Intent(this, GyroActivity.class);
        startActivity(intent);
    }
}