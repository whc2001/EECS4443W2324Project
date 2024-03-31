package ca.yorku.eecs.groupr.tilttiktok;

import android.os.Parcel;
import android.os.Parcelable;

public class ExperimentSetup implements Parcelable {
    private String subjectID;
    private ControlMethod controlMethod;
    private int trials;

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public ControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(ControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }

    public int getTrials() {
        return trials;
    }

    public void setTrials(int trials) {
        this.trials = trials;
    }

    // Parcelable implementation
    public ExperimentSetup() {
    }

    public ExperimentSetup(String subjectID, ControlMethod controlMethod, int trials) {
        this.subjectID = subjectID;
        this.controlMethod = controlMethod;
        this.trials = trials;
    }

    protected ExperimentSetup(Parcel in) {
        subjectID = in.readString();
        controlMethod = ControlMethod.fromString(in.readString());
        trials = in.readInt();
    }

    public static final Creator<ExperimentSetup> CREATOR = new Creator<ExperimentSetup>() {
        @Override
        public ExperimentSetup createFromParcel(Parcel in) {
            return new ExperimentSetup(in);
        }

        @Override
        public ExperimentSetup[] newArray(int size) {
            return new ExperimentSetup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subjectID);
        parcel.writeString(controlMethod.toString());
        parcel.writeInt(trials);
    }
}
