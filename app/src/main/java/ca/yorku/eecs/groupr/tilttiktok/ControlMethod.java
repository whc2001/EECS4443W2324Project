package ca.yorku.eecs.groupr.tilttiktok;

// Enum for the control method

public enum ControlMethod {
    TOUCH("Touch"),
    TILT("Tilt")
    ;

    private String displayName;
    ControlMethod(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ControlMethod fromString(String text) {
        for (ControlMethod controlMethod : ControlMethod.values()) {
            if (controlMethod.displayName.equalsIgnoreCase(text)) {
                return controlMethod;
            }
        }
        return null;
    }
}
