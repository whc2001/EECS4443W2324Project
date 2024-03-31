package ca.yorku.eecs.groupr.tilttiktok;

// Callback interface for the coordinator to communicate back with the activity

public interface ExperimentCoordinatorCallback {
    void onExperimentStart();
    void onExperimentFinished(long[] durationEachTrial, int[] incorrectActionEachTrial);
    void onNewTrial(int currentTrial, ExperimentAction action);
    void onIncorrectAction(int currentTrial, ExperimentAction action);
}
