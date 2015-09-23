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
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Peptide;

/**
 * Checks, if the CV terms MS:1002509 - 'cross-link donor' and MS:1002510 - 'cross-link receiver' are paired with the same value in two different Peptide Modifications.
 * 
 * @author Gerhard
 * 
 */
public class XLinkPeptideModificationObjectRule extends AObjectRule<Peptide> {

    /**
     * Constants.
     */
    private static final Context PeptideContext = new Context(MzIdentMLElement.Peptide.getXpath());
    private static final String ACC_XL_DONOR   = "MS:1002509";
    private static final String ACC_XL_RECEIVER= "MS:1002510";
    private final static HashMap<String, HashMap<String, String>> xlCvValue2CvAccession2PeptIDMap = new HashMap<>();

    /**
     * Constructor.
     */
    public XLinkPeptideModificationObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager 
     */
    public XLinkPeptideModificationObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a Peptide.
     * 
     * @param obj
     * @return true, if obj is a Peptide
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof Peptide);
    }

    /**
     * Checks, if there are cross-link donor and cross-link receiver and if the
     * CV terms MS:1002509 - 'cross-link donor' and MS:1002510 - 'cross-link receiver' are paired with the same value in two different Peptide Modifications
     * 
     * @param pept
     * @return Collection<>
     * @throws ValidatorException 
     */
    @Override
    public Collection<ValidatorMessage> check(Peptide pept) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();
        
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            for (Modification mod: pept.getModification()) {
                for (CvParam cv: pept.getCvParam()) {
                    switch (cv.getAccession()) {
                        case ACC_XL_DONOR:      // cross-link donor
                            this.checkForEmptyCVValueAndFillMap(pept, mod, cv, messages);
                            break;
                        case ACC_XL_RECEIVER:   // cross-link receiver
                            this.checkForEmptyCVValueAndFillMap(pept, mod, cv, messages);
                            break;
                    }
                }
            }
        }
        
        return messages;
    }

    /**
     * Checks, if the CV value is empty and fills the data map.
     * @param pept
     * @param mod
     * @param cv
     * @param messages 
     */
    private void checkForEmptyCVValueAndFillMap(Peptide pept, Modification mod, CvParam cv, List<ValidatorMessage> messages) {
        String cvValue = cv.getValue();
        
        // check for empty CV value
        ValidatorMessage valMsg;
        if (cvValue.isEmpty()) {
            valMsg = new ValidatorMessage("The '" + cv.getName()
                + "' cvParam in the Modification location='" + mod.getLocation() + "' of Peptide (id='" + pept.getId() + "') element at "
                + XLinkPeptideModificationObjectRule.PeptideContext.getContext() + " has an empty value.",
                MessageLevel.WARN, XLinkPeptideModificationObjectRule.PeptideContext, this);
            messages.add(valMsg);
        }
        
        // fill data to map
        if (!XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.containsKey(cvValue)) {
            XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.put(cvValue, new HashMap<String, String>());
        }
        XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.get(cvValue).put(cv.getAccession(), pept.getId());
    }
    
    /**
     * Checks the rules by access to the HashMap.
     * @return Collection<>
     */
    public static Collection<ValidatorMessage> checkRulesWithHashMapContent() {
        List<ValidatorMessage> messages = new ArrayList<>();
        
        if (XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.isEmpty()) {
            ValidatorMessage valMsg = new ValidatorMessage("No cross-linked modified peptides donors (MS:1002509) / receivers (MS:1002510) are found for a cross-linking file "
                + XLinkPeptideModificationObjectRule.PeptideContext.getContext(),
                MessageLevel.ERROR);
            messages.add(valMsg);
        }
        else {
            XLinkPeptideModificationObjectRule.checkForPairedDonorReceiverPairs(messages);
        }
        
        return messages;
    }
        
    /**
     * Checks, if 'cross-link donor' and 'cross-link receiver' are always paired correctly.
     * @param messages 
     */
    private static void checkForPairedDonorReceiverPairs(Collection<ValidatorMessage> messages) {
        ValidatorMessage valMsg;
        
        for (String cvVal: XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.keySet()) {
            HashMap<String, String> acc2PeptIDMap = XLinkPeptideModificationObjectRule.xlCvValue2CvAccession2PeptIDMap.get(cvVal);

            if (acc2PeptIDMap.size() == 2) { 
                int cntDonor = 0;
                int cntReceiver = 0;
                for (String acc: acc2PeptIDMap.keySet()) {
                    switch (acc2PeptIDMap.get(acc)) {
                        case ACC_XL_DONOR:
                            cntDonor++;
                            break;
                        case ACC_XL_RECEIVER:
                            cntReceiver++;
                            break;
                    }
                }
                
                if (cntDonor == 0) {
                    valMsg = new ValidatorMessage("Cross-link donors with the value " + cvVal
                        + " is missing in Peptides with ID's " + XLinkPeptideModificationObjectRule.getPeptideIDList(acc2PeptIDMap)
                        + XLinkPeptideModificationObjectRule.PeptideContext.getContext(),
                        MessageLevel.ERROR, XLinkPeptideModificationObjectRule.PeptideContext, null);
                    messages.add(valMsg);
                }
                else if (cntReceiver == 0) {
                    valMsg = new ValidatorMessage("Cross-link receiver with the value " + cvVal
                        + " is missing in Peptides with ID's " + XLinkPeptideModificationObjectRule.getPeptideIDList(acc2PeptIDMap)
                        + XLinkPeptideModificationObjectRule.PeptideContext.getContext(),
                        MessageLevel.ERROR, XLinkPeptideModificationObjectRule.PeptideContext, null);
                    messages.add(valMsg);
                }
            }
            else {
                valMsg = new ValidatorMessage("There must be exactly one pair of 'cross-link donor' and 'cross-link receiver' with the value " + cvVal
                    + " in Peptides with ID's " + XLinkPeptideModificationObjectRule.getPeptideIDList(acc2PeptIDMap)
                    + XLinkPeptideModificationObjectRule.PeptideContext.getContext(),
                    MessageLevel.ERROR, XLinkPeptideModificationObjectRule.PeptideContext, null);
                messages.add(valMsg);
            }
        }
    }

    /**
     * Gets the list of Peptide ID's.
     * @param acc2PeptIDMap
     * @return String
     */
    private static String getPeptideIDList(HashMap<String, String> acc2PeptIDMap) {
        StringBuilder strB = new StringBuilder();
        
        boolean bFirst = true;
        for (String acc: acc2PeptIDMap.keySet()) {
            if (!bFirst) {
                strB.append(", ");
                bFirst = false;
            }
            strB.append(acc2PeptIDMap.get(acc));
        }
        
        return strB.toString();
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return Collection<>
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("The CV terms MS:1002509 - 'cross-link donor' and MS:1002510 - 'cross-link receiver' must be paired with the same value in two different Peptide Modifications"
                + XLinkPeptideModificationObjectRule.PeptideContext.getContext());

        return ret;
    }
}
