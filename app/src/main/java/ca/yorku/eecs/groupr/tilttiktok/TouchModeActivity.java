package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

public class TouchModeActivity extends Activity {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    private SensorManager sm;
    private Sensor gyro;
    private TiltDirectionListener tdl;

    private ExperimentSetup setup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchui);

        // get experiment setup
        setup = getIntent().getExtras().getParcelable("setup");

        // lock rotate
        if (this.getDefaultDeviceOrientation() == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (setup.controlMethod == ControlMethod.TILT) {
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyro == null) {
                Toast.makeText(this, "Sensor error, the experiment cannot be performed in tilt mode.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            tdl = new TiltDirectionListener(0.6f, 0.25f, new TiltDirectionListener.ITiltDirectionCallback() {
                @Override
                public void onTiltDirection(int direction) {
                    StringBuilder sb = new StringBuilder();
                    if ((direction & TiltDirectionListener.UP) != 0) sb.append("UP ");
                    if ((direction & TiltDirectionListener.DOWN) != 0) sb.append("DOWN ");
                    if ((direction & TiltDirectionListener.LEFT) != 0) sb.append("LEFT ");
                    if ((direction & TiltDirectionListener.RIGHT) != 0) sb.append("RIGHT ");
                    Log.i(MYDEBUG, sb.toString());
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
        if (setup.controlMethod == ControlMethod.TILT) {
            sm.registerListener(tdl, gyro, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (setup.controlMethod == ControlMethod.TILT) {
            sm.unregisterListener(tdl);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }
}