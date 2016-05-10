package psidev.psi.pi.rulefilter;

/**
 * Enum for the DB type.
 */
public enum DatabaseTypeCondition {
    // enum values
    DECOY_DATABASE("DECOY_DATABASE"),
    NO_DECOY_DATABASE("NO_DECOY_DATABASE");

    // members
    private final String option;

    /**
     * Standard constructor.
     * @param option 
     */
    DatabaseTypeCondition(String option) {
        this.option = option;
    }

    /**
     * Gets the ID for this enum.
     * @return the ID
     */
    public static String getID() {
        return "DATABASE_TYPE";
    }

    /**
     * Gets the option.
     * @return the option
     */
    public String getOption() {
        return this.option;
    }
}
