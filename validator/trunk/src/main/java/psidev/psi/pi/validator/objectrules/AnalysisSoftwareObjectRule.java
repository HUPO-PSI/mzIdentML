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
    private boolean bVersionError = false;
    private boolean bContactRoleMissingError = false;

    /**
     * Constructor.
     */
    public AnalysisSoftwareObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
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
            this.bVersionError = true;
            messages.add(new ValidatorMessage(
                "There is not a version in the Analysis Software (id='" + software.getId()
                + "') element at " + AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT.getContext(),
                MessageLevel.ERROR, AnalysisSoftwareObjectRule.ANALYSISSW_CONTEXT, this));
        }
        // contactRole
        if (software.getContactRole() == null) {
            this.bContactRoleMissingError = true;
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

        if (this.bVersionError) {
            ret.add("Add the attribute 'version' to the AnalysisSoftware.");
        }
        
        if (this.bContactRoleMissingError) {
            ret.add("Add the contactRole element to the AnalysisSoftware.");
        }

        return ret;
    }
}
