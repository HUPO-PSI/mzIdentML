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
 * Check if the octuplet of mandatory CV terms is present for a proteogenomics search.
 * 
 * @author Gerhard
 * 
 */
public class ProteoGenomicsObjectRule extends AObjectRule<PeptideEvidence> {

    /**
     * Constants.
     */
    private static final Context PEV_CONTEXT = new Context(MzIdentMLElement.PeptideEvidence.getXpath());
    private final String octupletMsg = " octuplet of proteogenomics attribute terms (childs of MS:1002520) ";

    /**
     * Members.
     */
    private boolean chromosomeNameTerm                      = false;
    private boolean chromosomeStrandTerm                    = false;
    private boolean peptideStartOnChromosomeTerm            = false;
    private boolean peptideEndOnChromosomeTerm              = false;
    private boolean peptideExonCountTerm                    = false;
    private boolean peptideExonNucleotideSizesTerm          = false;
    private boolean peptideStartPositionsOnChromosomeTerm   = false;
    private boolean genomeReferenceVersionTerm              = false;
    
    /**
     * Constructors.
     */
    public ProteoGenomicsObjectRule() {
        this(null);
    }

    /**
     * 
     * @param ontologyManager the ontology manager
     */
    public ProteoGenomicsObjectRule(OntologyManager ontologyManager) {
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
     * Checks, if the required octuplet of terms is present in case of a proteogenomics search.
     * 
     * @param pev the PeptideEvidence element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(PeptideEvidence pev) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch) {
            String acc;
            for (CvParam cv: pev.getCvParam()) {
                acc = cv.getAccession();
                switch (acc) {
                    case "MS:1002637":  // chromosome name
                        this.chromosomeNameTerm = true;
                        break;
                    case "MS:1002638":  // chromosome strand
                        this.chromosomeStrandTerm = true;
                        break;
                    case "MS:1002639":  // peptide start on chromosome
                        this.peptideStartOnChromosomeTerm = true;
                        break;
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
                    case "MS:1002644":  // genome reference version
                        this.genomeReferenceVersionTerm = true;
                        break;
                }
            }
            
            if (  !(this.chromosomeNameTerm                     &&
                    this.chromosomeStrandTerm                   &&
                    this.peptideStartOnChromosomeTerm           &&
                    this.peptideEndOnChromosomeTerm             &&
                    this.peptideExonCountTerm                   &&
                    this.peptideExonNucleotideSizesTerm         &&
                    this.peptideStartPositionsOnChromosomeTerm  &&
                    this.genomeReferenceVersionTerm)) {
                this.addMessageToCollection(pev, messages);
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
        + pev.getId() + "') element at " + ProteoGenomicsObjectRule.PEV_CONTEXT.getContext()
        + " doesn't contain the " + this.octupletMsg + "required in case of a proteogenomics search", MessageLevel.ERROR, ProteoGenomicsObjectRule.PEV_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Add the" + this.octupletMsg + "to each PeptideEvidence");
        
        return ret;
    }
}
