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

	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		if(savedInstanceState != null && savedInstanceState.getBoolean("exit"))
		{
			super.onDestroy();
			this.finish();
		}

		cmbSubjectID = (Spinner) findViewById(R.id.cmbSubjectID);
		cmbSubjectID.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, SUBJECT_ID));
		cmbSubjectID.setSelection(0);

		cmbControlMethod = (Spinner) findViewById(R.id.cmbControlMethod);
		cmbControlMethod.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, CONTROL_METHOD));
		cmbControlMethod.setSelection(0);

		cmbTrials = (Spinner) findViewById(R.id.cmbTrials);
		cmbTrials.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, TRIALS));
		cmbTrials.setSelection(TRIALS.length - 1);
	}

	// called when the "OK" button is tapped
	public void btnOK_Click(View view)
	{
//		// get the selected values from the spinners
		String subjectID = cmbSubjectID.getSelectedItem().toString();
		ControlMethod controlMethod = ControlMethod.fromString(cmbControlMethod.getSelectedItem().toString());
		int trials = Integer.parseInt(cmbTrials.getSelectedItem().toString());

		// construct the bundle and start the experiment activity
		Bundle b = new Bundle();
		b.putParcelable("setup", new ExperimentSetup(subjectID, controlMethod, trials));
		Intent i = new Intent(getApplicationContext(), controlMethod == ControlMethod.TOUCH ? TouchModeActivity.class : null);
		i.putExtras(b);
		startActivity(i);
	}

	/** Called when the "Exit" button is pressed. */
	public void btnExit_Click(View view)
	{
		super.onDestroy(); // cleanup
		this.finish(); // terminate
	}
}
