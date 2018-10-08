package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import psidev.psi.pi.validator.objectrules.util.OBOFileReader;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.Param;

/**
 * Checks the CVParams of ParamGroups.
 *
 * @author Gerhard
 * 
 */
public class ParamObjectRule extends AObjectRule<Param> {

    /**
     * Constants.
     */
    private static final Context PARAM_CONTEXT = new Context(MzIdentMLElement.Param.getXpath());
    
    /**
     * Members.
     */
    private boolean bEmptyAccession     = false;
    private boolean bWrongCvTermName    = false;
    private boolean bMissingCvTermValue = false;
    
    /**
     * Constructor.
     */
    public ParamObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ParamObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Resets all member variables.
     */
    private void resetMembers() {
        this.bEmptyAccession    = false;
        this.bWrongCvTermName   = false;
        this.bMissingCvTermValue= false;
    }
    
    /**
     * Checks, if the object is a Param.
     * 
     * @param obj   the object to check
     * @return true, if obj is a Param
     */
    @Override
    public boolean canCheck(Object obj) {
        boolean bRet = obj instanceof Param;
        
        if (bRet) {
            this.resetMembers();
        }
        
        return bRet;
    }

    /**
     * Checks, if the CVParams of ParamGroups.
     * 
     * @param param the Param element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Param param) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();
        
        CvParam cvParam = param.getCvParam();
        if (cvParam!= null) {
            String acc = cvParam.getAccession();
            String cvName = cvParam.getName();

            // checks for empty accession number
            if (acc.isEmpty()) {
                messages.add(this.getEmptyAccessionMsg(cvName));
            }
            else {
                int pos = acc.indexOf(":");
                String ontID = acc.substring(0, pos);
                String termID = acc.substring(pos + 1);

                // checks if the CV term name is valid
                if (!OBOFileReader.isValidCVTermName(ontID, termID, cvName)) {
                    messages.add(this.getWrongCVTermNameMsg(cvParam, ontID, termID));
                }

                // checks if the CV term has not a value, when it should have one
                if (!OBOFileReader.hasCVTermAValue(ontID, termID)) {
                    messages.add(this.getMissingCVTermValueMsg(cvParam));
                }
            }
        }
        
        return messages;
    }

   /**
     * Gets the validator message for an empty CV accession.
     * @param cvName
     * @return the ValidatorMessage
     */
    private ValidatorMessage getEmptyAccessionMsg(String cvName) {
        this.bEmptyAccession = true;
        String strB = "The cvParam for " + cvName + " has an empty accession.";
        
        return new ValidatorMessage(strB, MessageLevel.ERROR, ParamObjectRule.PARAM_CONTEXT, this);        
    }
    
   /**
     * Gets the validator message for a wrong CV term name.
     * @param cvParam
     * @param ontID     the ontology ID
     * @param termID    the CV term ID
     * @return the ValidatorMessage
     */
    private ValidatorMessage getWrongCVTermNameMsg(CvParam cvParam, String ontID, String termID) {
        this.bWrongCvTermName = true;
        String strB = "A cvParam for " + cvParam.getAccession() + " has an invalid name: " + cvParam.getName()+ ". It should be: " + OBOFileReader.getCVTermNameFromID(ontID, termID);
        
        return new ValidatorMessage(strB, MessageLevel.ERROR, ParamObjectRule.PARAM_CONTEXT, this);        
    }
    
   /**
     * Gets the validator message for a wrong CV term name.
     * @param cvParam
     * @return the ValidatorMessage
     */
    private ValidatorMessage getMissingCVTermValueMsg(CvParam cvParam) {
        this.bMissingCvTermValue = true;
        String strB = "A cvParam for " + cvParam.getAccession() + " has a missing value where it should have one. ";
        
        return new ValidatorMessage(strB, MessageLevel.ERROR, ParamObjectRule.PARAM_CONTEXT, this);        
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return Collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        if (this.bEmptyAccession) {
            ret.add("Check the CV terms under ParamGroups for an empty accession.");
        }
        if (this.bWrongCvTermName) {
            ret.add("Check the CV terms under ParamGroups for wrong or outdated term name.");
        }
        if (this.bMissingCvTermValue) {
            ret.add("Check the CV terms under ParamGroups for missing values.");
        }
        
        return ret;
    }
}
