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
 * 
 * @author Gerhard
 * 
 */
public class XLinkSIIObjectRule extends AObjectRule<SpectrumIdentificationResult> {

    /**
     * Constants.
     */
    private static final Context SIRContext = new Context(MzIdentMLElement.SpectrumIdentificationResult.getXpath());
    private final static HashMap<String, ArrayList<String>> xlCvValue2SiiIdListMap = new HashMap<>();

    /**
     * Constructor.
     */
    public XLinkSIIObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager 
     */
    public XLinkSIIObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationResult.
     * 
     * @param obj
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
     * @param sir
     * @return Collection<>
     * @throws ValidatorException 
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
                                    + XLinkSIIObjectRule.SIRContext.getContext() + " has an empty value.",
                                    MessageLevel.WARN, XLinkSIIObjectRule.SIRContext, this);
                                messages.add(valMsg);
                            }

                            // fill data to map
                            if (!XLinkSIIObjectRule.xlCvValue2SiiIdListMap.containsKey(cvValue)) {
                                XLinkSIIObjectRule.xlCvValue2SiiIdListMap.put(cvValue, new ArrayList<String>());
                            }
                            XLinkSIIObjectRule.xlCvValue2SiiIdListMap.get(cvValue).add(sii.getId());

                            break;
                    }
                }
            }
        }

        return messages;
    }

    /**
     * Checks the rules by access to the HashMap.
     * @return Collection<>
     */
    public static Collection<ValidatorMessage> checkRulesWithHashMapContent() {
        List<ValidatorMessage> messages = new ArrayList<>();
        ValidatorMessage valMsg;
        
        if (XLinkSIIObjectRule.xlCvValue2SiiIdListMap.isEmpty()) {
            valMsg = new ValidatorMessage("No CV terms MS:1002511 - 'cross-link spectrum identification item' found for a cross-linking file."
                + XLinkSIIObjectRule.SIRContext.getContext(),
                MessageLevel.ERROR);
            messages.add(valMsg);
        }
        else {
            for (String cvValue: XLinkSIIObjectRule.xlCvValue2SiiIdListMap.keySet()) {
                ArrayList<String> siiIdList = XLinkSIIObjectRule.xlCvValue2SiiIdListMap.get(cvValue);

                if (siiIdList.size() == 1) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam with value " + cvValue + " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at "
                        + XLinkSIIObjectRule.SIRContext.getContext() + " has no corresponding cvParam in another SpectrumIdentificationItem of the same SpectrumIdentificationResult.",
                        MessageLevel.ERROR);
                    messages.add(valMsg);
                }
                else if (siiIdList.size() > 2) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam's with value " + cvValue + " in the SpectrumIdentificationItem "
                        + XLinkSIIObjectRule.SIRContext.getContext() + " occur more than 2 times, but must occur paired, i.e. EXCATLY 2 times.",
                        MessageLevel.ERROR);
                    messages.add(valMsg);
                }
                else if (siiIdList.get(0).equals(siiIdList.get(1))) {
                    valMsg = new ValidatorMessage("The '"
                        + "' cvParam's with value " + cvValue + " in the SpectrumIdentificationItem (id='" + siiIdList.get(0) + "') element at "
                        + XLinkSIIObjectRule.SIRContext.getContext() + " occur at the same SpectrumIdentificationItem, but must occur in different SpectrumIdentification items of the same SpectrumIdentificationResult.",
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
     * @return Collection<>
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Provide the term MS:1002511 - 'cross-link spectrum identification item' at two different SpectrumIdentificationItems of the same SpectrumIdentificationResult" + XLinkSIIObjectRule.SIRContext.getContext());

        return ret;
    }
}
