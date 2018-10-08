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
import uk.ac.ebi.jmzidml.model.mzidml.Enzyme;

/**
 * Check if there is a regular expression in the enzyme.
 * 
 * @author Salva
 * 
 */
public class RegExpEnzymeObjectRule extends AObjectRule<Enzyme> {

    /**
     * Constants.
     */
    private static final Context ENZYME_CONTEXT = new Context(MzIdentMLElement.Enzyme.getXpath());

    /**
     * Constructor.
     */
    public RegExpEnzymeObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public RegExpEnzymeObjectRule(OntologyManager ontologyManager) {
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

        if (enzyme.getSiteRegexp() == null || enzyme.getSiteRegexp().isEmpty()) {
            messages.add(new ValidatorMessage("There is not a regular expression in Enzyme (id='"
            + enzyme.getId() + "') element at " + RegExpEnzymeObjectRule.ENZYME_CONTEXT.getContext()
            + "/SiteRegexp", MessageLevel.ERROR, RegExpEnzymeObjectRule.ENZYME_CONTEXT, this));
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

        ret.add("Add the element 'SiteRegexp' to the Enzyme.");
        
        return ret;
    }
}
