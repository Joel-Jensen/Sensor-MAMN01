package se.joeljensen.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager SensorManage;
    private ImageView compassImage;
    private float degreeStart = 0f;
    private boolean headingNorth = false;
    private boolean degreeOutsideVibrationRange = true;
    TextView degreeTV;
    Vibrator vibrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compassImage);
        degreeTV = (TextView) findViewById(R.id.degreeTV);
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

        vibrationService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        degreeTV.setText(String.format("%.0f", degree));
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                degreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassImage.startAnimation(ra);
        if((degree > 345 || degree < 15) && !headingNorth) {
            compassImage.setImageResource(R.drawable.compass_red);
            headingNorth = true;
        }
        if(degree < 345 && degree > 15 && headingNorth) {
            compassImage.setImageResource(R.drawable.compass_white);
            headingNorth = false;
            degreeOutsideVibrationRange = true;
        }
        if((degree > 358 || degree < 2) && degreeOutsideVibrationRange) {
            degreeOutsideVibrationRange = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrationService.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrationService.vibrate(500);
            }
        }
        degreeStart = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}