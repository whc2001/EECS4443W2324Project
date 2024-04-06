package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ExperimentResultActivity extends Activity {

    private TextView lblAverageTime, lblIncorrectActions, lblAverageAccuracy;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        // Initialize controls
        lblAverageTime = findViewById(R.id.lblAverageTime);
        lblIncorrectActions = findViewById(R.id.lblIncorrectActions);
        lblAverageAccuracy = findViewById(R.id.lblAverageAccuracy);

        // Get the result from the intent
        ExperimentResult result = getIntent().getParcelableExtra("result");
        int trials = result.getSetup().getTrials();
        long[] durations = result.getDurationEachTrial();
        int[] incorrects = result.getIncorrectActionEachTrial();

        // Calculate the average time
        long average = 0;
        for(int i = 0; i < trials; ++i)
            average += durations[i];
        average /= trials;
        lblAverageTime.setText(average + " ms");

        // Calculate the number of incorrect actions
        int incorrect = 0;
        for(int i = 0; i < trials; ++i)
            incorrect += incorrects[i];
        lblIncorrectActions.setText(incorrect + " times");

        // Calculate the average accuracy
        int totalActions = 0;
        for(int i = 0; i < trials; ++i)
            totalActions += incorrects[i] + 1;
        double accuracy = (double)(totalActions - incorrect) / totalActions * 100;
        lblAverageAccuracy.setText(String.format("%.2f", accuracy) + " %");

        // Save the result
        ExperimentResultExporter.write(result);
    }

    public void btnBack_Click(View view)
    {
        // Go back to the main activity
        finish();
    }

    public void btnExit_Click(View view)
    {
        // Tell the main activity to exit
        Intent intent = new Intent(this, ExperimentSetupActivity.class);
        intent.putExtra("exit", true);
        startActivity(intent);

        // Close this activity
        super.onDestroy();
        finish();
    }
}
