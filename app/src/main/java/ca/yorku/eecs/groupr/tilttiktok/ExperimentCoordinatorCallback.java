package ca.yorku.eecs.groupr.tilttiktok;

// Callback interface for the coordinator to communicate back with the activity

public interface ExperimentCoordinatorCallback {
    void onExperimentStart(int currentTrial, ExperimentAction action);
    void onExperimentFinished(ExperimentResult result);
    void onNewTrial(int currentTrial, ExperimentAction action);
    void onIncorrectAction(int currentTrial, ExperimentAction action);
}
