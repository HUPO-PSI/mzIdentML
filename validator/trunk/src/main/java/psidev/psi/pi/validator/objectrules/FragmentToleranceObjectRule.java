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
 * Check if there is a fragment tolerance element at the SpectrumIdentificationProcotol.
 * 
 * @author Salva
 * 
 */
public class FragmentToleranceObjectRule extends AObjectRule<SpectrumIdentificationProtocol> {

    // Contexts
    private static final Context SIPContext = new Context(MzIdentMLElement.SpectrumIdentificationProtocol.getXpath());

    // We had a problem with the default constructor. It was necessary to build a new one this way to call the ObjectRule
    public FragmentToleranceObjectRule() {
        this(null);
    }

    // Another constructor that calls to ObjectRule
    public FragmentToleranceObjectRule(OntologyManager ontologyManager) {
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
     * @throws ValidatorException 
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationProtocol protocol) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (protocol.getFragmentTolerance() == null) {
            messages.add(new ValidatorMessage("There is not a fragment tolerance in SpectrumIdentificationProtocol (id='"
                + protocol.getId() + "') element at " + FragmentToleranceObjectRule.SIPContext.getContext()
                + "/SiteRegexp", MessageLevel.ERROR, FragmentToleranceObjectRule.SIPContext, this));
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

        ret.add("Add the element 'FragmentTolerance' to the SpectrumIdentificationProtocol at " + FragmentToleranceObjectRule.SIPContext.getContext());
        
        return ret;
    }
}
