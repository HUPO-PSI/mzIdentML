package psidev.psi.pi.rulefilter;

/**
 * Enum for the cleavage.
 */
public enum CleavageRuleCondition {
    // enum values
    USER_SPECIFIC_CLEAVAGE_RULE("USER_SPECIFIC_CLEAVAGE_RULE"),
    NO_USER_SPECIFIC_CLEAVAGE_RULE("NO_USER_SPECIFIC_CLEAVAGE_RULE");

    // members
    private final String option;

    /**
     * Standard constructor.
     * @param option 
     */
    CleavageRuleCondition(String option) {
        this.option = option;
    }

    /**
     * Gets the ID for this enum.
     * @return the ID
     */
    public static String getID() {
        return "CLEAVAGE_TYPE";
    }

    /**
     * Gets the option.
     * @return the option
     */
    public String getOption() {
        return this.option;
    }
}
