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
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.DBSequence;

/**
 * Checks if the triplet of genome reference version (MS:1002644), chromosome name (MS:1002637) and chromosome strand (MS:1002638)
 * or the term unmapped protein (MS:1002741) is present under the DBSequence for a proteogenomics search.
 * 
 * @author Gerhard
 * 
 */
public class ProteoGenomicsDBSeqObjectRule extends AObjectRule<DBSequence> {

    /**
     * Constants.
     */
    private static final Context DBSEQ_CONTEXT = new Context(MzIdentMLElement.DBSequence.getXpath());
    private final String tripletMsg = "  triplet of proteogenomics attribute terms (childs of MS:1002520) or 'unmapped protein' (MS:1002741) ";

    /**
     * Members.
     */
    private boolean chromosomeNameTerm          = false;
    private boolean chromosomeStrandTerm        = false;
    private boolean genomeReferenceVersionTerm  = false;
    private boolean unmappedProteinTerm         = false;
    
    /**
     * Constructors.
     */
    public ProteoGenomicsDBSeqObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ProteoGenomicsDBSeqObjectRule(OntologyManager ontologyManager) {
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
     * Checks, if the required tripletMsg of terms is present in case of a proteogenomics search.
     * 
     * @param dbseq the DBSequence element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(DBSequence dbseq) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch) {
            String acc;
            boolean bFound = false;
            
            for (CvParam cv: dbseq.getCvParam()) {
                if (cv != null) {
                    acc = cv.getAccession();
                    switch (acc) {
                        case "MS:1002637":  // chromosome name
                            this.chromosomeNameTerm = true;
                            break;
                        case "MS:1002638":  // chromosome strand
                            this.chromosomeStrandTerm = true;
                            break;
                        case "MS:1002644":  // genome reference version
                            this.genomeReferenceVersionTerm = true;
                            bFound = true;
                            break;
                        case "MS:1002741":  // unmappedProtein
                            this.unmappedProteinTerm = true;
                            bFound = true;
                            break;
                    }
                }
                if (bFound) {
                    break;
                }
            }

            if (!this.unmappedProteinTerm) {
                if (  !(this.chromosomeNameTerm         &&
                        this.chromosomeStrandTerm       &&
                        this.genomeReferenceVersionTerm)) {
                    this.addMessageToCollection(dbseq, messages);
                }
            }
        }

        return messages;
    }
    
    /**
     * Adds a message to the messages collection.
     * @param dbseq
     * @param messages 
     */
    private void addMessageToCollection(DBSequence dbseq, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The DBSequence (id='"
        + dbseq.getId() + "') element at " + ProteoGenomicsDBSeqObjectRule.DBSEQ_CONTEXT.getContext()
        + " doesn't contain the " + this.tripletMsg + "required in case of a proteogenomics search", MessageLevel.ERROR, ProteoGenomicsDBSeqObjectRule.DBSEQ_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Add the" + this.tripletMsg + "to each DBSequence in case of a proteogenomics search.");
        
        return ret;
    }
}
