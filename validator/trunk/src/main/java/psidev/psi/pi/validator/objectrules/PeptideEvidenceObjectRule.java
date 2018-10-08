package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.DBSequence;
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence;

/**
 * Check if the start and end attributes of PeptideEvidence are set unless it's a de novo search.
 * 
 * @author Gerhard
 * 
 */
public class PeptideEvidenceObjectRule extends AObjectRule<PeptideEvidence> {

    /**
     * Constants.
     */
    private static final Context PEV_CONTEXT    = new Context(MzIdentMLElement.PeptideEvidence.getXpath());
    private final String startEndAttrMissingMsg = " must have correct start and end attributes set (since it's not a de novo search).";
    private final String startEndAttrWrongMsg   = " has wrong start and end attributes set (start must be >= 1 and end >=start, but < length of the protein sequence.";
    public static HashSet<String> peptideRefSet = new HashSet<>();
    public static HashSet<String> dbSeqRefSet   = new HashSet<>();
    public static HashMap<String, String> peptideRef2PeptideEvidenceIDMap = new HashMap<>();
    
    /**
     * Constructors.
     */
    public PeptideEvidenceObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public PeptideEvidenceObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a PeptideEvidence.
     * 
     * @param obj   the object to check
     * @return true, if obj is a PeptideEvidence
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof PeptideEvidence);
    }

    /**
     * Checks, if the required triplet of terms is present in case of peptide-level scoring.
     * 
     * @param pev the PeptideEvidence element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(PeptideEvidence pev) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (!SearchTypeObjectRule.bIsDeNovoSearch) {
            int start=0, end=0;
            
            try {
                start = pev.getStart();
                end = pev.getEnd();
            }
            catch (NullPointerException ex) {
                this.addMissingMessageToCollection(pev, messages);
            }
            
            if (start < 1 || end <= start) {
                this.addWrongMessageToCollection(pev, messages);
            }
            else {
                DBSequence dbSequence = pev.getDBSequence();    // TODO: Why is dbSequence here null ?
                
                if (dbSequence != null && end > dbSequence.getSeq().length()) {
                    this.addWrongMessageToCollection(pev, messages);
                }
                else {
                    this.LOGGER.info("dbSequence is null");
                }
            }
        }
        
        PeptideEvidenceObjectRule.peptideRefSet.add(pev.getPeptideRef());
        PeptideEvidenceObjectRule.dbSeqRefSet.add(pev.getDBSequenceRef());
        PeptideEvidenceObjectRule.peptideRef2PeptideEvidenceIDMap.put(pev.getPeptideRef(), pev.getId());

        return messages;
    }
    
    /**
     * Adds a missing start/end attribute message to the messages collection.
     * @param pev
     * @param messages 
     */
    private void addMissingMessageToCollection(PeptideEvidence pev, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The PeptideEvidence (id='"
        + pev.getId() + "') element at " + PeptideEvidenceObjectRule.PEV_CONTEXT.getContext()
        + this.startEndAttrMissingMsg, MessageLevel.ERROR, PeptideEvidenceObjectRule.PEV_CONTEXT, this));
    }
    
    /**
     * Adds a wrong start/end attribute message to the messages collection.
     * @param pev
     * @param messages 
     */
    private void addWrongMessageToCollection(PeptideEvidence pev, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The PeptideEvidence (id='"
        + pev.getId() + "') element at " + PeptideEvidenceObjectRule.PEV_CONTEXT.getContext()
        + this.startEndAttrWrongMsg, MessageLevel.ERROR, PeptideEvidenceObjectRule.PEV_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("<PeptideEvidence> elements must have correct start and end attributes set, unless it's a de novo search.");
        
        return ret;
    }
}
