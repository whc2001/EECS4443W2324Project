package ca.yorku.eecs.groupr.tilttiktok;

// Class for categorizing the tilt direction from SensorEvent

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class TiltDirectionListener implements SensorEventListener {
    // LPF coefficient, the larger the more influence the new value has
    private static final float ALPHA = 0.4f;

    // Threshold for a tilt to be registered
    private static final float THRESHOLD = 1.15f;

    // Time window for sensor data to be collected
    private static final int SENSOR_TIME_WINDOW = 250;

    // Interval between two reports
    private static final int REPORT_COOLDOWN_TIME = 750;

    public interface ITiltDirectionCallback {
        void onTiltDirection(TiltDirection direction);
    }

    private ITiltDirectionCallback callback;

    private float[] lastRotationRate = new float[3];
    private float[] maxDeltaRotationRate = new float[3];
    private long lastSensorTime = 0;
    private long lastReportTime = 0;

    private float lowPass(float current, float last) {
        return last * (1.0f - ALPHA) + current * ALPHA;
    }

    public TiltDirectionListener(ITiltDirectionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationRate = event.values;

        // Apply low-pass filter
        lastRotationRate[0] = lowPass(rotationRate[0], lastRotationRate[0]);
        lastRotationRate[1] = lowPass(rotationRate[1], lastRotationRate[1]);
        lastRotationRate[2] = lowPass(rotationRate[2], lastRotationRate[2]);

        // Calculate the change in rotation rate
        float deltaX = Math.abs(lastRotationRate[0] - rotationRate[0]);
        float deltaY = Math.abs(lastRotationRate[1] - rotationRate[1]);
        float deltaZ = Math.abs(lastRotationRate[2] - rotationRate[2]);

        // Update the maximum changes in rotation rate
        maxDeltaRotationRate[0] = Math.max(maxDeltaRotationRate[0], deltaX);
        maxDeltaRotationRate[1] = Math.max(maxDeltaRotationRate[1], deltaY);
        maxDeltaRotationRate[2] = Math.max(maxDeltaRotationRate[2], deltaZ);

        // Check if the tilt is significant
        if (maxDeltaRotationRate[0] > THRESHOLD || maxDeltaRotationRate[1] > THRESHOLD) {
            TiltDirection direction;

            // Determine the direction with the most tilt
            if (maxDeltaRotationRate[0] > maxDeltaRotationRate[1]) {
                if (lastRotationRate[0] > 0) {
                    direction = TiltDirection.DOWN;
                } else {
                    direction = TiltDirection.UP;
                }
            } else {
                if (lastRotationRate[1] > 0) {
                    direction = TiltDirection.RIGHT;
                } else {
                    direction = TiltDirection.LEFT;
                }
            }

            // Check if it's enough time since the last report and if there is a tilt
            if (event.timestamp - lastReportTime > REPORT_COOLDOWN_TIME * 1000000) {
                lastReportTime = event.timestamp;
                if(direction != TiltDirection.NONE)
                        callback.onTiltDirection(direction);
            }
        }

        // Clear the old sensor data when the time window is exceeded
        if(event.timestamp - lastSensorTime > SENSOR_TIME_WINDOW * 1000000) {
            maxDeltaRotationRate[0] = 0;
            maxDeltaRotationRate[1] = 0;
            maxDeltaRotationRate[2] = 0;
            lastSensorTime = event.timestamp;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
