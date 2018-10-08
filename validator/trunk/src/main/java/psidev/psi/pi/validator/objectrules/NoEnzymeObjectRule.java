package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import psidev.psi.pi.validator.objectrules.util.ObjectRuleUtil;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.Enzyme;

/**
 * Check if the missed cleavages number or the SiteRegexp are defined in the
 * enzyme when "No Enzyme" is specified. In that case, create an error.
 * 
 * @author Salva
 * 
 */
public class NoEnzymeObjectRule extends AObjectRule<Enzyme> {

    /**
     * Constants.
     */
    private static final Context ENZYME_CONTEXT = new Context(MzIdentMLElement.Enzyme.getXpath());
    private static final String NO_ENZYME_ACC = "MS:1001091";

    /**
     * Constructor.
     */
    public NoEnzymeObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public NoEnzymeObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is an Enzyme.
     * 
     * @param obj   the object to check
     * @return true, if obj is an Enzyme
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof Enzyme);
    }

    /**
     * 
     * @param enzyme the Enzyme element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Enzyme enzyme) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (enzyme.getEnzymeName() != null) {
            final CvParam noEnzymeTerm = ObjectRuleUtil.checkAccessionsInCVParams(enzyme.getEnzymeName().getCvParam(), NO_ENZYME_ACC);
            
            if (noEnzymeTerm != null) {
                if (enzyme.getMissedCleavages() != null) {
                    messages.add(new ValidatorMessage("The attribute 'missed cleavages' at "
                        + NoEnzymeObjectRule.ENZYME_CONTEXT.getContext()
                        + " makes no sense since 'No enzyme' ('"
                        + NO_ENZYME_ACC + "') has been specified.",
                        MessageLevel.ERROR, NoEnzymeObjectRule.ENZYME_CONTEXT, this));
                }
                
                if (enzyme.getSiteRegexp() != null) {
                    messages.add(new ValidatorMessage("A definition of the element 'SiteRegexp' at "
                        + NoEnzymeObjectRule.ENZYME_CONTEXT.getContext()
                        + " has no sense since 'No enzyme' ('"
                        + NO_ENZYME_ACC + "') has been specified.",
                        MessageLevel.ERROR, NoEnzymeObjectRule.ENZYME_CONTEXT, this));
                }
            }
            else {
                if (enzyme.getMissedCleavages() == null) {
                    messages.add(new ValidatorMessage("The attribute 'missed cleavages' at "
                        + NoEnzymeObjectRule.ENZYME_CONTEXT.getContext()
                        + " MUST be provided if an enzyme is provided (different from a 'No enzyme'/'"
                        + NO_ENZYME_ACC + "') has been specified.",
                        MessageLevel.ERROR, NoEnzymeObjectRule.ENZYME_CONTEXT, this));
                }
            }
        }
        
        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return  collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Remove the information specified in the message.");
        return ret;
    }
}
