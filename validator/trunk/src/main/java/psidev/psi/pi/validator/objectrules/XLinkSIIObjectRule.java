package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationResult;

/**
 * Checks, if for each CV term MS:1002511 - 'cross-link spectrum identification
 * item' there exists the same CV term with the same chargeState and the same
 * value in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.
 * For isotope labelled linkers we can have the double number of SIIs for the
 * same cross-link, e.g. 4 instead of 2. But for different chargeStates the value
 * of the SIIs must be different (see Feature D in Figure 3 of the spec doc).
 * 
 * Remark: The number of SIIs with the same value and the same chargeState
 *         for the same cross-link must be 2 or 4 (for isotope labelled linkers).
 *         For different chargeStates the value of the SIIs must be different.
 * 
 * @author Gerhard
 * 
 */
public class XLinkSIIObjectRule extends AObjectRule<SpectrumIdentificationResult> {

    /**
     * Constants.
     */
    private static final Context SIR_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationResult.getXpath());
    
    /**
     * Members.
     */
    // mapping from sirID and cvValue to the SII-ID list
    private static HashMap<ImmutablePair<String, String>, ArrayList<String>> XL_SIRID_AND_CVVALUE2SII_IDLISTMAP = null;
    // mapping from sirID and cvValue to the chargeState list
    private static HashMap<ImmutablePair<String, String>, ArrayList<Integer>> XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP = null;

    /**
     * Constructor.
     */
    public XLinkSIIObjectRule() {
        this(null);
        
        XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP = new HashMap<>();
        XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP = new HashMap<>();
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public XLinkSIIObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
        
        XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP = new HashMap<>();
        XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP = new HashMap<>();
    }

    /**
     * Checks, if the object is a SpectrumIdentificationResult.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationResult
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationResult);
    }

    /**
     * Checks, if for each CV term MS:1002511 - 'cross-link spectrum identification item' there exists the same CV term
     * with the same value in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.
     * 
     * @param sir the SpectrumIdentificationResult element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationResult sir) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            String sirID = sir.getId();
            
            ImmutablePair<String, String> sirID_CvValue_key;
            for (SpectrumIdentificationItem sii: sir.getSpectrumIdentificationItem()) {
                for (CvParam cv: sii.getCvParam()) {
                    if (cv != null) {
                        switch (cv.getAccession()) {
                            case "MS:1002511":  // cross-link spectrum identification item
                                String cvValue = cv.getValue();

                                if (cvValue.isEmpty()) {
                                    messages.add(new ValidatorMessage("The '" + cv.getName()
                                        + "' cvParam in the SpectrumIdentificationItem (id='" + sii.getId() + "') element at "
                                        + XLinkSIIObjectRule.SIR_CONTEXT.getContext() + " has an empty value.",
                                        MessageLevel.WARN, XLinkSIIObjectRule.SIR_CONTEXT, this));
                                }

                                // fill the first map
                                sirID_CvValue_key = new ImmutablePair<>(sirID, cvValue);
                                if (!XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.containsKey(sirID_CvValue_key)) {
                                    XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.put(sirID_CvValue_key, new ArrayList<>());
                                }
                                XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.get(sirID_CvValue_key).add(sii.getId());

                                // fill the second map
                                sirID_CvValue_key = new ImmutablePair<>(sirID, cvValue);
                                if (!XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP.containsKey(sirID_CvValue_key)) {
                                    XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP.put(sirID_CvValue_key, new ArrayList<>());
                                }
                                if (!XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP.get(sirID_CvValue_key).contains(sii.getChargeState())) {
                                    XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP.get(sirID_CvValue_key).add(sii.getChargeState());
                                }
                                break;
                        }
                    }
                }
            }
        }

        return messages;
    }

    /**
     * Checks the rules by access to the HashMap.
     * @return collection of messages
     */
    public static Collection<ValidatorMessage> checkRulesWithHashMapContent() {
        List<ValidatorMessage> messages = new ArrayList<>();
        ValidatorMessage valMsg;
        
        if (XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.isEmpty()) {
            valMsg = new ValidatorMessage("No CV terms MS:1002511 - 'cross-link spectrum identification item' found for a cross-linking file."
                + XLinkSIIObjectRule.SIR_CONTEXT.getContext(),
                MessageLevel.ERROR);
            messages.add(valMsg);
        }
        else {
            for (ImmutablePair<String, String> sirID_CvValue_key: XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.keySet()) {
                String cvValue = sirID_CvValue_key.right;
                ArrayList<Integer> chargeStateList = XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2CHARGESTATELISTMAP.get(sirID_CvValue_key);
                
                if (chargeStateList.size() > 1) {
                    valMsg = new ValidatorMessage("The cvParam's MS:1002511 with the value " + cvValue + " are used in SpectrumIdentificationItem's with " +
                        chargeStateList.size() + " different charge states", MessageLevel.ERROR);
                    messages.add(valMsg);
                }
                else {
                    ArrayList<String> siiIdList = XLinkSIIObjectRule.XL_SIRID_AND_CVVALUE2SII_IDLISTMAP.get(sirID_CvValue_key);
                
                    if (siiIdList.size() == 1) {
                        valMsg = new ValidatorMessage("The cvParam MS:1002511 with value " + cvValue +
                            " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at " + XLinkSIIObjectRule.SIR_CONTEXT.getContext() +
                            " has no corresponding cvParam in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.",
                            MessageLevel.ERROR);
                        messages.add(valMsg);
                    }
                    else if (siiIdList.size() !=2 && siiIdList.size() != 4) {
                        valMsg = new ValidatorMessage("The cvParam's MS:1002511 with value " + cvValue +
                            " in the SpectrumIdentificationItem " + XLinkSIIObjectRule.SIR_CONTEXT.getContext() +
                            " occurs not paired (" + siiIdList.size() + "times), i.e. 2 times resp. 4 times for isotope labelled linkers.",
                            MessageLevel.ERROR);
                        messages.add(valMsg);
                    }
                    else {
                        if (siiIdList.size() == 2 && siiIdList.get(0).equals(siiIdList.get(1))) {
                            valMsg = new ValidatorMessage("The cvParam's MS:1002511 with value " + cvValue +
                                " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at " + XLinkSIIObjectRule.SIR_CONTEXT.getContext() +
                                " occurs at the same SpectrumIdentificationItem, but must occur in different SpectrumIdentificationItem's of the same SpectrumIdentificationResult.",
                                MessageLevel.ERROR);
                            messages.add(valMsg);
                        }
                        else if (siiIdList.size() == 4 &&
                                siiIdList.get(0).equals(siiIdList.get(1)) &&
                                siiIdList.get(1).equals(siiIdList.get(2)) &&
                                siiIdList.get(2).equals(siiIdList.get(3))) {
                            valMsg = new ValidatorMessage("The cvParam's MS:1002511 with value " + cvValue +
                                " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at " + XLinkSIIObjectRule.SIR_CONTEXT.getContext() +
                                " occurs at the same SpectrumIdentificationItem, but must occur in different SpectrumIdentificationItem's of the same SpectrumIdentificationResult.",
                                MessageLevel.ERROR);
                            messages.add(valMsg);
                        }
                    }
                }
            }
        }
        
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

        ret.add("Provide the term MS:1002511 - 'cross-link spectrum identification item' at two different SpectrumIdentificationItems of the same SpectrumIdentificationResult.");

        return ret;
    }
}
