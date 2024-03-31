package ca.yorku.eecs.groupr.tilttiktok;

// Class for controlling the experiment process

public class ExperimentCoordinator {
    private ExperimentCoordinatorCallback callback;
    private boolean isExperimentRunning = false;
    private int currentTrial = 0;
    private int totalTrials;
    private long currentTrialStartTime;
    private ExperimentAction[] experimentActions;
    private long[] durationEachTrial;
    private int[] incorrectActionEachTrial;

    private void startNextTrial() {
        if (currentTrial < totalTrials) {
            experimentActions[currentTrial] = ExperimentAction.getRandomAction();
            callback.onNewTrial(currentTrial, experimentActions[currentTrial]);
            currentTrialStartTime = System.currentTimeMillis();
        } else {
            endExperiment();
        }
    }

    public ExperimentCoordinator(ExperimentCoordinatorCallback callback, int totalTrials) {
        this.callback = callback;
        this.totalTrials = totalTrials;
        durationEachTrial = new long[totalTrials];
        incorrectActionEachTrial = new int[totalTrials];
        experimentActions = new ExperimentAction[totalTrials];
    }

    public void startExperiment() {
        isExperimentRunning = true;
        currentTrial = 0;
        callback.onExperimentStart();
        startNextTrial();
    }

    public void endExperiment() {
        isExperimentRunning = false;
        callback.onExperimentFinished(durationEachTrial, incorrectActionEachTrial);
    }

    public void performAction(ExperimentAction action) {
        if (isExperimentRunning) {
            if (action != experimentActions[currentTrial]) {
                callback.onIncorrectAction(currentTrial, action);
                ++incorrectActionEachTrial[currentTrial];
            } else {
                long duration = System.currentTimeMillis() - currentTrialStartTime;
                durationEachTrial[currentTrial] = duration;
                currentTrial++;
                startNextTrial();
            }
        }
    }
}
