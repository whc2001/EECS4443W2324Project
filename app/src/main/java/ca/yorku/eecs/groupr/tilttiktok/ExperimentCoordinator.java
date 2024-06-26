package ca.yorku.eecs.groupr.tilttiktok;

// Class for controlling the experiment process

import java.util.Random;

public class ExperimentCoordinator {
    private ExperimentCoordinatorCallback callback;
    private ExperimentSetup setup;

    private boolean isExperimentRunning = false;
    private long experimentStartTime;
    private int currentTrial;
    private int totalTrials;
    private long currentTrialStartTime;
    private ExperimentAction[] experimentActions;
    private long[] durationEachTrial;
    private int[] incorrectActionEachTrial;
    private Random r = new Random();

    private void initializeRandomActions() {
        experimentActions[0] = ExperimentAction.values()[r.nextInt(ExperimentAction.values().length)];
        for (int i = 1; i < totalTrials; i++) {
            ExperimentAction action;
            do {
                action = ExperimentAction.values()[r.nextInt(ExperimentAction.values().length)];
            } while (action == experimentActions[i - 1]);
            experimentActions[i] = action;
        }
    }

    private void startNextTrial() {
        if (currentTrial < totalTrials) {
            callback.onNewTrial(currentTrial, experimentActions[currentTrial]);
            currentTrialStartTime = System.currentTimeMillis();
        } else {
            endExperiment();
        }
    }

    public ExperimentCoordinator(ExperimentSetup setup, ExperimentCoordinatorCallback callback) {
        this.setup = setup;
        this.callback = callback;
        this.totalTrials = setup.getTrials();
        durationEachTrial = new long[totalTrials];
        incorrectActionEachTrial = new int[totalTrials];
        experimentActions = new ExperimentAction[totalTrials];
    }

    public void startExperiment() {
        initializeRandomActions();
        isExperimentRunning = true;
        currentTrial = 0;
        experimentStartTime = currentTrialStartTime = System.currentTimeMillis();
        callback.onExperimentStart(currentTrial, experimentActions[currentTrial]);
    }

    public void endExperiment() {
        isExperimentRunning = false;
        ExperimentResult result = new ExperimentResult(setup, experimentStartTime, durationEachTrial, incorrectActionEachTrial);
        callback.onExperimentFinished(result);
    }

    public void performAction(ExperimentAction action) {
        if (isExperimentRunning) {
            if (action != experimentActions[currentTrial]) {
                callback.onIncorrectAction(currentTrial, experimentActions[currentTrial]);
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
