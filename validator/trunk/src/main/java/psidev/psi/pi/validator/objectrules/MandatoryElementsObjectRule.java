package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;

/**
 * This class is for displaying a validator FATAL message when an exception is thrown by the PSI xml parser.
 * This rule is not in the list of object rules to execute.
 */

public class MandatoryElementsObjectRule extends AObjectRule<Object> {
    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public MandatoryElementsObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);

        this.setName("Mandatory element check");

        this.setDescription("Check that some elements in the xml file are present or not.");

        this.addTip("Add the element in the appropriate location.");
    }

    /**
     * Checks, if the object is a Object.
     * 
     * @param obj   the object to check
     * @return boolean
     */
    @Override
    public boolean canCheck(Object obj) {
        return true;
    }

    /**
     * Checks the Object.
     * @param obj   the object to check
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Object obj) throws ValidatorException {
        return new ArrayList<>();
    }
}
