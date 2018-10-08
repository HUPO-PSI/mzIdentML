package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;

/**
 * Check if triplet of terms is present under SpectrumIdentificationItem in case of peptide-level scoring.
 * 
 * @author Gerhard
 * 
 */
public class PeptideLevelStatsObjectRule extends AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constants.
     */
    private static final Context SII_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationItem.getXpath());
    private final String tripletMsg = " triplet of terms MS:1002520 (peptide group ID), MS:1002500 (peptide passes threshold) and a child of MS:1002358 (search engine specific score for distinct peptides) ";
    private boolean firstTerm = false;
    private boolean secondTerm = false;
    private boolean thirdTerm = false;

    /**
     * Members.
     */
    private HashMap<String, String> map_1001143 = null; // maps accession to term name
    private HashMap<String, String> map_1002358 = null; // maps accession to term name
    
    /**
     * Constructors.
     */
    public PeptideLevelStatsObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public PeptideLevelStatsObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
        
        this.map_1001143 = this.getTermChildren("MS:1001143", "MS");
        this.map_1002358 = this.getTermChildren("MS:1002358", "MS");
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

        if (AdditionalSearchParamsObjectRule.bIsPeptideLevelScoring) {
            String acc;
            for (CvParam cv: sii.getCvParam()) {
                if (cv != null) {
                    acc = cv.getAccession();
                    if (this.isASearchEnginePeptideScore(acc) || this.isASearchEnginePSMScore(acc)) {
                        this.firstTerm = true;
                    }
                    switch (acc) {
                        case "MS:1002500":  // peptide passes threshold
                            this.secondTerm = true;
                            break;
                        case "MS:1002520":  // peptide group ID
                            this.thirdTerm = true;
                            break;
                    }
                }
            }
            if (!(this.firstTerm && this.secondTerm && this.thirdTerm)) {
                this.addMessageToCollection(sii, messages);
            }
        }

        return messages;
    }
    
    /**
     * Checks, if a term is a child of MS:1002358.
     * @param acc
     * @return true, if the accession belongs to a CV term, which is a child of MS:1002358 ("search engine specific score for distinct peptides")
     */
    private boolean isASearchEnginePeptideScore(String acc) {
        return this.map_1002358.containsKey(acc);
    }

    /**
     * Checks, if a term is a child of MS:1001143.
     * @param acc
     * @return true, if the accession belongs to a CV term, which is a child of MS:1001143 ("search engine specific score for PSMs")
     */
    private boolean isASearchEnginePSMScore(String acc) {
        return this.map_1001143.containsKey(acc);
    }

    /**
     * Adds a message to the messages collection.
     * @param sii
     * @param messages 
     */
    private void addMessageToCollection(SpectrumIdentificationItem sii, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The SpectrumIdentificationItem (id='"
        + sii.getId() + "') element at " + PeptideLevelStatsObjectRule.SII_CONTEXT.getContext()
        + " doesn't contain the " + this.tripletMsg + "required in case of peptide-level scoring", MessageLevel.ERROR, PeptideLevelStatsObjectRule.SII_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Add the" + this.tripletMsg + "to each SpectrumIdentificationItem.");
        
        return ret;
    }
}
