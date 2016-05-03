package psidev.psi.pi.rulefilter;

/**
 * Enum for the peptides level stats case.
 */
public enum PeptideLevelStatsCondition {
    // enum values
    USER_SPECIFIC_PEPTIDELEVELSTATS_RULE("USER_SPECIFIC_PEPTIDELEVELSTATS_RULE"),
    NO_USER_SPECIFIC_PEPTIDELEVELSTATS_RULE("NO_USER_SPECIFIC_PEPTIDELEVELSTATSS_RULE");

    // members
    private final String option;

    /**
     * Standard constructor.
     * @param option 
     */
    PeptideLevelStatsCondition(String option) {
        this.option = option;
    }

    /**
     * Gets the ID for this enum.
     * @return the ID
     */
    public static String getID() {
        return "PEPTIDELEVELSTATS_RULE";
    }

    /**
     * Gets the option.
     * @return the option
     */
    public String getOption() {
        return this.option;
    }
}
