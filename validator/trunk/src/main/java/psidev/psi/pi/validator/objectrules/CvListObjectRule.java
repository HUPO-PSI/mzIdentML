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
     * Checks the CvList element.
     * @param cvList the CvList element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(CvList cvList) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        this.checkCvVersion(cvList, messages);
        this.checkCvURI(cvList, messages);

        return messages;
    }

    /**
     * Checks the verison of the CV's.
     * @param cvList the CvList element
     * @param messages list of ValidatorMessage's
     */
    private void checkCvVersion(CvList cvList, List<ValidatorMessage> messages) {
        String version, id;
        
        for (Cv cv: cvList.getCv()) {
            version = cv.getVersion();
            
            if (version != null) {
                id = cv.getId();
                switch (id) {
                    case "MOD":
                    case "PSI-MOD":
                        if (version.compareTo("1.013.0") < 0) {
                            messages.add(this.getValidatorVersionMsg(id));
                        }
                        break;
                    case "MS":
                    case "PSI-MS":
                        if (version.compareTo("4.0.7") < 0) {
                            messages.add(this.getValidatorVersionMsg(id));
                        }
                        break;
                    case "PATO":
                        if (version.compareTo("releases/2017-01-26") < 0) {
                            messages.add(this.getValidatorVersionMsg(id));
                        }
                        break;
                    case "UNIMOD":
                        if (version.compareTo("2016:09:23 13:49") < 0) {
                            messages.add(this.getValidatorVersionMsg(id));
                        }
                        break;
                    case "UO":
                    case "UNIT":
                        break;
                    case "XLMOD":
                        if (version.compareTo("releases/2016-07-08") < 0) {
                            messages.add(this.getValidatorVersionMsg(id));
                        }
                        break;
                    // TODO: add other possible ontologies here / update when new schemas are out of date
                }
            }
        }
    }
    
    /**
     * Checks the URI of the CV's.
     * @param cvList the CvList element
     * @param messages list of ValidatorMessage's
     */
    private void checkCvURI(CvList cvList, List<ValidatorMessage> messages) {
        String uri, id;
        
        for (Cv cv: cvList.getCv()) {
            uri = cv.getUri();
            
            if (uri != null) {
                id = cv.getId();
                switch (id) {
                    case "MOD":
                    case "PSI-MOD":
                    case "MS":
                    case "PSI-MS":
                    case "XLMOD":
                    case "PATO":
                    case "UO":
                    case "UNIT":
                        if (!uri.startsWith("https://raw.githubusercontent.com")) {
                            messages.add(this.getValidatorURIMsg(id));
                        }
                        break;
                    case "UNIMOD":
                        if (!uri.equals("http://www.unimod.org/obo/unimod.obo")) {
                            messages.add(this.getValidatorURIMsg(id));
                        }
                        break;
                    // TODO: add other possible ontologies here / update when new schemas are out
                }
            }
        }
    }
    
   /**
     * Gets the validator message for the CV version.
     * @param cvID
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorVersionMsg(String cvID) {
        String strB = "The cv element for " + cvID + " uses an old version.";
        
        return new ValidatorMessage(strB, MessageLevel.INFO, CvListObjectRule.CV_CONTEXT, this);        
    }
    
   /**
     * Gets the validator message for the CV URI.
     * @param cvID
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorURIMsg(String cvID) {
        String strB = "The cv element for " + cvID + " uses an old or wrong URI.";
        
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

        ret.add("Provide the newest version for all cv element under the CvList element." + CvListObjectRule.CV_CONTEXT.getContext());

        return ret;
    }
}
