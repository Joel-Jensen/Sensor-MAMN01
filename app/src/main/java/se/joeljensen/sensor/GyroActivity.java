package se.joeljensen.sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class GyroActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private ImageView levelImage;
    private float lastX = 0;
    private float lastY = 0;
    private ToneGenerator tg;
    private int lastDistance = 0;
    private TextView pitchTextView, tiltTextView;
    private ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_gyro);

        levelImage = (ImageView) findViewById(R.id.level);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pitchTextView = (TextView) findViewById(R.id.pitch);
        tiltTextView = (TextView) findViewById(R.id.tilt);
        layout = (ConstraintLayout) findViewById(R.id.layout);


        tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
        tg.stopTone();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tg.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
    }

    // From https://stackoverflow.com/questions/11175599/how-to-measure-the-tilt-of-the-phone-in-xy-plane-using-accelerometer-in-android/15149421#15149421
    public void onSensorChanged(SensorEvent event) {
        //Get Rotation Vector Sensor Values
        double pitch, tilt, azimuth;
        double[] g = convertFloatsToDoubles(event.values.clone());

        //Normalise
        double norm = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2] + g[3] * g[3]);
        g[0] /= norm;
        g[1] /= norm;
        g[2] /= norm;
        g[3] /= norm;

        //Set values to commonly known quaternion letter representatives
        double x = g[0];
        double y = g[1];
        double z = g[2];
        double w = g[3];

        //Calculate Pitch in degrees (-180 to 180)
        double sinP = 2.0 * (w * x + y * z);
        double cosP = 1.0 - 2.0 * (x * x + y * y);
        pitch = Math.atan2(sinP, cosP) * (180 / Math.PI);

        //Calculate Tilt in degrees (-90 to 90)
        double sinT = 2.0 * (w * y - z * x);
        if (Math.abs(sinT) >= 1)
            tilt = Math.copySign(Math.PI / 2, sinT) * (180 / Math.PI);
        else
            tilt = Math.asin(sinT) * (180 / Math.PI);

        tiltTextView.setText(String.format("%.0f", tilt));
        pitchTextView.setText(String.format("%.0f", pitch));
        float newX = (float) (-pitch*20);
        float newY = (float) (-tilt*20);

        TranslateAnimation animation = new TranslateAnimation(lastY, newY, lastX, newX);
        animation.setDuration(210);
        animation.setFillAfter(true);

        levelImage.startAnimation(animation);

        lastX = newX;
        lastY = newY;

        int distance = (int) (Math.sqrt(Math.pow(newX, 2) + Math.pow(newY, 2)) / 5);
        if(distance == 0 && lastDistance != 0) {
            tg.stopTone();
            tg.startTone(24, 10000);
            levelImage.setImageResource(R.drawable.level_black);
        }
        if(distance != 0 && lastDistance == 0) {
            levelImage.setImageResource(R.drawable.level);
        }
        lastDistance = distance;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
            return null;

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++)
            output[i] = input[i];

        return output;
    }
}