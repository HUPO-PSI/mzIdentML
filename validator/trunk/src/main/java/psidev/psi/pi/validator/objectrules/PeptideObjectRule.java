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
import uk.ac.ebi.jmzidml.model.mzidml.Peptide;

/**
 * Checks, if the Peptide is referenced by a PeptideEvidence.
 * 
 * @author Gerhard
 * 
 */
public class PeptideObjectRule extends AObjectRule<Peptide> {

    /**
     * Constants.
     */
    private static final Context PEPTIDE_CONTEXT = new Context(MzIdentMLElement.Peptide.getXpath());
    
    /**
     * Constructor.
     */
    public PeptideObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public PeptideObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a Peptide.
     * 
     * @param obj   the object to check
     * @return true, if obj is a Peptide
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof Peptide);
    }

    /**
     * Checks, if the Peptide is referenced by a PeptideEvidence.
     * 
     * @param pept the Peptide element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Peptide pept) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();
        
        if (!PeptideEvidenceObjectRule.peptideRefSet.contains(pept.getId())) {
            messages.add(new ValidatorMessage("The peptide with ID " + pept.getId() + " is not referenced by a PeptideEvidence element at" + 
                PeptideObjectRule.PEPTIDE_CONTEXT.getContext(),
                MessageLevel.WARN, PeptideObjectRule.PEPTIDE_CONTEXT, this));
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

        ret.add("Reported peptides SHOULD be referenced by at least one PeptideEvidence element.");
        
        return ret;
    }
}
