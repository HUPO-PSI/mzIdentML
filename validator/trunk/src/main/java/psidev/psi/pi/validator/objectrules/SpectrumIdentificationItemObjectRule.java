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
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;

/**
 * Checks: if there is a calculatedMassToCharge in the
 * SpectrumIdentificationItem.
 * 
 * @author Salva
 * 
 */
public class SpectrumIdentificationItemObjectRule extends AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constants.
     */
    private static final Context SII_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationItem.getXpath());

    /**
     * Members.
     */
    private boolean bMissingCalculatedMassToChargeRatio         = false;
    private boolean bInconsistentPeptideAndPeptideEvidenceRefs  = false;
    
    /**
     * Constructor.
     */
    public SpectrumIdentificationItemObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SpectrumIdentificationItemObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Resets all member variables.
     */
    private void resetMembers() {
        this.bMissingCalculatedMassToChargeRatio= false;
        this.bInconsistentPeptideAndPeptideEvidenceRefs = false;
    }
    
    /**
     * Checks, if the object is a SpectrumIdentificationItem.
     * 
     * @param obj   the object to check
     * @return true, if obj is an SpectrumIdentificationItem
     */
    @Override
    public boolean canCheck(Object obj) {
        boolean bRet = obj instanceof SpectrumIdentificationItem;
        
        if (bRet) {
            this.resetMembers();
        }
        
        return bRet;
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
        String siiID = spectrumIdentificationItem.getId();

        // check for calculated m/z ratio
        if (spectrumIdentificationItem.getCalculatedMassToCharge() == null) {
            this.bMissingCalculatedMassToChargeRatio = true;
            messages.add(new ValidatorMessage(
                "There is not a definition of the calculatedMassToCharge in the SpectrumIdentificationItem (id='"
                + siiID + "') element at "
                + SpectrumIdentificationItemObjectRule.SII_CONTEXT.getContext() + " or the value is 0.0", MessageLevel.WARN, SII_CONTEXT, this));
        }
        
        // check for matching peptide_ref and PeptideEvidence
        List<PeptideEvidenceRef> pevList = spectrumIdentificationItem.getPeptideEvidenceRef();
        String peptideRef = spectrumIdentificationItem.getPeptideRef();
        
        for (PeptideEvidenceRef pevRef : pevList) {
            String pevID = pevRef.getPeptideEvidenceRef();
            
            if (PeptideEvidenceObjectRule.peptideRef2PeptideEvidenceIDMap.containsKey(peptideRef)) {
                if (PeptideEvidenceObjectRule.peptideRef2PeptideEvidenceIDMap.get(peptideRef).equals(pevID)) {
                    break;
                }
            }
            else {
                this.bInconsistentPeptideAndPeptideEvidenceRefs = true;
                messages.add(new ValidatorMessage(
                    "There is no PeptideEvidenceRef element that matches the peptide referenced by peptide_ref in the SpectrumIdentificationItem (id='"
                        + siiID + "') element at "
                        + SpectrumIdentificationItemObjectRule.SII_CONTEXT.getContext(), MessageLevel.ERROR, SII_CONTEXT, this));
            }
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

        if (this.bMissingCalculatedMassToChargeRatio) {
            ret.add("Add the attribute 'calculatedMassToCharge' to the SpectrumIdentificationItem and set a value > 0.0");
        }

        if (this.bInconsistentPeptideAndPeptideEvidenceRefs) {
            ret.add("Check the SpectrumIdentificationItem element for consistent references for Peptides and PeptideEvidences.");
        }
        
        return ret;
    }
}
