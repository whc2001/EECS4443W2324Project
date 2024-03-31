package ca.yorku.eecs.groupr.tilttiktok;

// Class for storing experiment results

public class ExperimentResult {
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
}
