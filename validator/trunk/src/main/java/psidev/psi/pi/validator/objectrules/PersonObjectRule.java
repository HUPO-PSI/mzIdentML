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
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.Person;

/**
 * Check if there is a regular expression in the cvParam 'modification rescoring:false localization rate'.
 * 
 * @author Gerhard
 * 
 */
public class PersonObjectRule extends AObjectRule<Person> {

    /**
     * Constants.
     */
    private static final Context PERSON_CONTEXT = new Context(MzIdentMLElement.Person.getXpath());

    /**
     * Constructor.
     */
    public PersonObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public PersonObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a Person.
     * 
     * @param obj   the object to check
     * @return true, if obj is a Person
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof Person);
    }

    /**
     * 
     * @param pers the Person element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Person pers) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        String cvValue;
        for (CvParam cv: pers.getCvParam()) {
            cvValue = cv.getValue();
            
            if (cvValue != null) {
                switch (cv.getAccession()) {
                    case "MS:1000587":  // contact address
                        if (cv.getValue().isEmpty()) {
                            messages.add(this.getValidatorMsg(pers, "contact address"));
                        }
                        break;
                    case "MS:1000588":  // contact URL
                        if (cv.getValue().isEmpty()) {
                            messages.add(this.getValidatorMsg(pers, "contact URL"));
                        }
                        break;
                    case "MS:1000589":  // contact email
                        if (cv.getValue().isEmpty()) {
                            messages.add(this.getValidatorMsg(pers, "contact email"));
                        }
                        break;
                }
            }
            else {
                messages.add(this.getValidatorMsg(pers, "Value is missing for CV: " + cv.getName()));
            }
        }

        return messages;
    }

   /**
     * Gets the validator message for a CV term.
     * @param pers
     * @param cvName
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorMsg(Person pers, String cvName) {
        StringBuilder strB = new StringBuilder("The '");
        strB.append(cvName).append("' cvParam in the Person (id='");
        strB.append(pers.getId()).append("') element at ").append(PersonObjectRule.PERSON_CONTEXT.getContext()).append(" has an empty value.");
        
        return new ValidatorMessage(strB.toString(), MessageLevel.WARN, PersonObjectRule.PERSON_CONTEXT, this);        
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Provide a value for all CV terms under the Person element.");

        return ret;
    }
}
