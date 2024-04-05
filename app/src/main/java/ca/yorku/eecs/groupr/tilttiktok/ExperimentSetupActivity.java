package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ExperimentSetupActivity extends Activity
{
	final static String[] SUBJECT_ID = {"01", "02", "03", "04", "05", "06", "07", "08"};
	final static String[] CONTROL_METHOD = {ControlMethod.TOUCH.toString(), ControlMethod.TILT.toString()};
	final static String[] TRIALS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

	Spinner cmbSubjectID, cmbControlMethod, cmbTrials;

	TextView lblCurrentAction;

	ExperimentCoordinator coord;

	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		cmbSubjectID = (Spinner) findViewById(R.id.cmbSubjectID);
		cmbSubjectID.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, SUBJECT_ID));
		cmbSubjectID.setSelection(0);

		cmbControlMethod = (Spinner) findViewById(R.id.cmbControlMethod);
		cmbControlMethod.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, CONTROL_METHOD));
		cmbControlMethod.setSelection(0);

		cmbTrials = (Spinner) findViewById(R.id.cmbTrials);
		cmbTrials.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, TRIALS));
		cmbTrials.setSelection(TRIALS.length - 1);

		lblCurrentAction = (TextView) findViewById(R.id.lblCurrentAction);
	}

	// called when the "OK" button is tapped
	public void btnOK_Click(View view)
	{
		// get the selected values from the spinners
		String subjectID = cmbSubjectID.getSelectedItem().toString();
		ControlMethod controlMethod = ControlMethod.fromString(cmbControlMethod.getSelectedItem().toString());
		int trials = Integer.parseInt(cmbTrials.getSelectedItem().toString());

		// construct the bundle and start the experiment activity
		Bundle b = new Bundle();
		b.putParcelable("setup", new ExperimentSetup(subjectID, controlMethod, trials));
		Intent i = new Intent(getApplicationContext(), ExperimentActivity.class);
		i.putExtras(b);
		startActivity(i);
	}

	public void btnOK2_Click(View view)
	{
//		// get the selected values from the spinners
//		String subjectID = cmbSubjectID.getSelectedItem().toString();
//		ControlMethod controlMethod = ControlMethod.fromString(cmbControlMethod.getSelectedItem().toString());
//		int trials = Integer.parseInt(cmbTrials.getSelectedItem().toString());
//
//		// construct the bundle and start the experiment activity
//		Bundle b = new Bundle();
//		b.putParcelable("setup", new ExperimentSetup(subjectID, controlMethod, trials));
//		Intent i = new Intent(getApplicationContext(), TouchModeActivity.class);
//		i.putExtras(b);
//		startActivity(i);
		ExperimentCoordinatorCallback callback = new ExperimentCoordinatorCallback() {
			@Override
			public void onExperimentStart() {
				Log.e("ExperimentCoordinator", "onExperimentStart");
			}

			@Override
			public void onNewTrial(int trial, ExperimentAction action) {
				Log.e("ExperimentCoordinator", "onNewTrial: " + trial + " " + action);
				lblCurrentAction.setText(action.toString());
			}

			@Override
			public void onIncorrectAction(int trial, ExperimentAction action) {
				Log.e("ExperimentCoordinator", "onIncorrectAction: " + trial + " " + action);
			}

			@Override
			public void onExperimentFinished(ExperimentResult result) {
				Log.e("ExperimentCoordinator", "onExperimentFinished");
				lblCurrentAction.setText("-Experiment Finished-");
				for(int i = 0; i < result.getDurationEachTrial().length; i++) {
					Log.e("ExperimentCoordinator", "Trial " + i + ": " + result.getDurationEachTrial()[i] + "ms");
				}

				for(int i = 0; i < result.getIncorrectActionEachTrial().length; i++) {
					Log.e("ExperimentCoordinator", "Trial " + i + ": " + result.getIncorrectActionEachTrial()[i] + " incorrect actions");
				}

				ExperimentResultExporter.write(result);
			}
		};

		ExperimentSetup setup = new ExperimentSetup("99", ControlMethod.TOUCH, 10);

		coord = new ExperimentCoordinator(setup, callback);

		coord.startExperiment();
	}

	/** Called when the "Exit" button is pressed. */
	public void btnExit_Click(View view)
	{
		super.onDestroy(); // cleanup
		this.finish(); // terminate
	}

	public void btnPrev_Click(View view)
	{
		if(coord != null) {
			coord.performAction(ExperimentAction.PREVIOUS);
		}
	}

	public void btnNext_Click(View view)
	{
		if(coord != null) {
			coord.performAction(ExperimentAction.NEXT);
		}
	}

	public void btnLike_Click(View view)
	{
		if(coord != null) {
			coord.performAction(ExperimentAction.LIKE);
		}
	}

	public void btnComment_Click(View view)
	{
		if(coord != null) {
			coord.performAction(ExperimentAction.COMMENT);
		}
	}

	public void btnShare_Click(View view)
	{
		if(coord != null) {
			coord.performAction(ExperimentAction.SHARE);
		}
	}
}
