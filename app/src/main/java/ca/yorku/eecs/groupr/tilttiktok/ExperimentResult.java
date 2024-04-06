package ca.yorku.eecs.groupr.tilttiktok;

// Class for storing experiment results

import android.os.Parcel;
import android.os.Parcelable;

public class ExperimentResult implements Parcelable {
    private ExperimentSetup setup;
    private long experimentStartTime;
    private long[] durationEachTrial;
    private int[] incorrectActionEachTrial;

    public ExperimentResult(ExperimentSetup setup, long experimentStartTime, long[] durationEachTrial, int[] incorrectActionEachTrial) {
        this.setup = setup;
        this.experimentStartTime = experimentStartTime;
        this.durationEachTrial = durationEachTrial;
        this.incorrectActionEachTrial = incorrectActionEachTrial;
    }

    public ExperimentSetup getSetup() {
        return setup;
    }

    public long getExperimentStartTime() {
        return experimentStartTime;
    }

    public long[] getDurationEachTrial() {
        return durationEachTrial;
    }

    public int[] getIncorrectActionEachTrial() {
        return incorrectActionEachTrial;
    }

    public ExperimentResult() {
    }

    protected ExperimentResult(Parcel in) {
        setup = in.readParcelable(ExperimentSetup.class.getClassLoader());
        experimentStartTime = in.readLong();
        durationEachTrial = in.createLongArray();
        incorrectActionEachTrial = in.createIntArray();
    }

    public static final Creator<ExperimentResult> CREATOR = new Creator<ExperimentResult>() {
        @Override
        public ExperimentResult createFromParcel(Parcel in) {
            return new ExperimentResult(in);
        }

        @Override
        public ExperimentResult[] newArray(int size) {
            return new ExperimentResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(setup, i);
        parcel.writeLong(experimentStartTime);
        parcel.writeLongArray(durationEachTrial);
        parcel.writeIntArray(incorrectActionEachTrial);
    }
}
