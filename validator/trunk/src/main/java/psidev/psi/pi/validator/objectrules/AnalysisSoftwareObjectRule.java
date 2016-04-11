package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.AnalysisSoftware;

/**
 * Check if the analysis software has:
 * <ul>
 * <li>a version attribute</li>
 * <li>a contactRole element that reference a software vendor</li></ul>
 * 
 * @author Salva
 * 
 */
public class AnalysisSoftwareObjectRule extends AObjectRule<AnalysisSoftware> {

    /**
     * Constants.
     */
    private static final Context ANALYSISSW_CONTEXT = new Context(MzIdentMLElement.AnalysisSoftware.getXpath());

    /**
     * Members.
     */
    private boolean versionError = false;
    private boolean contactRoleMissingError = false;

    // We had a problem with the default constructor. It was necessary to build a new one this way to call the ObjectRule
    public AnalysisSoftwareObjectRule() {
        this(null);
    }

    // Another constructor that calls to ObjectRule
    public AnalysisSoftwareObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is an AnalysisSoftware.
     * 
     * @param obj   object to check
     * @return true, if obj is an AnalysisSoftware
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof AnalysisSoftware);
    }

    /**
     * 
     * @param software the AnalysisSoftware element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(AnalysisSoftware software) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        // version
        if (software.getVersion() == null || software.getVersion().isEmpty()) {
            this.versionError = true;
            messages.add(new ValidatorMessage(
                "There is not a version in the Analysis Software (id='" + software.getId()
                + "') element at " + AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT.getContext(),
                MessageLevel.ERROR, AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT, this));
        }
        // contactRole
        if (software.getContactRole() == null) {
            this.contactRoleMissingError = true;
            messages.add(new ValidatorMessage(
                "There is not a contactRole element to refer to a software vendor in the Analysis Software (id='"
                + software.getId() + "') element at " + AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT.getContext(), MessageLevel.ERROR,
                AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT, this));
        }

        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        if (this.versionError) {
            ret.add("Add the attribute 'version' to the AnalysisSoftware at " + AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT.getContext());
        }
        
        if (this.contactRoleMissingError) {
            ret.add("Add the contactRole element to the AnalysisSoftware at " + AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT.getContext() + " with the appropriate role type (software vendor)");
        }

        return ret;
    }
}
