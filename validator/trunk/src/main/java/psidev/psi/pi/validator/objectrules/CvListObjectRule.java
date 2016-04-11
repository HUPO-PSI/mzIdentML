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
import uk.ac.ebi.jmzidml.model.mzidml.Cv;
import uk.ac.ebi.jmzidml.model.mzidml.CvList;

/**
 * Check if there is a regular expression in the cvParam 'modification rescoring:false localization rate'.
 * 
 * @author Gerhard
 * 
 */
public class CvListObjectRule extends AObjectRule<CvList> {

    /**
     * Constants.
     */
    private static final Context CV_CONTEXT = new Context(MzIdentMLElement.CvList.getXpath());

    /**
     * Constructor.
     */
    public CvListObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public CvListObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a CvList.
     * 
     * @param obj   the object to check
     * @return true, if obj is a CvList
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof CvList);
    }

    /**
     * 
     * @param cvList the CvList element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(CvList cvList) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        String version, id;
        for (Cv cv: cvList.getCv()) {
            version = cv.getVersion();
            
            id = cv.getId();
            switch (id) {
                case "MOD":
                    if (version.compareTo("1.013.0") < 0) {
                        messages.add(this.getValidatorMsg(id));
                    }
                    break;
                case "PSI-MS":
                case "MS":
                    if (version.compareTo("3.73.0") < 0) {
                        messages.add(this.getValidatorMsg(id));
                    }
                    break;
                // TODO: add other possible ontologies here / update when new schemas are out
            }
        }

        return messages;
    }

   /**
     * Gets the validator message for a CV term.
     * @param cvID
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorMsg(String cvID) {
        String strB = "The cv element for " + cvID + " uses an old version.";
        
        return new ValidatorMessage(strB, MessageLevel.INFO, CvListObjectRule.CV_CONTEXT, this);        
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Provide the newest version for all cv element under the cvList element." + CvListObjectRule.CV_CONTEXT.getContext());

        return ret;
    }
}
