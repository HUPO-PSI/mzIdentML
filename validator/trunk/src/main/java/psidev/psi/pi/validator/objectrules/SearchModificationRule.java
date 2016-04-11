package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.impl.OntologyTermImpl;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.ontology_manager.interfaces.OntologyTermI;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.SearchModification;

/**
 * Checks if any modification of the peptide comes from a child of the following
 * terms:'UNIMOD:0','MOD:00000','MS:1001471' (peptide modification details)
 * 
 * @author Salva
 * 
 */
public class SearchModificationRule extends AObjectRule<uk.ac.ebi.jmzidml.model.mzidml.SearchModification> {

    private static final Context SEARCHMOD_CONTEXT = new Context(MzIdentMLElement.SearchModification.getXpath());

    // Another constructor that calls to ObjectRule
    public SearchModificationRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SearchModification.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SearchModification
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SearchModification);
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of messages
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Include just child term of the following terms: 'UNIMOD:0','MOD:00000','MS:1001471'");
        
        return ret;
    }

    /**
     * 
     * @param searchModification the SearchModification element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SearchModification searchModification) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        final OntologyAccess msOntology = ontologyManager.getOntologyAccess("MS");
        final OntologyAccess modOntology = ontologyManager.getOntologyAccess("MOD");
        final OntologyAccess unimodOntology = ontologyManager.getOntologyAccess("UNIMOD");

        List<CvParam> cvParams = searchModification.getCvParam();
        for (CvParam cvParam : cvParams) {
            // check in MS ontology
            Set<OntologyTermI> allParents = msOntology
                            .getAllParents(new OntologyTermImpl(cvParam.getAccession()));
            if (allParents != null) {
                for (OntologyTermI ontologyTermI : allParents) {
                    if (ontologyTermI.getTermAccession().equals("MS:1001471")) {
                        return new ArrayList<>();
                    }
                }
            }
            
            // check in MOD ontology
            Object term = modOntology.getTermForAccession(cvParam.getAccession());
            if (term == null) {
                // check in UNIMOD ontology
                term = unimodOntology.getTermForAccession(cvParam.getAccession());
            }
            else {
                return new ArrayList<>();
            }
            
            if (term == null) {
                messages.add(new ValidatorMessage(
                    "There is not a valid cvParam for the search modification  (id='"
                    + searchModification + "') element at "
                    + SearchModificationRule.SEARCHMOD_CONTEXT.getContext(),
                    MessageLevel.ERROR, SearchModificationRule.SEARCHMOD_CONTEXT, this));
            }
            else {
                return new ArrayList<>();
            }
        }

        return messages;
    }
}
