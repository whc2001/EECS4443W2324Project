package ca.yorku.eecs.groupr.tilttiktok;

// Class for categorizing the tilt direction from SensorEvent

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

public class TiltDirectionListener implements SensorEventListener {
    public interface ITiltDirectionCallback {
        void onTiltDirection(int direction);
    }

    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;

    private float threshold, alpha;
    private ITiltDirectionCallback callback;
    private int lastStatus = 0;

    private static final double NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 0.000001f;
    private final float[] deltaRotationVector = new float[4];
    private float[] rotationCurrent = {1, 0, 0, 0, 1, 0, 0, 0, 1};
    private double lastTimestamp;

    private float[] lowPass(float[] input, float[] output, float alpha) {
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + alpha * (input[i] - output[i]);
        return output;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    public TiltDirectionListener(float threshold, float alpha, ITiltDirectionCallback callback) {
        this.threshold = threshold;
        this.alpha = alpha;
        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Not working for now
        /*
        if (lastTimestamp != 0) {
            final double dT = (event.timestamp - lastTimestamp) * NS2S;
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            double thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            rotationCurrent = matrixMultiplication(rotationCurrent, deltaRotationMatrix);
        }
        lastTimestamp = event.timestamp;

        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationCurrent, orientation);

        Log.d("", Arrays.toString(orientation));

        float pitch = orientation[1];
        float roll = orientation[2];

        if (Math.abs(pitch) > Math.abs(roll)) {
            if (pitch > 0) {
                // The device is tilted down
            } else {
                // The device is tilted up
            }
        } else {
            if (roll > 0) {
                // The device is tilted to the left
            } else {
                // The device is tilted to the right
            }
        }
        */
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
