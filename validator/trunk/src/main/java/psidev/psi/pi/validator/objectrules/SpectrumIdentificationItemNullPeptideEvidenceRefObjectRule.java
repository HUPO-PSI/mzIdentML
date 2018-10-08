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
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;

/**
 * Reports an error if there is a PeptideEvidenceRef element over the {@link SpectrumIdentificationItem}.<br>
 * <b>Important note: the results of this rule will be discarded when a de novo search type is performed,
 * that is, when "de novo search" CV Term is reported on the AnalysisProtocolCollection/SpectrumIdentificationProtocol/AdditionalSearchParams.</b>
 * 
 * @author Salva
 */
public class SpectrumIdentificationItemNullPeptideEvidenceRefObjectRule extends	AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constants.
     */
    private static final Context SII_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationItem.getXpath());

    /**
     * Constructor.
     */
    public SpectrumIdentificationItemNullPeptideEvidenceRefObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SpectrumIdentificationItemNullPeptideEvidenceRefObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationItem.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationItem
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationItem);
    }

    /**
     * 
     * @param spectrumIdentificationItem the SpectrumIdentificationItem element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationItem spectrumIdentificationItem) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (!spectrumIdentificationItem.getPeptideEvidenceRef().isEmpty()) {
            messages.add(new ValidatorMessage("PeptideEvidenceRef in the SpectrumIdentificationItem element is not allowed for de novo searches", MessageLevel.ERROR, SII_CONTEXT, this));
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

        ret.add("Remove the PeptideEvidenceRef element for de novo search.");
        return ret;
    }
}
