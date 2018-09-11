package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationList;

/**
 * Check if there SpectrumIdentificationList is a "final PSM lists".
 * Remark: This object rule is DEACTIVATED, since not listed in ObjectRules.1.2.0.xml and ObjectRulesMIAPE.1.2.0.xml
 * @author Gerhard Mayer
 */
public class SILCheckForFinalPSMListRule extends AObjectRule<SpectrumIdentificationList> {

    /**
     * Constructors.
     */
    public SILCheckForFinalPSMListRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SILCheckForFinalPSMListRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationList.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationList
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationList);
    }

    /**
     * Checks, if the SpectrumIdentificationList is a final one.
     * 
     * @param sil the SpectrumIdentificationList element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationList sil) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        for (CvParam cv: sil.getCvParam()) {
            if (cv != null) {
                if (cv.getAccession().equals("MS:1002439")) { // final PSM list
                    SIRUniqueSpectrumIDSpectrumRefCombinationRule.bIsFinalPSMList = true;
                    return messages;
                }
            }
        }
        
        SIRUniqueSpectrumIDSpectrumRefCombinationRule.bIsFinalPSMList = false;
        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        return ret;
    }
}
