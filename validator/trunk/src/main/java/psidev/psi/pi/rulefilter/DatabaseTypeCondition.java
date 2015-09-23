package psidev.psi.pi.rulefilter;

/**
 * Enum for the DB type.
 */
public enum DatabaseTypeCondition {
    DECOY_DATABASE("DECOY_DATABASE"),
    NO_DECOY_DATABASE("NO_DECOY_DATABASE");

    private final String option;

    public static String getID() {
        return "DATABASE_TYPE";
    }

    DatabaseTypeCondition(String option) {
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }
}
