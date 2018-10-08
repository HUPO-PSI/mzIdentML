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
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol;

/**
 * Check if there is a parent tolerance element at the SpectrumIdentificationProcotol.
 * 
 * @author Salva
 * 
 */
public class ParentToleranceObjectRule extends AObjectRule<SpectrumIdentificationProtocol> {

    /**
     * Constants.
     */
    private static final Context SIP_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationProtocol.getXpath());

    /**
     * Constructor.
     */
    public ParentToleranceObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ParentToleranceObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationProtocol.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationProtocol
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationProtocol);
    }

    /**
     * 
     * @param protocol the SpectrumIdentificationProtocol element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationProtocol protocol) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (protocol.getParentTolerance() == null) {
            messages.add(new ValidatorMessage("There is not a parent tolerance in SpectrumIdentificationProtocol (id='"
                + protocol.getId() + "') element at "
                + ParentToleranceObjectRule.SIP_CONTEXT.getContext()
                + "/SiteRegexp", MessageLevel.ERROR,
                ParentToleranceObjectRule.SIP_CONTEXT, this));
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

        ret.add("Add the element 'FragmentTolerance' to the SpectrumIdentificationProtocol.");
        
        return ret;
    }
}
