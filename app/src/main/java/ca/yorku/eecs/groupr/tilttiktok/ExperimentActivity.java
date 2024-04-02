package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

public class ExperimentActivity extends Activity {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    private SensorManager sm;
    private Sensor gyro;
    private TiltDirectionListener tdl;

    private ExperimentSetup setup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get experiment setup
        setup = getIntent().getExtras().getParcelable("setup");

        // lock rotate
        if (this.getDefaultDeviceOrientation() == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (setup.getControlMethod() == ControlMethod.TILT) {
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyro == null) {
                Toast.makeText(this, "Sensor error, the experiment cannot be performed in tilt mode.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            tdl = new TiltDirectionListener(new TiltDirectionListener.ITiltDirectionCallback() {
                @Override
                public void onTiltDirection(TiltDirection direction) {
                    Log.i(MYDEBUG, direction.toString());
                }
            });
        }
    }

    /*
     * Get the default orientation of the device. This is needed to correctly map the sensor data
     * for pitch and roll (see onSensorChanged). See...
     *
     * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on-
     * android-i-e-get-landscape
     */
    public int getDefaultDeviceOrientation() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation == Configuration.ORIENTATION_LANDSCAPE) || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation == Configuration.ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else return Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (setup.getControlMethod() == ControlMethod.TILT) {
            sm.registerListener(tdl, gyro, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (setup.getControlMethod() == ControlMethod.TILT) {
            sm.unregisterListener(tdl);
        }
    }

    /*
     * Cancel the timer when the activity is stopped. If we don't do this, the timer continues after
     * the activity finishes. See...
     *
     * http://stackoverflow.com/questions/15144232/countdowntimer-continues-to-tick-in-background-how
     * -do-i-retrieve-that-count-in
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Screen updates are initiated in onFinish which executes every REFRESH_INTERVAL milliseconds
     */
    private class ScreenRefreshTimer extends CountDownTimer {
        ScreenRefreshTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            /*
            float tiltMagnitude = (float) Math.sqrt(pitch * pitch + roll * roll);
            float tiltAngle = tiltMagnitude == 0f ? 0f : (float) Math.asin(roll / tiltMagnitude) * RADIANS_TO_DEGREES;

            if (pitch > 0 && roll > 0)
                tiltAngle = 360f - tiltAngle;
            else if (pitch > 0 && roll < 0)
                tiltAngle = -tiltAngle;
            else if (pitch < 0 && roll > 0)
                tiltAngle = tiltAngle + 180f;
            else if (pitch < 0 && roll < 0)
                tiltAngle = tiltAngle + 180f;

            rb.updateBallPosition(pitch, roll, tiltAngle, tiltMagnitude); // will invalidate ball panel
            this.start();
             */
        }
    }
}