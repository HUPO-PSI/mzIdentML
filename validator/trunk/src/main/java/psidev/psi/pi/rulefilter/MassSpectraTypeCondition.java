package psidev.psi.pi.rulefilter;

/**
 * Enum for the MS type.
 */
public enum MassSpectraTypeCondition {
    PMF("PMF"),
    PFF("PFF"),
    PMFPFF("PMFPFF");
    
    private final String option;

    public static String getID() {
        return "MASS_SPECTRA_TYPE";
    }

    MassSpectraTypeCondition(String option) {
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }
}
