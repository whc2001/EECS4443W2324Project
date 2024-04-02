package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class TouchModeActivity extends Activity {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int CONTENT_COLOR_RED = Color.argb(255, 204, 102, 102);
    final static int CONTENT_COLOR_GREEN = Color.argb(255, 102, 204, 102);
    final static int CONTENT_COLOR_BLUE = Color.argb(255, 102, 102, 204);
    final int[] pageColors = {CONTENT_COLOR_BLUE, CONTENT_COLOR_RED, CONTENT_COLOR_GREEN, CONTENT_COLOR_BLUE, CONTENT_COLOR_RED};   // Colors for each page, the first and the last is for the infinite scrolling effect

    private int currentPage = 1;    // Start from the second page, which is the first real page

    private SensorManager sm;
    private Sensor gyro;
    private TiltDirectionListener tdl;

    private ExperimentSetup setup;

    private ViewPager2 vpContent;
    private ImageButton likeButton, commentButton, shareButton;

    private ViewPager2.OnPageChangeCallback onPageChange = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (position > currentPage) {
                Log.i(MYDEBUG, ">>>NEXT>>>");
            } else if (position < currentPage) {
                Log.i(MYDEBUG, "<<<PREV<<<");
            }
            currentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                // If the user scrolls to the first page, teleport silently to the second last page for infinite scrolling effect
                // When teleporting temporarily unregister the callback to avoid multiple scroll events
                if (currentPage == 0) {
                    vpContent.unregisterOnPageChangeCallback(this);
                    vpContent.setCurrentItem(pageColors.length - 2, false);
                    vpContent.registerOnPageChangeCallback(this);
                    currentPage = pageColors.length - 2;
                }
                // Same goes on the other side
                else if (currentPage == pageColors.length - 1) {
                    vpContent.unregisterOnPageChangeCallback(this);
                    vpContent.setCurrentItem(1, false);
                    vpContent.registerOnPageChangeCallback(this);
                    currentPage = 1;
                }
            }
        }
    };

    public void scrollNext() {
        vpContent.setCurrentItem(currentPage + 1, true);
    }

    public void scrollPrev() {
        vpContent.setCurrentItem(currentPage - 1, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchui);

        vpContent = findViewById(R.id.content_view);
        likeButton = findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollPrev();
            }
        });
        commentButton = findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollNext();
            }
        });

        // Setup the ViewPager2 with infinite scrolling effect
        vpContent.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = new View(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return new RecyclerView.ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setBackgroundColor(pageColors[position]);
            }

            @Override
            public int getItemCount() {
                return pageColors.length;
            }
        });
        vpContent.setCurrentItem(currentPage, false);
        vpContent.registerOnPageChangeCallback(onPageChange);


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

    @Override
    public void onStop() {
        super.onStop();
    }
}