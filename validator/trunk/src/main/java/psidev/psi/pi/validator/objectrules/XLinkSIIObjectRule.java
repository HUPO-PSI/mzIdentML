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
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationResult;

/**
 * Checks, if for each CV term MS:1002511 - 'cross-link spectrum identification item' there exists the same CV term
 * with the same value in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.
 * For isotope labelled linker we have 4 SIIs for the same cross-link .
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
    private static HashMap<String, ArrayList<String>> XL_CVVALUE2SII_IDLISTMAP = null;

    /**
     * Constructor.
     */
    public XLinkSIIObjectRule() {
        this(null);
        
        XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP = new HashMap<>();
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public XLinkSIIObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
        
        XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP = new HashMap<>();
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
            ValidatorMessage valMsg;
            for (SpectrumIdentificationItem sii: sir.getSpectrumIdentificationItem()) {
                for (CvParam cv: sii.getCvParam()) {
                    switch (cv.getAccession()) {
                        case "MS:1002511":  // cross-link spectrum identification item
                            String cvValue = cv.getValue();

                            if (cvValue.isEmpty()) {
                                valMsg = new ValidatorMessage("The '" + cv.getName()
                                    + "' cvParam in the SpectrumIdentificationItem (id='" + sii.getId() + "') element at "
                                    + XLinkSIIObjectRule.SIR_CONTEXT.getContext() + " has an empty value.",
                                    MessageLevel.WARN, XLinkSIIObjectRule.SIR_CONTEXT, this);
                                messages.add(valMsg);
                            }

                            // fill data to map
                            if (!XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.containsKey(cvValue)) {
                                XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.put(cvValue, new ArrayList<>());
                            }
                            XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.get(cvValue).add(sii.getId());

                            break;
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
        
        if (XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.isEmpty()) {
            valMsg = new ValidatorMessage("No CV terms MS:1002511 - 'cross-link spectrum identification item' found for a cross-linking file."
                + XLinkSIIObjectRule.SIR_CONTEXT.getContext(),
                MessageLevel.ERROR);
            messages.add(valMsg);
        }
        else {
            for (String cvValue: XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.keySet()) {
                ArrayList<String> siiIdList = XLinkSIIObjectRule.XL_CVVALUE2SII_IDLISTMAP.get(cvValue);

                if (siiIdList.size() == 1) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam with value " + cvValue + " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at "
                        + XLinkSIIObjectRule.SIR_CONTEXT.getContext() + " has no corresponding cvParam in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.",
                        MessageLevel.ERROR);
                    messages.add(valMsg);
                }
                else if (siiIdList.size() > 2 && siiIdList.size() != 4) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam's with value " + cvValue + " in the SpectrumIdentificationItem "
                        + XLinkSIIObjectRule.SIR_CONTEXT.getContext() + " occurs not paired (" + siiIdList.size() + "times), i.e. EXCATLY 2 times resp. 4 times for isotope labelled linkers.",
                        MessageLevel.ERROR);
                    messages.add(valMsg);
                }
                else if (siiIdList.get(0).equals(siiIdList.get(1))) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam's with value " + cvValue + " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at "
                        + XLinkSIIObjectRule.SIR_CONTEXT.getContext() + " occur at the same SpectrumIdentificationItem, but must occur in different SpectrumIdentification items of the same SpectrumIdentificationResult.",
                        MessageLevel.ERROR);
                    messages.add(valMsg);
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

        ret.add("Provide the term MS:1002511 - 'cross-link spectrum identification item' at two different SpectrumIdentificationItems of the same SpectrumIdentificationResult" + XLinkSIIObjectRule.SIR_CONTEXT.getContext());

        return ret;
    }
}
