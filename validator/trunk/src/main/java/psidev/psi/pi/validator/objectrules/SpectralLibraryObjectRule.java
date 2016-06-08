package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;

/**
 * Checks specific for a spectral library search (to be done).
 * 
 * @author Gerhard
 * 
 */
public class SpectralLibraryObjectRule extends AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constructors.
     */
    public SpectralLibraryObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SpectralLibraryObjectRule(OntologyManager ontologyManager) {
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
     * Checks, if the required triplet of terms is present in case of peptide-level scoring.
     * 
     * @param sii the SpectrumIdentificationItem element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationItem sii) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsSpectralLibrarySearch) {
            // TODO: implement
        }

        return messages;
    }
    
    /**
     * Adds a message to the messages collection.
     * @param sii
     * @param messages 
     */
    private void addMessageToCollection(SpectrumIdentificationItem sii, List<ValidatorMessage> messages) {
        // TODO: implement
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        // TODO: implement
        //ret.add("Add the " + this.tripletMsg + "to each SpectrumIdentificationItem");
        
        return ret;
    }
}
