package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private ExperimentSetup setup;
    private ExperimentCoordinator coord;

    private SoundEffect se;

    // Don't auto scroll to next if the last action was NEXT or PREVIOUS
    private boolean coordLastShouldNotScroll = false;
    // Don't send scroll events if the last scroll was caused by the coordinator
    private boolean coordIgnoreLastScroll = false;

    private ViewPager2 vpContent;
    private Button btnStart;
    private ImageButton btnLike, btnComment, btnShare;
    private boolean isLiked = false, isCommented = false, isShared = false;
    TextView lblDisplay;

    private ViewPager2.OnPageChangeCallback onPageChange = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // do not send scroll events if the last scroll was caused by the coordinator
            if (!coordIgnoreLastScroll) {
                if (position > currentPage) {
                    coord.performAction(ExperimentAction.NEXT);
                } else if (position < currentPage) {
                    coord.performAction(ExperimentAction.PREVIOUS);
                }
            }
            currentPage = position;
            coordIgnoreLastScroll = false;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            resetButtonStates();
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

    ExperimentCoordinatorCallback callback = new ExperimentCoordinatorCallback() {
        @Override
        public void onExperimentStart(int currentTrial, ExperimentAction action) {
            lblDisplay.setText(action.toString());
            coordLastShouldNotScroll = action == ExperimentAction.NEXT || action == ExperimentAction.PREVIOUS;
        }

        @Override
        public void onNewTrial(int trial, ExperimentAction action) {
            se.correct();
            coordIgnoreLastScroll = true;
            if(!coordLastShouldNotScroll)
                scrollNext();
            lblDisplay.setText(action.toString());
            coordLastShouldNotScroll = action == ExperimentAction.NEXT || action == ExperimentAction.PREVIOUS;
        }

        @Override
        public void onIncorrectAction(int trial, ExperimentAction action) {
            se.incorrect();
        }

        @Override
        public void onExperimentFinished(ExperimentResult result) {
            se.correct();

            // transition to the finish activity
            Intent intent = new Intent(TouchModeActivity.this, ExperimentResultActivity.class);
            intent.putExtra("result", result);
            startActivity(intent);

            // close self
            TouchModeActivity.super.onDestroy();
            finish();
        }
    };

    public void scrollNext() {
        resetButtonStates();
        vpContent.setCurrentItem(currentPage + 1, true);
    }

    public void scrollPrev() {
        resetButtonStates();
        vpContent.setCurrentItem(currentPage - 1, true);
    }

    public void resetButtonStates() {
        isLiked = false;
        btnLike.setImageResource(R.drawable.heart);

        isCommented = false;
        btnComment.setImageResource(R.drawable.message);

        isShared = false;
        btnShare.setImageResource(R.drawable.arrowblank);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchui);

        // Lock rotate
        if (this.getDefaultDeviceOrientation() == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize controls
        lblDisplay = findViewById(R.id.lblDisplay);
        vpContent = findViewById(R.id.vpContent);
        btnStart = findViewById(R.id.btnStart);
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnShare = findViewById(R.id.btnShare);

        // Retrieve the passed ExperimentSetup object
        setup = getIntent().getParcelableExtra("setup");

        // Initialize the ExperimentCoordinator
        coord = new ExperimentCoordinator(setup, callback);
        se = new SoundEffect(this);

        // Attach the events for the buttons
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);
                lblDisplay.setVisibility(View.VISIBLE);
                coord.startExperiment();
            }
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coord.performAction(ExperimentAction.LIKE);

                // Toggle the state and change the button image based on the new state
                isLiked = !isLiked;
                btnLike.setImageResource(isLiked ? R.drawable.heartfill : R.drawable.heart);
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coord.performAction(ExperimentAction.COMMENT);

                // Toggle the state and change the button image based on the new state
                isCommented = !isCommented;
                btnComment.setImageResource(isCommented ? R.drawable.messagefill : R.drawable.message);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coord.performAction(ExperimentAction.SHARE);
                // Toggle the state and change the button image based on the new state
                isShared = !isShared;
                btnShare.setImageResource(isShared ? R.drawable.arrowfill : R.drawable.arrowblank);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            se.finalize();
        } catch (Throwable e) {
        }
    }
}