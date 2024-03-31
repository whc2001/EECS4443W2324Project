package ca.yorku.eecs.groupr.tilttiktok;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Enum for the actions available in the experiment

public enum ExperimentAction {
    NEXT("Swipe to next video"),
    PREVIOUS("Swipe to previous video"),
    LIKE("Like the current video"),
    COMMENT("Open the comment of the current video"),
    SHARE("Share the current video"),
    ;

    private String displayName;
    private static final List<ExperimentAction> values =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int size = values.size();
    private static final Random r = new Random();

    ExperimentAction(String displayName) {
        this.displayName = displayName;
    }

    public static ExperimentAction getRandomAction() {
        return values.get(r.nextInt(size));
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ExperimentAction fromString(String text) {
        for (ExperimentAction experimentAction : ExperimentAction.values()) {
            if (experimentAction.displayName.equalsIgnoreCase(text)) {
                return experimentAction;
            }
        }
        return null;
    }
}
