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
 * Reports an error if there is not a PeptideEvidenceRef element over the {@link SpectrumIdentificationItem}.<br>
 * <b>Important note: the results of this rule will be discarded when MSMS or
 * PMF search type is performed, that is, when "ms-ms  search", "pmf+msms" or
 * "pmf" CV Terms are reported on the APC/SIP/searchType.</b>
 * 
 * @author Salva
 */
public class SpectrumIdentificationItemPeptideEvidenceRefObjectRule extends AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constants.
     */
    private static final Context SII_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationItem.getXpath());

    /**
     * Constants.
     */
    public SpectrumIdentificationItemPeptideEvidenceRefObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SpectrumIdentificationItemPeptideEvidenceRefObjectRule(OntologyManager ontologyManager) {
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

        if (spectrumIdentificationItem.getPeptideEvidenceRef().isEmpty()) {
            messages.add(new ValidatorMessage(
                "A PeptideEvidenceRef is needed at the SpectrumIdentificationItem element for MS/MS or PMF searches",
                MessageLevel.ERROR, SII_CONTEXT, this));
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

        ret.add("Add the PeptideEvidenceRef element for MS/MS or PMF searches.");
        
        return ret;
    }
}
