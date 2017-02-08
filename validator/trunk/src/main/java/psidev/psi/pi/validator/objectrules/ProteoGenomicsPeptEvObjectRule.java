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
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidence;

/**
 * Check if the quartet of mandatory CV terms for each PeptideEvidence or the term unmapped peptide (MS:1002740) is present for a proteogenomics search.
 * 
 * @author Gerhard
 * 
 */
public class ProteoGenomicsPeptEvObjectRule extends AObjectRule<PeptideEvidence> {

    /**
     * Constants.
     */
    private static final Context PEV_CONTEXT = new Context(MzIdentMLElement.PeptideEvidence.getXpath());
    private final String quartetMsg = " quartet of proteogenomics attribute terms (childs of MS:1002520) or 'unmapped peptide' (MS:1002740) ";

    /**
     * Members.
     */
    private boolean peptideEndOnChromosomeTerm              = false;
    private boolean peptideExonCountTerm                    = false;
    private boolean peptideExonNucleotideSizesTerm          = false;
    private boolean peptideStartPositionsOnChromosomeTerm   = false;
    private boolean unmappedPeptideTerm                     = false;
    
    /**
     * Constructors.
     */
    public ProteoGenomicsPeptEvObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ProteoGenomicsPeptEvObjectRule(OntologyManager ontologyManager) {
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
     * Checks, if the required quartet of terms is present in case of a proteogenomics search.
     * 
     * @param pev the PeptideEvidence element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(PeptideEvidence pev) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch && !pev.isIsDecoy()) {
            String acc;
            for (CvParam cv: pev.getCvParam()) {
                acc = cv.getAccession();
                switch (acc) {
                    case "MS:1002640":  // peptide end on chromosome
                        this.peptideEndOnChromosomeTerm = true;
                        break;
                    case "MS:1002641":  // peptide exon count
                        this.peptideExonCountTerm = true;
                        break;
                    case "MS:1002642":  // peptide exon nucleotide sizes
                        this.peptideExonNucleotideSizesTerm = true;
                        break;
                    case "MS:1002643":  // peptide start positions on chromosome
                        this.peptideStartPositionsOnChromosomeTerm = true;
                        break;
                    case "MS:1002740":  // unmapped peptide
                        this.unmappedPeptideTerm = true;
                        break;
                }
            }
            
            if (!this.unmappedPeptideTerm) {
                if (  !(this.peptideEndOnChromosomeTerm             &&
                        this.peptideExonCountTerm                   &&
                        this.peptideExonNucleotideSizesTerm         &&
                        this.peptideStartPositionsOnChromosomeTerm)) {
                    this.addMessageToCollection(pev, messages);
                }
            }
        }

        return messages;
    }
    
    /**
     * Adds a message to the messages collection.
     * @param pev
     * @param messages 
     */
    private void addMessageToCollection(PeptideEvidence pev, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The PeptideEvidence (id='"
        + pev.getId() + "') element at " + ProteoGenomicsPeptEvObjectRule.PEV_CONTEXT.getContext()
        + " doesn't contain the " + this.quartetMsg + "required in case of a proteogenomics search", MessageLevel.ERROR, ProteoGenomicsPeptEvObjectRule.PEV_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Add the" + this.quartetMsg + "to each PeptideEvidence  in case of a proteogenomics search.");
        
        return ret;
    }
}
