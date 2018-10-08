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
import uk.ac.ebi.jmzidml.model.mzidml.DBSequence;

/**
 * Checks, if the DBSequence is referenced by a PeptideEvidence.
 * 
 * @author Gerhard
 * 
 */
public class DBSequenceObjectRule extends AObjectRule<DBSequence> {

    /**
     * Constants.
     */
    private static final Context DBSEQUENCE_CONTEXT = new Context(MzIdentMLElement.DBSequence.getXpath());
    
    /**
     * Constructor.
     */
    public DBSequenceObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public DBSequenceObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a DBSequence.
     * 
     * @param obj   the object to check
     * @return true, if obj is a DBSequence
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof DBSequence);
    }

    /**
     * Checks, if the DBSequence is referenced by a PeptideEvidence.
     * 
     * @param dbSeq the DBSequence element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(DBSequence dbSeq) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();
        
        if (!PeptideEvidenceObjectRule.dbSeqRefSet.contains(dbSeq.getId())) {
            messages.add(new ValidatorMessage("The DBSequence with ID " + dbSeq.getId() + " is not referenced by a PeptideEvidence element at" + 
                DBSequenceObjectRule.DBSEQUENCE_CONTEXT.getContext(),
                MessageLevel.WARN, DBSequenceObjectRule.DBSEQUENCE_CONTEXT, this));
        }
        
        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return Collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Reported DBSequences SHOULD be referenced by at least one PeptideEvidence element.");
        
        return ret;
    }
}
