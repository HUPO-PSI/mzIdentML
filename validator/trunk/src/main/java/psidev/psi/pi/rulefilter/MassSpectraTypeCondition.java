package psidev.psi.pi.rulefilter;

/**
 * Enum for the MS type.
 */
public enum MassSpectraTypeCondition {
    // enum values
    PMF("PMF"),
    PFF("PFF"),
    PMFPFF("PMFPFF");

    // members
    private final String option;

    /**
     * Standard constructor.
     * @param option 
     */
    MassSpectraTypeCondition(String option) {
        this.option = option;
    }

    /**
     * Gets the ID for this enum.
     * @return the ID
     */
    public static String getID() {
        return "MASS_SPECTRA_TYPE";
    }

    /**
     * Gets the option.
     * @return the option
     */
    public String getOption() {
        return this.option;
    }
}
