package psidev.psi.pi.rulefilter;

/**
 * Enum for the cleavage.
 */
public enum CleavageRuleCondition {
    USER_SPECIFIC_CLEAVAGE_RULE("USER_SPECIFIC_CLEAVAGE_RULE"),
    NO_USER_SPECIFIC_CLEAVAGE_RULE("NO_USER_SPECIFIC_CLEAVAGE_RULE");

    private final String option;

    public static String getID() {
        return "CLEAVAGE_RULE";
    }

    CleavageRuleCondition(String option) {
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }
}
