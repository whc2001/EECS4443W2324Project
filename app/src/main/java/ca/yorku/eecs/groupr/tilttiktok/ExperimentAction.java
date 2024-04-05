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

    ExperimentAction(String displayName) {
        this.displayName = displayName;
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
