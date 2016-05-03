package psidev.psi.pi.rulefilter;

/**
 * Enum for the de novo case.
 */
public enum DeNovoCondition {
    // enum values
    USER_SPECIFIC_DENOVO_RULE("USER_SPECIFIC_DENOVO_RULE"),
    NO_USER_SPECIFIC_DENOVO_RULE("NO_USER_SPECIFIC_DENOVO_RULE");

    // members
    private final String option;

    /**
     * Standard constructor.
     * @param option 
     */
    DeNovoCondition(String option) {
        this.option = option;
    }

    /**
     * Gets the ID for this enum.
     * @return the ID
     */
    public static String getID() {
        return "DENOVO_RULE";
    }

    /**
     * Gets the option.
     * @return the option
     */
    public String getOption() {
        return this.option;
    }
}
